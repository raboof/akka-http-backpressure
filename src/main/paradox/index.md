@@@section { data-background="#15a9ce" }

### Handle Intense Workloads with Backpressure and Reactive Streams

##### Arnout Engelen

@@@

@@@section

Agenda:

1. Reactive Streams
1. Akka Streams
1. Akka HTTP
1. Demo!

@notes[
What to expect<br>
* history, concepts and scope of reactive streams<br>
* Close look at main concepts of one RS impl: Akka Streams<br>
* how Akka Streams is really at the core of the Akka HTTP lib<br>
* A 'live' example of an Akka HTTP application leveraging streams
]

@@@

@@@section

# 1. Reactive Streams

@notes[So let's start with Reactive Streams. To better understand the reactive streams initiative it makes sense to look at some history of there this effort came from]

@@@

@@@section

![Akka logo](akka_full_color.svg)

Open Source toolkit for building Concurrent, Distributed, Resilient Message-Driven applications on the JVM

@notes[Akka grew out of the realization, circa 2010, that threads are a heavyweight abstraction that is hard to make resilient. Inspired by Erlang, asynchronous model, actor model, but not covering that today.]

[TODO] small example: threaded code, explain many calls = many threads, actor alternative, explain resiliency by explaining the actor is in a better position to deal with problems compared to having an exception in each thread


@@@

@@@section

### Traditional synchonous model

@@snip[Synchronous.java]($root$/src/main/java/rs/async/Synchronous.java){#service}

@@snip[Synchronous.java]($root$/src/main/java/rs/async/Synchronous.java){#perform}

@notes[As a refresher on the advantages of async code I have a tiny example. This should look familiar to most of you, right? a number of tasks are performed by a service]

@@@

@@@section

### Asynchonous approach

@@snip[x]($root$/src/main/java/rs/async/Asynchronous.java){#actor}

@@snip[x]($root$/src/main/java/rs/async/Asynchronous.java){#enqueue}

@notes[Same trivial snippet in Actor. Main diff: task in mailbox. Saves threads.<br>
Resilience. Back a slide, highlight the sending thread no longer has to deal with the exception<br>
Coordination also much easier when using message passing<br>
Nothing new: smalltalk, erlang<br>
Note: there are of course many ways to make a system asynchonous, and I'm sure many of you have introduced asynchonous boundaries on your own perhaps even with using a library. Actors are just a particularly nice way to achieve it.]

@@@

@@@section

## Akka toolkit

Actor model

@span[Clustering]{ .fragment }

@span[Persistence (Event Sourcing)]{ .fragment }

@span[HTTP]{ .fragment } 

@span[[Reactive Manifesto](https://www.reactivemanifesto.org)]{ .fragment }

@notes[So the actor is the basic building block in Akka, but the library contains more: it turned out that the abstractions for concurrency and resiliency in the actor model lend themselves very well for modelling distributed systems, so Akka grew modules for clustering, persistence, and a HTTP stack.<br>Excellent fit for building systems that have the properties of the Reactive Manifesto]

@@@

@@@section

### So far the good news...

@@snip[x]($root$/src/main/java/rs/async/Asynchronous.java){#actor}

@@snip[x]($root$/src/main/java/rs/async/Asynchronous.java){#enqueue}

@notes[Akka is by no means the only approach to asynchonous programming: Node.js, RxJava for example also exploring the same space. And they all ran into the problem of message targets not being able to keep up. Not impossible to solve, but solutions (e.g. ack'ing etc) ad hoc and not composable. This lead to a number of players in industry identifying the need for an interoperable mechanism to get asynchronous, backperssured streams: the Reactive Streams initiative]

@@@

@@@section { data-background="#489ebd" }

### Reactive Streams Timeline

![Reactive Streams Timeline](reactive_streams_timeline.png)

@notes[Industry got together under Reactive Streams umbrella (not limited to JVM)]

@@@

@@@section { data-background="#489ebd" }

### Reactive Streams Scope

Reactive Streams is an initiative to provide a standard for asynchronous stream processing with non-blocking back pressure. This encompasses efforts aimed at runtime environments (JVM and JavaScript) as well as network protocols

http://www.reactive-streams.org

@@@

@@@section

### `java.util.concurrent.Flow`

@@snip[rs]($root$/src/main/java/rs/Publisher.java) { #rs }
@@snip[rs]($root$/src/main/java/rs/Subscriber.java) { #rs }
@@snip[rs]($root$/src/main/java/rs/Subscription.java) { #rs }
@@snip[rs]($root$/src/main/java/rs/Processor.java) { #rs }

@notes[Most significant milestone was the inclusion of the RS interfaces in JDK9
If you're not on JDK9 you can use the org.reactivestreams library.]

@@@

@@@section

### Availablility

Included in JDK9

No JDK9? No problem!

```
<dependency>
  <groupId>org.reactivestreams</groupId>
  <artifactId>reactive-streams</artifactId>
  <version>1.0.2</version>
</dependency>
```

@@@

@@@section

### Specifications

* 11-17 subtle requirements each
* Technology Compatibility Kit (TCK)

@notes[simplicity is deceptive: TCK for conformance checking. Originally planned: both user-facing API and an interoperability API. User-facing scrapped ~2014 so libraries can provide their own, ideomatic impl.]

@@@

@@@section

# 2. Akka Streams

@notes[Before we start: hands up Java/Scala experience? Will show a bit of both.]

[TODO] introduce source, flows, sinks, perhaps tell async/backpressure story again

@@@

@@@section

![Source, Flow and Sink](stream-blocks.svg)

@@@

@@@section

@@snip[x]($root$/src/main/java/streams/Intro.java){#source-no}
@@snip[x]($root$/src/main/java/streams/Intro.java){#flow-no group="x fragment"}
@@snip[x]($root$/src/main/java/streams/Intro.java){#sink-no group="x fragment"}

@notes[Before we dive into the specifics, let's start with some small examples to get a feel]

@@@

@@@section

@@snip[x]($root$/src/main/java/streams/Intro.java){#source}
@@snip[x]($root$/src/main/java/streams/Intro.java){#flow}
@@snip[x]($root$/src/main/java/streams/Intro.java){#sink}
@@snip[x]($root$/src/main/java/streams/Intro.java){#graph group="x fragment"}
@@snip[x]($root$/src/main/java/streams/Intro.java){#run group="x fragment"}

@@@

@@@section

@@snip[x]($root$/src/main/scala/sstreams/Intro.scala){#source}
@@snip[x]($root$/src/main/scala/sstreams/Intro.scala){#flow}
@@snip[x]($root$/src/main/scala/sstreams/Intro.scala){#sink}
@@snip[x]($root$/src/main/scala/sstreams/Intro.scala){#graph group="x fragment"}
@@snip[x]($root$/src/main/scala/sstreams/Intro.scala){#run group="x fragment"}

@@@

@@@section

@@snip[x]($root$/src/main/java/streams/Intro.java){#short}

@@snip[x]($root$/src/main/scala/sstreams/Intro.scala){#short}

@notes[we really try to make the API really easy to use for Java and Scala]


@@@

@@@section

## Materialization

@@snip[x]($root$/src/main/java/streams/Materialization.java){#multiple}

@notes[running = 2-stage: build graph, run graph. run = materialization. Same graph can be materialized multiple times.]

@@@

@@@section

## Materialized values

@@snip[x]($root$/src/main/java/streams/Materialization.java){#source}

@@snip[x]($root$/src/main/java/streams/Materialization.java){#sink group="x fragment"}

[TODO] use Sink.reduce as example for Sink materialization

@note[Streams and actors go well together, default left]

@@@

@@@section

## Actor materialization

@@snip[x]($root$/src/main/java/streams/Materialization.java){#fusing}

![Source, Flow and Sink](stream-unfused.svg)

@notes[Advantage of 2-phase: reusable building blocks, but opportunity for optimizations at materialization time]

@@@

@@@section

# Fusing

@@snip[x]($root$/src/main/java/streams/Materialization.java){#fusing-explicit-async}

![Source, Flow and Sink](stream-async.svg)

@@@

@@@section

# Fusing

@@snip[x]($root$/src/main/java/streams/Materialization.java){#fusing-async}

![Source, Flow and Sink](stream-async.svg)

@@@

@@@section

## Backpressure propagation

Demand is signalled across async boundaries

![Backpressure across async boundary](backpressure-propagation.svg)

@@@

@@@section

## Backpressure propagation

Thanks to Reactive Streams, across different libraries:

![Backpressure across async boundary](backpressure-propagation-across-libs.svg)

@@@

@@@section

## Backpressure propagation

Often also possible across external protocols, i.e. TCP:

![TCP window](tcp-window-initial.png)

@span[![TCP window](tcp-window-1.png)]{.fragment}

@@@

@@@section

## Backpressure propagation

Can be seen in e.g. wireshark:

![Wireshark backpressure](wireshark-fullwindow-2.png)

@notes[and keeps heartbeating]

@@@

@@@section

## Live demo time!

@notes[TCP server from scratch, throttling and backpressure demo, with netcat and showing the send/recv queues in ss]

@@@

@@@section

# 3. Akka HTTP

@notes[So far: might seem lowlevel, but usable to build higher-level abstractions, i.e. Akka HTTP. Will show some of the API, highlighting how Akka streams is the foundation]

@@@

@@@section

## Binding

@@snip[x]($root$/src/main/java/http/Basics.java){#init}
@@snip[x]($root$/src/main/java/http/Basics.java){#bind group="x fragment"}
@@snip[x]($root$/src/main/java/http/Basics.java){#run group="x fragment"}
@@snip[x]($root$/src/main/java/http/Basics.java){#log group="x fragment"}

@@@

@@@section

## Connection

Is a `Flow[Response, Request, _]`:

@@snip[x]($root$/src/main/java/http/Basics.java){#flow}

@span[Or provide your `Flow[Request, Response, _]`:]{.fragment}

@@snip[x]($root$/src/main/java/http/Basics.java){#handleWith group="x fragment"}

@@@

@@@section

[TODO] Simple minimal Akka HTTP application, some slides with additional features?

[TODO] Number of slides showing how the basic foundations of the Akka HTTP API is built on building blocks that should be familiar from the Akka Streams section. Both the low-level bind() and the request and response entities, tied together by the DSL.

@@@

@@@section

# Demo

[TODO] Explain the context of the demo: a HTTP API for fetching files that are stored on a back-end FTP server

@@@

@@@section

# Demo

[TODO] First: fetching things from FTP. Introduce Alpakka. (Maybe move this demo to the Akka Streams section as a form of foreshadowing?)

@@@

@@@section

# Demo

[TODO] Naive implementation with `Sink.reduce(_ + _)`

@@@

@@@section

# Demo

[TODO] Interaction moment: who spotted the security issue?

@@@

@@@section

# Demo

[TODO] Overload it, show in VisualVM

@@@

@@@section

# Demo

[TODO] Stream the entity

@@@

@@@section

# Re-cap

[TODO] Backpressure protects against overloading the target of an asynchronous non-blocking stream. Reactive Streams makes this work end-to-end, use a library like Akka Streams, Akka HTTP is built on top of Akka Streams and benefits from it.

@@@

@@@section

# Call to action

[TODO] Where to download slides, Lightbend quickstarts, where to find the community

@notes[And talk to me afterwards :)]

@@@
