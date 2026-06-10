package proxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import donnees_bloquees.InterfaceIncidents;
import database_service.ServiceDatabase;


class ProxyHandler implements HttpHandler {
  
  private Proxy proxy;
  
  public ProxyHandler(Proxy proxy) {
    this.proxy = proxy;
  }
  
  public void envoyerRequete(HttpExchange exchange, String response) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");// ajout du header content-type dans la reponse

    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length); // code 200 (ok) + content length

    try (OutputStream os = exchange.getResponseBody()) { //exchange.getResponseBody() --> récupère le flux de sortie associé à la réponse HTTP. c’est dans ce flux qu’on écrit le contenu envoyé au client.
      os.write(response.getBytes(StandardCharsets.UTF_8)); // os.write --> écrit les octets de la chaîne response dans le flux de réponse. | response.getBytes(StandardCharsets.UTF_8) --> Convertit la chaîne de caractères en tableau d’octets en utilisant l’encodage UTF-8. 
    }
  }

  public void envoyerRequete(HttpExchange exchange, String response, int status) throws IOException {
    exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");

    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

    exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);

    try (OutputStream os = exchange.getResponseBody()) { 
      os.write(response.getBytes(StandardCharsets.UTF_8));
    }
  }

  public void envoyerRequete(HttpExchange exchange, String response, boolean isJson) throws IOException {
    if (!isJson) exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
    else exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

    try (OutputStream os = exchange.getResponseBody()) {
      os.write(response.getBytes(StandardCharsets.UTF_8));
    }
  }

  public void envoyerRequete(HttpExchange exchange, String response, int status, boolean isJson) throws IOException {
    if (!isJson) exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
    else exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");

    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

    exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);

    try (OutputStream os = exchange.getResponseBody()) { 
      os.write(response.getBytes(StandardCharsets.UTF_8));
    }
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    
    //pour l'instant j'affiche juste la requête du client | TODO --> call rmi en fonction de la requête du client.
    String clientAddress = exchange.getRemoteAddress().toString();

    System.out.println("[PROXYHANDLER] -------------------- CONNEXION DE : "+clientAddress+" --------------------");
    System.out.println("[PROXYHANDLER] "+exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getProtocol());

    for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
        for (String value : entry.getValue()) {
            System.out.println("[PROXYHANDLER] "+entry.getKey() + ": " + value);
        }
    }


    String uri = String.valueOf(exchange.getRequestURI());
    System.out.println();
    System.out.println("[PROXYHANDLER] URI: " + uri);
    if (uri.startsWith("/api")) {
      System.out.println("[PROXYHANDLER] l'endpoint /api est appelé");
      String endpoint = uri.substring(4);
      
      if (endpoint.startsWith("/bd")) { //TODO : faire un endpoint pour les coordonnées et un endpoint pour séservé (/bd/reserver/<restaurant>) 
        System.out.println("[PROXYHANDLER] l'endpoint /api/bd est appelé");
        endpoint = endpoint.substring(3);
        ServiceDatabase restaurant = this.proxy.getRestaurants();

        if (restaurant == null) {
          System.out.println("[PROXYHANDLER] Le service RMI pour de gestion de BD n'est pas enregistré au près du proxy");
          envoyerRequete(exchange, "erreur : le service RMI de base de données n'est pas disponible", 400);
        } else {

          if (endpoint.startsWith("/restaurants")) {
            System.out.println("[PROXYHANDLER] l'endpoint /api/bd/restaurants est appelé");
            try {
              String jsonBd = restaurant.getCoordonneesRestaurants();
              envoyerRequete(exchange, jsonBd, true);
            } catch (RemoteException e) {
              System.out.println("[PROXYHANDLER] l'appel RMI pour récupérer les coordonées des restaurants a échoué avec l'erreur : "+e.getMessage());
              envoyerRequete(exchange, e.getMessage(), 400);
            }

          } else if (endpoint.startsWith("/reserver")) {
            System.out.println("[PROXYHANDLER] l'endpoint /api/bd/reserver est appelé");

            //recupérer les infos depuis les params POST de la requête

            //String jsonBd = restaurant.reserverTable();
            //envoyerRequete(exchange, jsonBd, true);
          } else {
            System.out.println("[PROXYHANDLER] l'endpoint est invalide : il commance par /api/bd mais ne fini pas par 'restaurants' ou 'reserver'");
            envoyerRequete(exchange, "erreur : endpoint non valide", 400);
          }

        }

      } else if (endpoint.startsWith("/incidents")) {
        System.out.println("[PROXYHANDLER] l'endpoint /api/incidents est appelé");
        InterfaceIncidents incidents = this.proxy.getIncidents();

        if (incidents == null) {
          System.out.println("[PROXYHANDLER] Le service RMI pour de contournement de l'erreur CORS n'est pas enregistré au près du proxy");
          envoyerRequete(exchange, "erreur : le service RMI pour les données bloquées n'est pas disponible", 400);
        } else {
          try {
            String jsonBd = incidents.fetchAPIIncidents();
            envoyerRequete(exchange, jsonBd, true);
          } catch (RemoteException e) {
            System.out.println("[PROXYHANDLER] l'appel RMI pour récupérer les incidents a échoué avec l'erreur : "+e.getMessage());
            envoyerRequete(exchange, e.getMessage(), 400);
          }
        }

      } else {
        System.out.println("[PROXYHANDLER] le client a utilisé l'endpoint /api mais n'a pas utilisé /api/bd/... ou /api/data/...");
        envoyerRequete(exchange, "erreur : endpoint non valide", 400);
      }
    } else {
      System.out.println("[PROXYHANDLER] l'endpoint /api n'est pas appelé");
      String response = "Reponse Test depuis le serveur HTTP";
      envoyerRequete(exchange, response);
    }
    
  }


}
