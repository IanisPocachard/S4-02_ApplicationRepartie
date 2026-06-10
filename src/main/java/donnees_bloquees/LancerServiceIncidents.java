package donnees_bloquees;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import proxy.InterfaceProxy;

public class LancerServiceIncidents {
    public static void main(String[] args) throws RemoteException, NotBoundException {

        String ipProxy = args.length > 0 ? args[0] : "localhost";

        // instanciation du service de données
        ClientHTTPIncidents service = new ClientHTTPIncidents();

        // export de l'objet sur le réseau
        InterfaceIncidents rd = (InterfaceIncidents) UnicastRemoteObject.exportObject(service, 0);

        Registry reg = LocateRegistry.getRegistry(ipProxy, 1099); // récupération du registre RMI du proxy distant

        InterfaceProxy serviceProxyDistant = (InterfaceProxy) reg.lookup("proxy"); // on récupère le proxy distant dans le registre RMI du proxy distant

        serviceProxyDistant.setIncident(rd); // on enregistre le service de données dans le proxy pour qu'il puisse y accéder à distance via RMI ensuite quand il va recevoir des requêtes HTTP du client

        System.out.println("[Client HTTP] Prêt à l'adresse IP : " + ipProxy + ", prêt à recevoir les requêtes du proxy pour intéragir avec des API bloquées");
    }
}