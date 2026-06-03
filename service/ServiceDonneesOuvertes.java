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

public class ServiceDonneesOuvertes implements InterfaceProxy {

    private static final String URL = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json";

    public ServiceDonneesOuvertes() throws RemoteException {
        super();
    }

    // transmettre les données recuperer via http
    @Override
    public void setIncidents(InterfaceIncidents incidents) throws RemoteException {
        System.out.println("setIncidents appelé");
    }

    @Override
    public void setRestaurants(InterfaceRestaurants restaurants) throws RemoteException {
        System.out.println("setRestaurants appelé");
    }

    // Recuperation http
    public String fetchDonnees() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(Version.HTTP_1_1)
                    .followRedirects(Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
                    .authenticator(Authenticator.getDefault())
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
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

            return response.body();

        } catch (IOException e) {
            System.err.println("Erreur réseau : " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Requête interrompue");
            Thread.currentThread().interrupt();
        }
        return null;
    }
}