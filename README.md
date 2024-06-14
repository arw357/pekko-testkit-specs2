
A small library for those who use [pekko-testkit] in [specs2](http://etorreborre.github.io/specs2/) specifications. Provides idiomatic specs2 matchers for checking the correct reception of messages by test actors and probes, handling the provision and proper termination of test actor systems.

## Usage

To use pekko-testkit-specs2 in an existing SBT project with Scala 2.12 or 2.13, add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "org.arw357" %% "pekko-testkit-specs2" % "1.0.1-y1"
```

To use it in your specifications, just extend `PekkoSpecification`:

```scala
class MySpec extends PekkoSpecification {

  "MySpec" should {

    "provide a test actor and matchers for messages" in {
      testActor ! "hello"
      this must receive("hello")
    }
  }
}
```

If you can't extend classes in your specification, just mix-in the trait `PekkoSpecificationLike` (you just need to provide it an `ActorSystem`). If you only need the matchers, you can also mix-in directly `PekkoMatchers`. While `PekkoSpecification` and `PekkoSpecificationLike` only support mutable specifications, `PekkoMatchers` should work in immutable specifications too.

The testkit provides several ways to check messages received by a test actor or a test probe. This library provides expectations already existent in pekko-testkit `TestKitBase` in the form of specs2 `Matchers` with proper failure messages and an idiomatic syntax:

```scala
class MySpec extends PekkoSpecification {
  // testActor is not thread-safe; use `TestProbe` instances per example when possible!
  sequential

  "my test probe" should {

    "receive messages" in {
      testActor ! "hello" // expect any message
      this must receiveMessage

      testActor ! "hello" // expect a specific message
      this must receive("hello")

      testActor ! "hello" // expect a message of a given type
      this must receive[String]

      testActor ! "hello" // expect a message matching a function
      this must receive[String].which(_ must startWith("h"))

      testActor ! Some("hello") // expect a message matching a partial function
      this must receive.like {
        case Some(s: String) => s must startWith("h")
      }

      testActor ! "b" // expect several messages, possibly unordered
      testActor ! "a"
      this must receive.allOf("a", "b")

      testActor ! "b" // expect a message (possibly not the next one)
      testActor ! "a"
      this must receive("a").afterOthers

      testActor ! "hello" // expect a message with an explicit timeout
      this must receiveWithin(1.second)("hello")

      case class Envelope(msg: String) // unwrap a message before matching
      testActor ! Envelope("hello")
      this must receive[Envelope].unwrap(_.msg).which(_ must startWith("h"))

      testActor ! "hello" // ...and several combinations of the modifiers above
      this must receiveWithin(1.second)[String].which(_ must startWith("h")).afterOthers
    }
  }
}
```

## Pekko Typed Actors

If you're using the new [Typed Actor API] avalilable since Pekko 1.0.1, you can use `PekkoTypedSpecification`, `PekkoTypedSpecificationLike` and `PekkoTypedMatchers` instead of the traits above:

```scala
class MyTypedSpec extends PekkoTypedSpecification {

  "my typed test probe" should {

    "receive messages" in {
      val probe = testKit.createTestProbe[String]()

      probe.ref ! "hello" // expect any message
      probe must receiveMessage

      probe.ref ! "hello" // expect a specific message
      probe must receive("hello")

      // any of the following are type errors:
      // probe.ref ! 3
      // probe must receive(3)

      // (...)
    }
  }
}
```

## Copyright

Copyright (c) 2016-2020 Rui Gonçalves. See LICENSE for details.
