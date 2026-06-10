package proxy;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class LancerProxy {
    public static void main (String[] args) {
        Proxy proxy = new Proxy();

        try {
            InterfaceProxy serviceProxy = (InterfaceProxy) UnicastRemoteObject.exportObject(proxy, 0);

            Registry reg = LocateRegistry.createRegistry(1099); // lancer via rmiregistry
            reg.rebind("proxy", serviceProxy);
        } catch (RemoteException e) {
            System.err.println("Erreur lors de l'export du proxy : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}