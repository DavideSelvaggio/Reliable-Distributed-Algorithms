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
A _BEB_ component should request BEB_Broadcast and indicate BEB_Deliver events as defined below:
```scala
 case class BEB_Deliver(source: Address, payload: KompicsEvent) extends KompicsEvent;
 case class BEB_Broadcast(payload: KompicsEvent) extends KompicsEvent;
```
As you have already learnt from the course lectures, Best-Effort Broadcast should satisfy the following properties:

- _Validity_: If a correct process broadcasts a message m, then every correct process eventually delivers m.
- _No duplication_: No message is delivered more than once.
- _No creation_: If a process delivers a message m with sender s, then m was previously broadcast by process s.
HINT: The recommended algorithm to use in this assignment is Basic Broadcast and is described in the following document in the respective lecture.