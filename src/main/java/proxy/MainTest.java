/*
package proxy;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

class MainTest {

  public static void main(String[] args) {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
      server.createContext("/", new ProxyHandler()); // TODO : constructeur pas ok
      server.setExecutor(null);
      
      server.start();
    }catch (Exception e) {
      e.printStackTrace();
    }

  }

}
*/