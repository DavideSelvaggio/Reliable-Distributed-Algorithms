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
      acks = 0;
      readlist = Map.empty;
      reading = true;
      println(s"($self) READ: BEB_Broadcasting -> rid: $rid reading: $reading");
      trigger(BEB_Broadcast(READ(rid)) -> beb);
    };
    case AR_Write_Request(wval) => handle { 
      rid = rid + 1;
      /*writeval = wval;*/
      writeval = Some(wval);
      acks = 0;
      readlist = Map.empty;
      trigger(BEB_Broadcast(READ(rid)) -> beb);
      println(s"($self) WRITE: BEB_Broadcasting -> rid: $rid");
    }
  }

  beb uponEvent {
    case BEB_Deliver(src, READ(readID)) => handle {
      trigger(PL_Send(src, VALUE(readID, ts, wr, value)) -> pLink);
      println(s"($self) Sendind VALUE form $src -> rid: $readID timestamp: $ts wr: $wr value: $value");
    }
    case BEB_Deliver(src, w: WRITE) => handle {
      if ((ts, wr)<(w.ts, w.wr)){
        /*(ts, wr, value) = (w.ts, w.wr, w.writeVal)*/
        ts = w.ts;
        wr = w.wr;
        value = w.writeVal;
      }
      trigger(PL_Send(src, ACK(w.rid)) -> pLink);
      println(s"($self) Sendind ACK form $src -> rid: $rid");
    }
  }

  pLink uponEvent {
    case PL_Deliver(src, v: VALUE) => handle {
    println(s"($self) PL_Deliver VALUE form  $src with acks: $acks and reading: $reading");
      if (v.rid == rid) {
        /*val myVal: Option[Any] = v.value;*/
        readlist.update(src,(v.ts, v.wr, v.value));
        if(readlist.size > n/2){
          println(readlist.values);
          var maxts = readlist.maxBy(maplist => (maplist._2._1, maplist._2._2))._2._1;
          var rr= readlist.maxBy(maplist => (maplist._2._1, maplist._2._2))._2._2;
          readval = readlist.maxBy(maplist => (maplist._2._1, maplist._2._2))._2._3;
          println(s"$maxts $rr $readval");
          readlist = Map.empty;
          var bcastval: Option[Any] = None;
          if(reading){
            bcastval = readval;
          } else {
            rr = selfRank;
            maxts = maxts + 1;
            bcastval = writeval;
          }
          trigger(BEB_Broadcast(WRITE(rid, maxts, rr, bcastval)) -> beb);
          println(s"($self) BEB_Broadcast WRITE -> $rid, $maxts, $rr, $bcastval");
        }
      }
    }
    case PL_Deliver(src, v: ACK) => handle {
    println(s"($self) PL_Deliver ACK from  $src with acks: $acks and reading: $reading");
      if (v.rid == rid) {
        acks = acks + 1;
        println(s"($self) acks: $acks");
        println(s"($self) reading: $reading");
        if(acks > n/2){
          acks = 0;
          if (reading){
            reading = false;
            trigger(AR_Read_Response(readval) -> nnar);
            println(s"($self) AR_Read_Response  -> $readval");
          } else {
            trigger(AR_Write_Response() -> nnar);
            println(s"($self) AR_Write_Response");
          }
        }
      }
    }
  }
}