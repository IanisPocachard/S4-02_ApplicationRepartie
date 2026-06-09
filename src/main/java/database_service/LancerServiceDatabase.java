package database_service;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceDatabase {
    public static void main(String[] args) {

        ServiceDatabase service = Database.getInstance(Credentials.USERNAME, Credentials.PASSWORD);

        try {
            ServiceDatabase rd = (ServiceDatabase) UnicastRemoteObject.exportObject(service, 0);
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.rebind("database", rd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
