import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class LancerServiceIncidents {
       public static void main(String[] args) {
        try {
            // instanciation du service de données
            ServiceIncidents service = new ServiceIncidents();

            // Enregistrement dans le registre RMI, pour le moment on ne spécifie pas de nom pour le service, on le fera plus tard donc sans paramètre ça va chercher sur localhost et sur le port par défaut 1099
            Registry registry = LocateRegistry.getRegistry();

            InterfaceProxy serviceProxyDistant = (InterfaceProxy) registry.lookup("proxy");

            serviceProxyDistant.setIncidents(service); // on enregistre le service de données dans le proxy pour qu'il puisse y accéder à distance via RMI ensuite quand il va recevoir des requêtes HTTP du client
            
        } catch (RemoteException e) {
            System.err.println("Erreur RMI : " + e.getMessage());
        }
    }
}