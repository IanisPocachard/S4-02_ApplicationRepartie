import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


class ProxyHandler implements HttpHandler {
  
  private Proxy proxy;
  
  public ProxyHandler(Proxy proxy) {
    this.proxy = proxy;
  }
  
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    
    //pour l'instant j'affiche juste la requête du client | TODO --> call rmi en fonction de la requête du client.
    String clientAddress = exchange.getRemoteAddress().toString();

    System.out.println("-----"+clientAddress+"-----");
    System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getProtocol());

    for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
        for (String value : entry.getValue()) {
            System.out.println(entry.getKey() + ": " + value);
        }
    }
    
    String uri = exchange.getRequestURI();
    if (uri.startsWith("/api")) {
      String endpoint = uri.substring(4);
      
      if (endpoint.startsWith("/bd")) {
        //appel rmi bd
      } else if (endpoint.startsWith("/data") {
        //appel rmi données bloquées
      } else {
        System.out.println("/api/bd/...  || /api/data/...");
      }
    }


    //reponse du serveur web
    String response = "Reponse Test depuis le serveur HTTP";

    exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");// ajout du header content-type dans la reponse 
    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length); // code 200 (ok) + content length

    try (OutputStream os = exchange.getResponseBody()) { //exchange.getResponseBody() --> récupère le flux de sortie associé à la réponse HTTP. c’est dans ce flux qu’on écrit le contenu envoyé au client.
      os.write(response.getBytes(StandardCharsets.UTF_8)); // os.write --> écrit les octets de la chaîne response dans le flux de réponse. | response.getBytes(StandardCharsets.UTF_8) --> Convertit la chaîne de caractères en tableau d’octets en utilisant l’encodage UTF-8. 
    }
  }


}
