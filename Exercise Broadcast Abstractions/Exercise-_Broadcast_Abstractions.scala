import se.kth.edx.id2203.core.ExercisePrimitives._
import se.kth.edx.id2203.core.Ports._
import se.kth.edx.id2203.validation._
import se.sics.kompics.network._
import se.sics.kompics.sl.{Init, _}
import se.sics.kompics.{ComponentDefinition => _, Port => _, KompicsEvent}

import scala.collection.immutable.Set
import scala.collection.mutable.ListBuffer

class BasicBroadcast(init: Init[BasicBroadcast]) extends ComponentDefinition {

  //BasicBroadcast Subscriptions
  val pLink = requires[PerfectLink];
  val beb = provides[BestEffortBroadcast];

  //BasicBroadcast Component State and Initialization
  val (self, topology) = init match {
    case Init(s: Address, t: Set[Address]@unchecked) => (s, t)
  };

  //BasicBroadcast Event Handlers
  beb uponEvent {
    case x: BEB_Broadcast => handle {
        for (q <- topology) {
            trigger(PL_Send(q, x) -> pLink);
        }
    }
  }

  pLink uponEvent {
    case PL_Deliver(src, BEB_Broadcast(payload)) => handle {
     
        trigger(BEB_Deliver(src,payload) -> beb);
     
    }
  }
}

//Reliable Broadcast

class EagerReliableBroadcast(init: Init[EagerReliableBroadcast]) extends ComponentDefinition {
  //EagerReliableBroadcast Subscriptions
  val beb = requires[BestEffortBroadcast];
  val rb = provides[ReliableBroadcast];

  //EagerReliableBroadcast Component State and Initialization
  val self = init match {
    case Init(s: Address) => s
  };
  val delivered = collection.mutable.Set[KompicsEvent]();

  //EagerReliableBroadcast Event Handlers
  rb uponEvent {
    case x@RB_Broadcast(payload) => handle {

        trigger(BEB_Broadcast(RB_Broadcast(payload)) -> beb);
    }
  }

  beb uponEvent {
    case BEB_Deliver(src, y@RB_Broadcast(payload)) => handle {
    
    if (!delivered.contains(y.payload)){
        
        delivered += y.payload;
        trigger(RB_Deliver(src, y.payload) -> rb);
        trigger(BEB_Broadcast(RB_Broadcast(payload)) -> beb);
      }
    }
  }
}


//Causal Reliable Broadcast

case class DataMessage(timestamp: VectorClock, payload: KompicsEvent) extends KompicsEvent;

class WaitingCRB(init: Init[WaitingCRB]) extends ComponentDefinition {

  //WaitingCRB Subscriptions
  val rb = requires[ReliableBroadcast];
  val crb = provides[CausalOrderReliableBroadcast];

  //WaitingCRB Component State and Initialization
  val (self, vec) = init match {
    case Init(s: Address, t: Set[Address]@unchecked) => (s, VectorClock.empty(t.toSeq))
  };

  //  val V = VectorClock.empty(init match { case Init(_, t: Set[Address]) => t.toSeq })
  var pending: ListBuffer[(Address, DataMessage)] = ListBuffer();
  var lsn = 0;


  //WaitingCRB Event Handlers
  crb uponEvent {
    case x: CRB_Broadcast => handle {
     var W = VectorClock(vec);
     W.set(self, lsn);
     lsn = lsn + 1;
     trigger(RB_Broadcast((DataMessage(W, x.payload))) -> rb);
    /* trigger(RB_Broadcast(CRB_Broadcast(DataMessage(W, x.payload))) -> rb); */
    }
  }

  rb uponEvent {
    case x@RB_Deliver(src: Address, msg: DataMessage) => handle {
    
    pending += ((src, msg));
    while(pending.exists { p => p._2.timestamp<=vec }){
        for (p <- pending) {
            if (p._2.timestamp<=vec) {
                val (s,m) = p; 
                pending -= p;
                vec.inc(s);
                trigger(CRB_Deliver(s,m.payload) -> crb);
                }
            }
        }
    }
  }
}