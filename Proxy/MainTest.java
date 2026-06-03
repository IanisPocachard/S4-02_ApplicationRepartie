import com.sum.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

class MainTest {

  public static void main(String[] args) {
    try {
      HttpServeur server = HttpServer.create(new InetSocketAddress(8080), 0);
      server.createContext("/", new ProxyHandler());
      server.setExecutor(null);
      
      server.start();
    }catch (Exception e) {
      e.printStackTrace();
    }

  }

}
