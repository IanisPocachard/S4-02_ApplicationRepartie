import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class LancerService {
       public static void main(String[] args) {
        try {
            // instanciation du service de données
            ServiceDonneesOuvertes service = new ServiceDonneesOuvertes();

            // exportation de l'objet service pour le rendre accessible à distance
            Object rd = (InterfaceIncident) UnicastRemoteObject.exportObject(service, 0);

            // Enregistrement dans le registre RMI, pour le moment on ne spécifie pas de nom pour le service, on le fera plus tard donc sans paramètre ça va chercher sur localhost et sur le port par défaut 1099
            Registry registry = LocateRegistry.getRegistry();

            InterfaxeProxy serviceDistant = (InterfaxeProxy) registry.lookup("proxy");

            serviceDistant.setIncidents(rd);
            
        } catch (RemoteException e) {
            System.err.println("Erreur RMI : " + e.getMessage());
        }
    }
}