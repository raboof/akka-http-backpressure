package demo;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.alpakka.ftp.FtpCredentials;
import akka.stream.alpakka.ftp.FtpSettings;
import akka.stream.alpakka.ftp.javadsl.Ftp;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletionStage;

public class FtpServerHttpApp extends AllDirectives {

  public static void main(String[] params) throws UnknownHostException, InterruptedException {
    //#ftpSettings
    FtpSettings ftpSettings = new FtpSettings(
      InetAddress.getLocalHost(),
      21,
      new FtpCredentials.NonAnonFtpCredentials("ftper", "nah5ev4E"),
      true,
      true
    );
    //#ftpSettings

    //new FtpServerHttpApp().runLs(ftpSettings);
    //new FtpServerHttpApp().runNaive(ftpSettings);
    new FtpServerHttpApp().runStreaming(ftpSettings);
  }


  void runLs(FtpSettings ftpSettings) throws UnknownHostException, InterruptedException {
    ActorSystem system = ActorSystem.create("server");
    Materializer materializer = ActorMaterializer.create(system);

    //#ls
    Ftp.ls("/home/ftper", ftpSettings)
      .runForeach(f -> System.out.println(f), materializer);
    Thread.sleep(1000);
    //#ls
  }

  void runNaive(FtpSettings ftpSettings) throws UnknownHostException, InterruptedException {
    ActorSystem system = ActorSystem.create("server");
    Materializer materializer = ActorMaterializer.create(system);
    Http http = Http.get(system);

    Route route = path(PathMatchers.segment("file").slash().concat(PathMatchers.segment()), filename -> {
      Source<ByteString, CompletionStage<IOResult>> source = Ftp.fromPath(filename, ftpSettings);
      CompletionStage<ByteString> result = source.toMat(Sink.reduce(ByteString::concat), Keep.right()).run(materializer);
      return onSuccess(() -> result, bytes -> complete(HttpEntities.create(bytes)));
    });

    http.bindAndHandle(
      route.flow(system, materializer),
      ConnectHttp.toHost("127.0.0.1", 8083),
      materializer
    );
  }

  void runStreaming(FtpSettings ftpSettings) {
    ActorSystem system = ActorSystem.create("server");
    Materializer materializer = ActorMaterializer.create(system);
    Http http = Http.get(system);

    Route route = path(PathMatchers.segment("file").slash().concat(PathMatchers.segment()), filename -> {
      //#streamResponseEntity
      Source<ByteString, CompletionStage<IOResult>> source =
        Ftp.fromPath(filename, ftpSettings);

      HttpEntity.CloseDelimited entity =
        HttpEntities.createCloseDelimited(
          ContentTypes.APPLICATION_OCTET_STREAM,
          source
        );

      return complete(HttpResponse.create().withEntity(entity));
      //#streamResponseEntity
    });

    http.bindAndHandle(
      route.flow(system, materializer),
      ConnectHttp.toHost("127.0.0.1", 8083),
      materializer
    );
  }
}
