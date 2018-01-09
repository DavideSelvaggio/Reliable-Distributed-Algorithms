import se.kth.edx.id2203.core.ExercisePrimitives.AddressUtils._
import se.kth.edx.id2203.core.Ports._
import se.kth.edx.id2203.validation._
import se.sics.kompics.network._
import se.sics.kompics.sl.{Init, _}
import se.sics.kompics.{KompicsEvent, ComponentDefinition => _, Port => _}
import scala.language.implicitConversions
import scala.collection.mutable.ListBuffer;

import se.sics.kompics.timer.{ScheduleTimeout, Timeout, Timer}

  case class Prepare(proposalBallot: (Int, Int)) extends KompicsEvent;
  case class Promise(promiseBallot: (Int, Int), acceptedBallot: (Int, Int), acceptedValue: Option[Any]) extends KompicsEvent;
  case class Accept(acceptBallot: (Int, Int), proposedValue: Any) extends KompicsEvent;
  case class Accepted(acceptedBallot: (Int, Int)) extends KompicsEvent;
  case class Nack(ballot: (Int, Int)) extends KompicsEvent;
  case class Decided(decidedValue: Any) extends KompicsEvent;

  /**
    * This augments tuples with comparison operators implicitly, which you can use in your code, for convenience. 
    * examples: (1,2) > (1,4) yields 'false' and  (5,4) <= (7,4) yields 'true' 
    */
  implicit def addComparators[A](x: A)(implicit o: math.Ordering[A]): o.Ops = o.mkOrderingOps(x);
  
  //HINT: After you execute the latter implicit ordering you can compare tuples as such within your component implementation:
  (1,2) <= (1,4);

class Paxos(paxosInit: Init[Paxos]) extends ComponentDefinition {

  //Port Subscriptions for Paxos

  val consensus = provides[Consensus];
  val beb = requires[BestEffortBroadcast];
  val plink = requires[PerfectLink];
 
  //Internal State of Paxos
  val (rank, numProcesses) = paxosInit match {
    case Init(s: Address, qSize: Int) => (toRank(s), qSize)
  }

  //Proposer State
  var round = 0;
  var proposedValue: Option[Any] = None;
  var promises: ListBuffer[((Int, Int), Option[Any])] = ListBuffer.empty;
  var numOfAccepts = 0;
  var decided = false;

  //Acceptor State
  var promisedBallot = (0, 0);
  var acceptedBallot = (0, 0);
  var acceptedValue: Option[Any] = None;

  def propose() = {
   /* 
   INSERT YOUR CODE HERE 
   */
  }

  consensus uponEvent {
    case C_Propose(value) => handle {
   /* 
   INSERT YOUR CODE HERE 
   */
    }
  }


  beb uponEvent {

    case BEB_Deliver(src, prep: Prepare) => handle {
   /* 
   INSERT YOUR CODE HERE 
   */
    };

    case BEB_Deliver(src, acc: Accept) => handle {
   /* 
   INSERT YOUR CODE HERE 
   */
    };

    case BEB_Deliver(src, dec : Decided) => handle {
   /* 
   INSERT YOUR CODE HERE 
   */
    }
  }

  plink uponEvent {

    case PL_Deliver(src, prepAck: Promise) => handle {
      if ((round, rank) == prepAck.promiseBallot) {
        /* 
           INSERT YOUR CODE HERE 
        */
      }
    };

    case PL_Deliver(src, accAck: Accepted) => handle {
      if ((round, rank) == accAck.acceptedBallot) {
        /* 
           INSERT YOUR CODE HERE 
        */
      }
    };

    case PL_Deliver(src, nack: Nack) => handle {
      if ((round, rank) == nack.ballot) {
        /* 
           INSERT YOUR CODE HERE 
        */
      }
    }
  }
  
 
};