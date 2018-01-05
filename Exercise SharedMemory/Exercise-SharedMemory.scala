//Rremember to execute the following imports first
import se.kth.edx.id2203.core.ExercisePrimitives._
import se.kth.edx.id2203.core.Ports._
import se.kth.edx.id2203.validation._
import se.sics.kompics.network._
import se.sics.kompics.sl.{Init, _}
import se.sics.kompics.{ComponentDefinition => _, Port => _,KompicsEvent}

import scala.collection.mutable.Map
import scala.language.implicitConversions


  //The following events are to be used internally by the Atomic Register implementation below
  case class READ(rid: Int) extends KompicsEvent;
  case class VALUE(rid: Int, ts: Int, wr: Int, value: Option[Any]) extends KompicsEvent;
  case class WRITE(rid: Int, ts: Int, wr: Int, writeVal: Option[Any]) extends KompicsEvent;
  case class ACK(rid: Int) extends KompicsEvent;

  /**
    * This augments tuples with comparison operators implicitly, which you can use in your code. 
    * examples: (1,2) > (1,4) yields 'false' and  (5,4) <= (7,4) yields 'true' 
    */
  implicit def addComparators[A](x: A)(implicit o: math.Ordering[A]): o.Ops = o.mkOrderingOps(x);

//HINT: After you execute the latter implicit ordering you can compare tuples as such within your component implementation:
(1,2) <= (1,4);



class ReadImposeWriteConsultMajority(init: Init[ReadImposeWriteConsultMajority]) extends ComponentDefinition {

  //subscriptions

  val nnar = provides[AtomicRegister];

  val pLink = requires[PerfectLink];
  val beb = requires[BestEffortBroadcast];

  //state and initialization

  val (self: Address, n: Int, selfRank: Int) = init match {
    case Init(selfAddr: Address, n: Int) => (selfAddr, n, AddressUtils.toRank(selfAddr))
  };

  var (ts, wr) = (0, 0);
  var value: Option[Any] = None;
  var acks = 0;
  var readval: Option[Any] = None;
  var writeval: Option[Any] = None;
  var rid = 0;
  var readlist: Map[Address, (Int, Int, Option[Any])] = Map.empty
  var reading = false;

  //handlers

  nnar uponEvent {
    case AR_Read_Request() => handle {
      rid = rid + 1;
      
        /* WRITE YOUR CODE HERE  */
     
    };
    case AR_Write_Request(wval) => handle { 
      rid = rid + 1;
         
        /* WRITE YOUR CODE HERE  */
     
    }
  }

  beb uponEvent {
    case BEB_Deliver(src, READ(readID)) => handle {
        
     /* WRITE YOUR CODE HERE  */
     
    }
    case BEB_Deliver(src, w: WRITE) => handle {
       
     /* WRITE YOUR CODE HERE  */
     
    }
  }

  pLink uponEvent {
    case PL_Deliver(src, v: VALUE) => handle {
      if (v.rid == rid) {
         
      /* WRITE YOUR CODE HERE  */
     
      }
    }
    case PL_Deliver(src, v: ACK) => handle {
      if (v.rid == rid) {
  
      /* WRITE YOUR CODE HERE  */
     
      }
    }
  }
}