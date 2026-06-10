package proxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import donnees_bloquees.InterfaceIncidents;
import database_service.ServiceDatabase;


class ProxyHandler implements HttpHandler {
  
  private Proxy proxy;
  
  public ProxyHandler(Proxy proxy) {
    this.proxy = proxy;
  }
  
  public void envoyerRequete(HttpExchange exchange, String response) throws IOException {// TODO : nouveau param pour renvoyer soit du JSON soit du texte (content-type) + nouveau param pour le code (400 si erreur)
    exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");// ajout du header content-type dans la reponse 
    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length); // code 200 (ok) + content length

    try (OutputStream os = exchange.getResponseBody()) { //exchange.getResponseBody() --> récupère le flux de sortie associé à la réponse HTTP. c’est dans ce flux qu’on écrit le contenu envoyé au client.
      os.write(response.getBytes(StandardCharsets.UTF_8)); // os.write --> écrit les octets de la chaîne response dans le flux de réponse. | response.getBytes(StandardCharsets.UTF_8) --> Convertit la chaîne de caractères en tableau d’octets en utilisant l’encodage UTF-8. 
    }
  }

  public void envoyerRequete(HttpExchange exchange, String response, int status) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
    exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);

    try (OutputStream os = exchange.getResponseBody()) { 
      os.write(response.getBytes(StandardCharsets.UTF_8));
    }
  }

  public void envoyerRequete(HttpExchange exchange, String response, boolean isJson) throws IOException {
    if (!isJson) exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
    else exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

    try (OutputStream os = exchange.getResponseBody()) { 
      os.write(response.getBytes(StandardCharsets.UTF_8));
    }
  }

  public void envoyerRequete(HttpExchange exchange, String response, int status, boolean isJson) throws IOException {
    if (!isJson) exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
    else exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);

    try (OutputStream os = exchange.getResponseBody()) { 
      os.write(response.getBytes(StandardCharsets.UTF_8));
    }
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
    
    String uri = String.valueOf(exchange.getRequestURI());
    if (uri.startsWith("/api")) {
      String endpoint = uri.substring(4);
      
      if (endpoint.startsWith("/bd")) { //TODO : faire un endpoint pour les coordonnées et un endpoint pour séservé (/bd/reserver/<restaurant>) 
        String endpoint = uri.substring(3);
        ServiceDatabase restaurant = this.proxy.getRestaurants();
        if (endpoint.startsWith("/restaurants")) {
          String jsonBd = restaurant.getCoordonneesRestaurants();
          envoyerRequete(exchange, jsonBd, true);
        } else if (endpoint.startsWith("/reserver")) {
          //recupérer les infos depuis les params POST de la requête
          
          //String jsonBd = restaurant.reserverTable();
          //envoyerRequete(exchange, jsonBd, true);
        } else {
          envoyerRequete(exchange, "erreur : endpoint non valide", 400);
        }
      } else if (endpoint.startsWith("/incidents")) {
        InterfaceIncidents incidents = this.proxy.getIncidents();
        String jsonBd = incidents.fetchAPIBloquee();
        envoyerRequete(exchange, jsonBd, true);
      } else {
        System.out.println("/api/bd/...  || /api/data/...");
        envoyerRequete(exchange, "erreur : endpoint non valide", 400);
      }
    } else {
      String response = "Reponse Test depuis le serveur HTTP";
      envoyerRequete(exchange, response);
    }
    
  }


}
