package demo;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class NumbersHttpApp extends AllDirectives {

  public static void main(String[] params) throws InterruptedException {
    new NumbersHttpApp().run();
  }

  void run() throws InterruptedException {
    Source<ByteString, NotUsed> stream = Source.range(0, 1000000)
      .throttle(5, new FiniteDuration(1, TimeUnit.SECONDS), 5, ThrottleMode.shaping())
      .map(i -> ByteString.fromString(i.toString() + "\n"));

    ActorSystem system = ActorSystem.create("server");
    Materializer materializer = ActorMaterializer.create(system);

    Http http = Http.get(system);

    Route route = path("numbers", () ->
      complete(HttpEntities.createCloseDelimited(
              ContentTypes.APPLICATION_OCTET_STREAM,
              stream))
    );

    http.bindAndHandle(route.flow(system, materializer),
            ConnectHttp.toHost("localhost", 8080),
            materializer);

    Thread.sleep(10000);
  }
}