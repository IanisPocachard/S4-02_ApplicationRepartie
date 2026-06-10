package database_service;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceDatabase {
    public static void main(String[] args) {

        String jsonFile = "restaurants.json";
        if (args.length > 0) {
            jsonFile = args[0];
        }

        Database database = Database.getInstance(Credentials.USERNAME, Credentials.PASSWORD);
        try {
            database.chargerRestaurants(jsonFile);
        } catch (IOException e) {
            System.err.println("Erreur : fichier non trouvé " + jsonFile + "\n" + e.getMessage());
        }

        try {
            ServiceDatabase rd = (ServiceDatabase) UnicastRemoteObject.exportObject((ServiceDatabase) database, 0);
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.rebind("database", rd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
