package donnees_bloquees;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.rmi.RemoteException;
import java.time.Duration;

public class ClientHTTPIncidents implements InterfaceIncidents {


    final String urlAPIIncidents = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json";

    // client HTTP
    public String fetchAPIIncidents() throws RemoteException {
        System.out.println("[Client HTTP] Appel de la méthode de récupération des données d'incidents depuis le proxy");
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(Version.HTTP_1_1) // permet de forcer l'utilisation de HTTP/1.1
                    .followRedirects(Redirect.NORMAL) // permet de suivre les redirections HTTP automatiquement
                    .connectTimeout(Duration.ofSeconds(20)) // définit un délai d'attente pour la connexion de 20 sec
                    .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128))) // configure un proxy pour les requêtes HTTP comme on va lancer sur un ordi de l'IUT
                    .build(); // permet de construire le client HTTP avec les paramètres spécifiés juste au dessus


            // TODO : remettre le .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128))) // configure un proxy pour les requêtes HTTP comme on va lancer sur un ordi de l'IUT
            // car sinon on ne peut pas faire de requêtes HTTP depuis l'ordi de l'IUT

            System.out.println("[Client HTTP] Envoi de la requête HTTP GET à l'API d'incidents : " + urlAPIIncidents);

            HttpRequest request = HttpRequest.newBuilder() // on construit ensuite la requete HTTP vers l'URL de l'API de traffic
                    .uri(URI.create(urlAPIIncidents))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString()); // BodyHandlers.ofString() --> indique que le corps de la réponse HTTP doit être traité comme une chaîne de caractères. | client.send(request, BodyHandlers.ofString()) --> envoie la requête HTTP et attend la réponse du serveur, qui est ensuite traitée pour extraire le corps de la réponse sous forme de chaîne de caractères, cet appel est bloquant donc le thread qui exécute cette méthode sera suspendu jusqu'à ce que la réponse soit reçue ou qu'une exception soit levée par exemple en cas de problème de réseau ou de délai d'attente dépassé
            System.out.println("[Client HTTP] Envoie de la requête : " + request);
            System.out.println("[Client HTTP] Réception de la réponse : " + response);
            int statusCode = response.statusCode();
            System.out.println("Code HTTP : " + statusCode);

            // gestion des erreurs
            if (statusCode != 200) {
                System.err.println("Erreur HTTP : " + statusCode);
                throw new RemoteException("Erreur HTTP : " + statusCode); // TODO : redemander à Ambroise si c'est ok pour lui que lui catch le RemoteException et qu'il renvoie lui un json vers le navigateur avec un message d'erreur pour que le navigateur puisse afficher un message d'erreur à l'utilisateur
            }

            return response.body(); // renvoie le corps de la réponse HTTP sous forme de chaîne de caractères, donc le xcontenu de l'API de traffic

        } catch (IOException e) {
            System.err.println("Erreur réseau : " + e.getMessage());
            throw new RemoteException("Erreur réseau pendant l'appel à l'API incidents", e);
        } catch (InterruptedException e) {
            System.err.println("Requête interrompue");
            Thread.currentThread().interrupt();
            throw new RemoteException("Requête interrompue", e);
        }
    }
}