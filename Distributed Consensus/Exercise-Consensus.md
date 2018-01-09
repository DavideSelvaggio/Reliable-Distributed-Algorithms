### Assignment 4: Distributed Consensus ###

In this final programming assignment for Part I of the course you will have to complete the implementation of a variation of the famous Paxos algorithm.

When you are done you simply have to export your notebook and then upload it in the "Programming Exercise 5" page.

**Things to Remember**:
1. Basic components such as `PerfectLink` and  `Best-Effort Broadcast` are already provided. No need to implement them.
2. Execute the imports defined below **before** compiling your component implementations.
3. We recommend making use of the component state and internal messages we have provided, if any, to complete the implementation logic.
4. You can always print messages to the output log, from within handlers to see what happens during the simulation. e.g. `println(s"Process $self delivers message $msg");`
5. Remember that during the simulation check you can print and observe the simulation time, i.e. with `System.currentTimeMillis()`.
5. Do not forget to run the checker code block after each component implementation to ensure that all properties are satisfied **before** exporting and submitting the notebook.
6. You can always restart the Kompics Interpreter to start fresh (Interpreter→KompicsInterpreter→Click Restart)

Good luck! :)


## Leader-less Obstraction-Free Paxos for Single Value Consensus ##

A (single value) Consensus Abstraction, in Kompics terms, is a component that **provides** the following port *(already imported in the notebook)*.
```scala
     class Consensus extends Port{
       request[C_Propose];
       indication[C_Decide];
     }
```
An **Consensus** component should request value proposals (`C_Propose`) and respond with decided value events (`C_Decide`) respectively as defined below:
```scala
     case class C_Decide(value: Any) extends KompicsEvent;
     case class C_Propose(value: Any) extends KompicsEvent;
```
The following properties define the expected behavior of a consensus abstraction more specifically:

1. **Validity**: *Only proposed values may be decided.*
2. **Uniform Agreement**: *No two nodes decide different values.*
3. **Integrity**: *Each node can decide a value at most once.*
4. **Termination**: *Every node eventually decides a value.*
    
The recommended algorithm to use is the the one we call "Leaderless Repeatable Paxos" which initiates new proposal rounds until a decision has been made.
