package sc.demo

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity.ChunkStreamPart
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.Uri.Path.Segment
import akka.http.scaladsl.server.{Directives, PathMatchers}
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.alpakka.ftp.scaladsl.Ftp
import akka.stream.alpakka.ftp.{FtpCredentials, FtpSettings}
import akka.stream.scaladsl.{Keep, Sink}
import akka.util.ByteString

object FtpServerHttpApp extends App with Directives {
  val ftpSettings = FtpSettings(
    InetAddress.getLocalHost,
    port = 21,
    FtpCredentials.NonAnonFtpCredentials("ftper", "nah5ev4E"),
    binary = true,
    passiveMode = true)

  def runLs(): Unit = {
    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()

    Ftp.ls("/home/ftper", ftpSettings)
        .runForeach(println)
  }

  def runNaive(): Unit = {
    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()

    //#reduceResponseEntity
    val route = path("file" / Segment) { filename => {
      val source = Ftp.fromPath(filename, ftpSettings)
      val result = source.toMat(Sink.reduce[ByteString](_ ++ _))(Keep.right).run()
      complete(result)
    }}
    //#reduceResponseEntity

    Http().bindAndHandle(route, "127.0.0.1", 8083)
  }

  def runStreaming(): Unit = {
    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()

    //#streamResponseEntity
    val route = path("file" / Segment) { filename => {
      val source = Ftp.fromPath(filename, ftpSettings)

      complete(HttpEntity.Chunked(
        ContentTypes.`application/octet-stream`,
        source.map(bytes ⇒ ChunkStreamPart(bytes))))
    }}
    //#streamResponseEntity

    Http().bindAndHandle(route, "127.0.0.1", 8083)
  }

  runNaive()
}
