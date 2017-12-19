import se.kth.edx.id2203.core.Ports._
import se.kth.edx.id2203.validation._
import se.sics.kompics.network._
import se.sics.kompics.sl.{Init, _}
import se.sics.kompics.timer.{ScheduleTimeout, Timeout, Timer}
import se.sics.kompics.{KompicsEvent, Start, ComponentDefinition => _, Port => _}

//Custom messages to be used in the internal component implementation
case class CheckTimeout(timeout: ScheduleTimeout) extends Timeout(timeout);

case class HeartbeatReply(seq: Int) extends KompicsEvent;
case class HeartbeatRequest(seq: Int) extends KompicsEvent;

//Define EPFD Implementation
class EPFD(epfdInit: Init[EPFD]) extends ComponentDefinition {

  //EPFD subscriptions
  val timer = requires[Timer];
  val pLink = requires[PerfectLink];
  val epfd = provides[EventuallyPerfectFailureDetector];

  // EPDF component state and initialization
  
  //configuration parameters
  val self = epfdInit match {case Init(s: Address) => s};
  val topology = cfg.getValue[List[Address]]("epfd.simulation.topology");
  val delta = cfg.getValue[Long]("epfd.simulation.delay");
  
  //mutable state
  var period = cfg.getValue[Long]("epfd.simulation.delay");
  var alive = Set(cfg.getValue[List[Address]]("epfd.simulation.topology"): _*);
  var suspected = Set[Address]();
  var seqnum = 0;

  def startTimer(delay: Long): Unit = {
    val scheduledTimeout = new ScheduleTimeout(period);
    scheduledTimeout.setTimeoutEvent(CheckTimeout(scheduledTimeout));
    trigger(scheduledTimeout -> timer);
  }

  //EPFD event handlers
  ctrl uponEvent {
    case _: Start => handle {
        period = cfg.getValue[Long]("epfd.simulation.delay");
        alive = Set(cfg.getValue[List[Address]]("epfd.simulation.topology"): _*);
        suspected = Set[Address]();
        seqnum = 0;
        startTimer(period);
    }
  }

  timer uponEvent {
    case CheckTimeout(_) => handle {
      if (!alive.intersect(suspected).isEmpty) {
          
        period = period + delta;
        
      }
      
      seqnum = seqnum + 1;
      
      for (p <- topology) {
        if (!alive.contains(p) && !suspected.contains(p)) {
            
          suspected = suspected + p;
          trigger(Suspect(p) -> epfd);
           
        } else if (alive.contains(p) && suspected.contains(p)) {
          suspected = suspected - p;
          trigger(Restore(p) -> epfd);
        }
        trigger(PL_Send(p, HeartbeatRequest(seqnum)) -> pLink);
      }
      alive = Set[Address]();
      startTimer(period);
    }
  }

  pLink uponEvent {
    case PL_Deliver(src, HeartbeatRequest(seq)) => handle {

      trigger(PL_Send(src, HeartbeatRequest(seq)) -> pLink);
      
    }
    case PL_Deliver(src, HeartbeatReply(seq)) => handle {

        if(seq == seqnum || suspected.contains(src)){
            alive = alive + src;
        }
        
    }
  }
};