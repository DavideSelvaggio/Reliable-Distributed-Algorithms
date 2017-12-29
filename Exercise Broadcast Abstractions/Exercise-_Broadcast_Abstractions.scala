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
      /*
      4: trigger h beb, Broadcast | [Data, self , m] i
      */
        /*trigger(BEB_Broadcast(x.payload) -> beb);*/
        trigger(BEB_Broadcast(RB_Deliver(self, payload)) -> beb);
    }
  }

  beb uponEvent {
    case BEB_Deliver(src, y@RB_Broadcast(payload)) => handle {
    
    if (!delivered.contains(payload)){
        delivered += payload;
        trigger(RB_Deliver(src, payload) -> rb);
        trigger(BEB_Broadcast(payload) -> beb);
    }
     /*       
      6: if m /∈ delivered then
      7: delivered := delivered ∪ {m}
      8: trigger h rb, Deliver | s, m i
      9: trigger h beb, Broadcast | [Data, s, m] i  
     */
     
    }
  }
}