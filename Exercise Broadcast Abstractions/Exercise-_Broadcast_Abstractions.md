# Assignment 3

From Best Effort to Causal Order Reliable Broadcast
This is the first programming assignment where you will have to build and reuse multiple components.
Starting bottom-up, you will first have to implement best effort broadcast, then reliable broadcast and finally causal order reliable broadcast.
Mind that passing each component check will give you a partial grade and therefore you will need to pass all checks to get the full grade for this programming assignment.

Things to Remember:
1. Some components such as PerfectLink, Network and Timer are already provided. No need to implement them.
2. Execute the imports defined below before compiling your component implementations.
3. We recommend making use of the component state and internal messages we have provided, if any, to complete the implementation logic.
4. You can always print messages to the output log, from within handlers to see what happens during the simulation. e.g. println(s"Process $self delivers message $msg");
5. Remember that during the simulation check you can print and observe the simulation time, i.e. with System.currentTimeMillis().
5. Do not forget to run the checker code block after each component implementation to ensure that all properties are satisfied before exporting and submitting the notebook.
6. You can always restart the Kompics Interpreter to start fresh (Interpreter→KompicsInterpreter→Click Restart)
7. Do not submit just the grading token to edx. You will have to submit the whole exported notebook content to get your grade.

Good luck! :)

# Part I: Best-Effort Broadcast
A Best-Effort Broadcast Abstraction (BEB), in Kompics terms, is a component that provides the following port (already imported in the notebook).
```scala
class BestEffortBroadcast extends Port {
 indication[BEB_Deliver];
 request[BEB_Broadcast];
}
```
A __BEB__ component should request BEB_Broadcast and indicate BEB_Deliver events as defined below:
```scala
 case class BEB_Deliver(source: Address, payload: KompicsEvent) extends KompicsEvent;
 case class BEB_Broadcast(payload: KompicsEvent) extends KompicsEvent;
```
As you have already learnt from the course lectures, Best-Effort Broadcast should satisfy the following properties:

- __Validity__: If a correct process broadcasts a message m, then every correct process eventually delivers m.
- __No duplication__: No message is delivered more than once.
- __No creation__: If a process delivers a message m with sender s, then m was previously broadcast by process s.


# Part II: Reliable Broadcast
A Reliable Broadcast Abstraction (RB), in Kompics terms, is a component that provides the following port (already imported in the notebook).
```scala
class ReliableBroadcast extends Port {
  indication[RB_Deliver];
  request[RB_Broadcast];
}
```
An __RB__ component should request RB_Broadcast and indicate RB_Deliver events, as defined below:
```scala
case class RB_Deliver(source: Address, payload: KompicsEvent) extends KompicsEvent;
case class RB_Broadcast(payload: KompicsEvent) extends KompicsEvent;
```
As you have already learnt from the course lectures, Reliable Broadcast adds the Agreement property into the already existing properties of Best-Effort Broadcast:

- Validity: If a correct process broadcasts a message m, then every correct process eventually delivers m.
- No duplication: No message is delivered more than once.
- No creation: If a process delivers a message m with sender s, then m was previously broadcast by process s.
-__Agreement__: If a message m is delivered by some correct process, then m is eventually delivered by every correct process.

Mind that, to complete this part, you will first have to implement and test Best-Effort Broadcast, defined above.

# Part III: Causal-Order Reliable Broadcast
A Causal-Order Reliable Broadcast Abstraction (CRB), in Kompics terms, is a component that __provides__ the following port (already imported in the notebook).
```scala
class CausalOrderReliableBroadcast extends Port {
  indication[CRB_Deliver];
  request[CRB_Broadcast];
}
```
A __CRB__ component should request CRB_Broadcast and indicate CRB_Deliver events, as defined below:
```scala
case class CRB_Deliver(src: Address, payload: KompicsEvent) extends KompicsEvent;
case class CRB_Broadcast(payload: KompicsEvent) extends KompicsEvent;
```
As you have already learnt from the course lectures, Causal-Order Reliable Broadcast adds the Causal Delivery property into the already existing properties of Reliable and Best-Effort Broadcast:

- Validity: If a correct process broadcasts a message m, then every correct process eventually delivers m.
- No duplication: No message is delivered more than once.
- No creation: If a process delivers a message m with sender s, then m was previously broadcast by process s.
- Agreement: If a message m is delivered by some correct process, then m is eventually delivered by every correct process.
- __Causal delivery__: For any message m1 that potentially caused a message m2, i.e., m1 → m2, no process delivers m2 unless it has already delivered m1.

Also mind, that to complete this part, you will first have to implement and test Best-Effort Broadcast and Reliable Broadcast, defined above.

Working with Vector Clocks
We have already provided you a VectorClock data structure (already imported) to aid you with the algorithm implementation. You can briefly see the supported operations below:
```scala
    case class VectorClock(var vc: Map[Address, Int]) {
        def inc(addr: Address) : Unit  //increases the clock corresponding to the address @addr provided
        def set(addr: Address, value: Int) : Unit //sets the clock of @addr to @value
        def <=(that: VectorClock): Boolean   //returns true if this vector clock instance is lower or equal to @that
    }
    object VectorClock {
        def empty(topology: scala.Seq[Address]): VectorClock //generates a vector clock that has an initial clock value of 0 for each address in the @topology provided
        def apply(that: VectorClock): VectorClock //copy constructor of a vector clock. E.g. if vc1 is a vector clock vc2 = VectorClock(vc1) is a copy of vc1
    }
```