package demo;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.ftp.FtpCredentials;
import akka.stream.alpakka.ftp.FtpSettings;
import akka.stream.alpakka.ftp.javadsl.Ftp;

import java.net.InetAddress;

public class FtpServerHttpApp extends AllDirectives {

  public static void main(String[] params) throws Exception {
    ActorSystem system = ActorSystem.create("server");
    Materializer materializer = ActorMaterializer.create(system);
    Http http = Http.get(system);

    FtpSettings ftpSettings = new FtpSettings(
            InetAddress.getLocalHost(),
            20,
            new FtpCredentials.NonAnonFtpCredentials("ftper", "nah5ev4E"),
            true,
            true
    );

    Ftp.ls("localhost", ftpSettings)
      .runForeach(f -> System.out.println(f), materializer);

  }
}
