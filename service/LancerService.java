import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class LancerService {
       public static void main(String[] args) {
        try {
            ServiceDonneesOuvertes service = new ServiceDonneesOuvertes();

            Object serviceExporte = UnicastRemoteObject.exportObject(service, 0);
            // Enregistrement dans le registre RMI
            Registry registry = LocateRegistry.createRegistry(1099);

            InterfaceProxy rd = (InterfaceProxy) serviceExporte;
            registry.rebind("ServiceDonneesBloquees", rd);
            System.out.println("Service enregistré dans le registre RMI");
            
        } catch (RemoteException e) {
            System.err.println("Erreur RMI : " + e.getMessage());
        }
    }
}