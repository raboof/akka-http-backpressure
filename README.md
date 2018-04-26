# reactive-streams-demo

This repo contains all the resources associated with my Reactive Streams talk.

There is a Scala and a Java version of the slides for this talk.
The Scala version can be found at https://raboof.github.io/akka-http-backpressure/.

## Generating

`sbt paradox` will generate the presentation and place it in
`target/paradox/main/site`

The presentation itself is a [paradox](https://github.com/lightbend/paradox)
markdown file in `src/main/paradox/index.md`. Code snippets are taken from
`src/main/java` and `src/main/scala`.

The paradox theme in `src/main/paradox/_theme` formats the output as a
[reveal.js](https://revealjs.com) HTML presentation. This could be split out
as a separate deliverable if it proves useful. The reveal.js theme in
`src/main/paradox/_template/css/theme/akka.css adds some Akka fonts and colors.

This approach has been inspired by the excellent
[tut](https://github.com/tpolecat/tut). The main advantage of paradox is that
it supports snippets from files in other languages than Scala, and makes it
easy to maintain code snippets in an IDE. The advantage of tut is that it
allows including not just the code, but also the output that code would give
on the REPL.
