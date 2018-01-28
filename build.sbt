scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "com.typesafe.akka" %% "akka-http" % "10.1.0-RC1",

  "com.lightbend.akka" %% "akka-stream-alpakka-ftp" % "0.16",

  "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.0" % "test",
  "io.gatling"            % "gatling-test-framework"    % "2.3.0" % "test",
)

enablePlugins(GatlingPlugin)

enablePlugins(ParadoxPlugin)
paradoxTheme := None
paradoxDirectives += {
  import com.lightbend.paradox.markdown.Writer.Context
  import com.lightbend.paradox.markdown.WrapDirective

  (_: Context) => new WrapDirective("section") {
    import org.pegdown.ast._
    import org.pegdown.Printer

    override def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
      val id =
        node.attributes.identifier match {
          case null => ""
          case x    => s""" id="$x""""
        }
      val classes =
        node.attributes.classesString match {
          case "" => ""
          case x  => s""" class="$x""""
        }
      import scala.collection.JavaConverters._
      val attrs = node.attributes.keys.asScala.map(key => s""" $key="${node.attributes.value(key)}"""").mkString
      printer.print(s"""<$typ$id$classes$attrs>""")
      node.contentsNode.accept(visitor)
      printer.print(s"</$typ>")
    }
  }
}

paradoxDirectives += {
  import com.lightbend.paradox.markdown.Writer.Context
  import com.lightbend.paradox.markdown.WrapDirective

  (_: Context) => new WrapDirective("section") {
    import org.pegdown.ast._
    import org.pegdown.Printer

    override def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
      val id =
        node.attributes.identifier match {
          case null => ""
          case x    => s""" id="$x""""
        }
      val classes =
        node.attributes.classesString match {
          case "" => ""
          case x  => s""" class="$x""""
        }
      import scala.collection.JavaConverters._
      val attrs = node.attributes.keys.asScala.map(key => s""" $key="${node.attributes.value(key)}"""").mkString
      printer.print(s"""<$typ$id$classes$attrs>""")
      node.contentsNode.accept(visitor)
      printer.print(s"</$typ>")
    }
  }
}

paradoxDirectives += {
  import com.lightbend.paradox.markdown.Writer.Context
  import com.lightbend.paradox.markdown.InlineDirective

  (_: Context) => new InlineDirective("notes") {
    import org.pegdown.ast._
    import org.pegdown.Printer

    override def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
      printer.print(s"""<aside class="notes">""")
      node.contentsNode.accept(visitor)
      printer.print(s"</aside>")
    }
  }
}
