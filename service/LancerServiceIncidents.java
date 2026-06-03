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

            InterfaxeProxy serviceProxyDistant = (InterfaxeProxy) registry.lookup("proxy");

            // exportation de l'objet service pour le rendre accessible à distance
            Object rd = (InterfaceIncidents) UnicastRemoteObject.exportObject(service, 0);


            serviceProxyDistant.setIncidents(rd);
            
        } catch (RemoteException e) {
            System.err.println("Erreur RMI : " + e.getMessage());
        }
    }
}