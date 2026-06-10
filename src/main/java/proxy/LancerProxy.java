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
            InterfaceProxy interfaceProxy = (InterfaceProxy) UnicastRemoteObject.exportObject(proxy, 0);

            Registry reg = LocateRegistry.getRegistry(1099); // lancer via rmiregistry
            reg.rebind("proxy", proxy);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}