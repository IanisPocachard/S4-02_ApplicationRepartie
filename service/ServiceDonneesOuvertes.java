import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

class ServiceDonneesOuvertes{
    private String url = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json";

    public static void main(String[] args) {

        try{
                HttpClient client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
                .authenticator(Authenticator.getDefault())
                .build();
                
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                System.out.println(response.statusCode());
                System.out.println(response.body());

                if (response.statusCode() == 400){
                    System.err.println("Mauvaise requete : " + statusCode);
                    return;
                }
                if (response.statusCode() == 301 || response.statusCode() == 302){
                    System.err.println("Redirection : " + statusCode);
                    return;
                }
                if (response.statusCode() == 401 ){
                    System.err.println("Non authentifié : " + statusCode);
                    return;
                }
                if (response.statusCode() == 403 ){
                    System.err.println("Accès interdit : " + statusCode);
                    return;
                }
                if (response.statusCode() == 404 ){
                    System.err.println("Ressource introuvable : " + statusCode);
                    return;
                }
                if (response.statusCode() == 500 ){
                    System.err.println("Erreur interne du serveur : " + statusCode);
                    return;
                }
                if (response.statusCode() == 503 ){
                    System.err.println("Service indisponible: " + statusCode);
                    return;
                }


        }catch (IOException e) {
            System.err.println(
                    "Erreur réseau : " + e.getMessage());
        }
        catch (InterruptedException e) {
            System.err.println(
                    "Requête interrompue");
            Thread.currentThread().interrupt();
        }
        catch (IllegalArgumentException e) {
            System.err.println(
                    "URL invalide : " + e.getMessage());
        }
    }
}