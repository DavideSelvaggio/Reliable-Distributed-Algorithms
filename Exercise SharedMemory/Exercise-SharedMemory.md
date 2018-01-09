### Assignment 3: Implementing an Atomic N-N register ###
In this programming assignment you will have to complete the implementation of an atomic register that supports multiple writers and readers.

When you are done you simply have to export your notebook and then upload it in the “Programming Exercise 3” page.

__Things to Remember__:
1. Basic components such as PerfectLink, Network and Timer are already provided. No need to implement them.
2. Execute the imports defined below before compiling your component implementations.
3. We recommend making use of the component state and internal messages we have provided, if any, to complete the implementation logic.
4. You can always print messages to the output log, from within handlers to see what happens during the simulation. e.g. println(s"Process $self delivers message $msg");
5. Remember that during the simulation check you can print and observe the simulation time, i.e. with System.currentTimeMillis().
5. Do not forget to run the checker code block after each component implementation to ensure that all properties are satisfied before exporting and submitting the notebook.
6. You can always restart the Kompics Interpreter to start fresh (Interpreter→KompicsInterpreter→Click Restart)

Good luck! :)

## The N-N Atomic Register ##

A (single) Atomic Register Abstraction (AR), in Kompics terms,  is a component that **provides** the following port *(already imported in the notebook)*.
```scala
     class AtomicRegister extends Port {
       request[AR_Read_Request]
       request[AR_Write_Request]
       indication[AR_Read_Response]
       indication[AR_Write_Response]
     }
```

An **AR** component should request reads (`AR_Read_Request`) or writes (`AR_Write_Request`) and respond with `AR_Read_Response` or `AR_Write_Response` events respectively as defined below:
```scala
     case class AR_Read_Request() extends KompicsEvent
     case class AR_Read_Response(value: Option[Any]) extends KompicsEvent
     case class AR_Write_Request(value: Any) extends KompicsEvent
     case class AR_Write_Response() extends KompicsEvent
```

As you have already learnt from the course lectures, Atomic Registers should be linerarizable and also terminate which we summarize with the following properties:

1. **Termination**: *If a correct process invokes an operation, then the operation eventually completes.*
2. **Atomicity**: *Every read operation returns the value that was written most recently in a hypothetical execution, where every failed operation appears to be complete or does not appear to have been invoked at all, and every complete operation appears to have been executed at some instant between its invocation and its completion.*