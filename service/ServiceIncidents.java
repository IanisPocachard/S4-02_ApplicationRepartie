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

public class ServiceIncidents implements InterfaceIncidents {

    private static final String URL = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json";

    public ServiceIncidents() throws RemoteException {
        super();
    }


    public String getIncidentsJson() throws RemoteException {
        return fetchDonnees(); // appelle la méthode fetchDonnees pour récupérer les données d'incidents au format JSON et les renvoyer
    }


    // Recuperation http
    public String fetchDonnees() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(Version.HTTP_1_1) // permet de forcer l'utilisation de HTTP/1.1
                    .followRedirects(Redirect.NORMAL) // permet de suivre les redirections HTTP automatiquement
                    .connectTimeout(Duration.ofSeconds(20)) // définit un délai d'attente pour la connexion de 20 sec
                    .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128))) // configure un proxy pour les requêtes HTTP comme on va lancer sur un ordi de l'IUT
                    .authenticator(Authenticator.getDefault())
                    .build(); // permet de construire le client HTTP avec les paramètres spécifiés juste au dessus

            HttpRequest request = HttpRequest.newBuilder() // on construit ensuite la requete HTTP vers l'URL de l'API de traffic
                    .uri(URI.create(URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            System.out.println("Code HTTP : " + statusCode);

            // gestion des erreurs
            if (statusCode != 200) {
                System.err.println("Erreur HTTP : " + statusCode);
                return null;
            }

            return response.body(); // renvoie le corps de la réponse HTTP sous forme de chaîne de caractères, donc le xcontenu de l'API de traffic

        } catch (IOException e) {
            System.err.println("Erreur réseau : " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Requête interrompue");
            Thread.currentThread().interrupt();
        }
        return null; // TODO : peut-être renvoyer une chaîne de caractères indiquant une erreur au lieu de null pour mieux gérer les erreurs côté client
    }
}