# Exercise 1: Build an Eventually Perfect Failure Detector
An Eventually Perfect Failure Detector (EPFD), in Kompics terms, is a component that provides the following port (already imported in the notebook).
```scala
class EventuallyPerfectFailureDetector extends Port {
 indication[Suspect];
 indication[Restore];
}
```
Simply put, your component should indicate or ‘deliver’ to the application the following messages:
```scala
case class Suspect(src: Address) extends KompicsEvent;
case class Restore(src: Address) extends KompicsEvent;
```
As you have already learnt from the course lectures, an EPFD, defined in a partially synchronous model, should satisfy the following properties:

Completeness: Every process that crashes should be eventually suspected permanently by every correct process
Eventual Strong Accuracy: No correct process should be eventually suspected by any other correct process
To complete this assignment you will have to fill in the missing functionality denoted by the commented sections below and pass the property checking test at the end of this notebook.
The recommended algorithm to use in this assignment is EPFD with Increasing Timeout and Sequence Numbers, which you can find at the second page of this document in the respective lecture.