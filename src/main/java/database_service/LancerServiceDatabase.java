package database_service;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerServiceDatabase {
    public static void main(String[] args) {

        String usage = "Usage: java LancerServiceDatabase [restaurant_json_file] [tables_json_file]";

        String jsonFileRestaurant = "restaurants.json";
        String jsonFileTables = "tables_restaurant.json";

        if (args.length == 0) System.out.println(usage);
        if (args.length > 0) {
            jsonFileRestaurant = args[0];
        }
        if (args.length > 1) {
            jsonFileTables = args[1];
        }

        Database database = Database.getInstance(Credentials.USERNAME, Credentials.PASSWORD);
        try {
            database.chargerRestaurants(jsonFileRestaurant);
        } catch (IOException e) {
            System.err.println("Erreur : fichier non trouvé " + jsonFileRestaurant + "\n" + e.getMessage());
        }
        try {
            database.chargerTables(jsonFileTables);
        } catch (IOException e) {
            System.err.println("Erreur : fichier non trouvé " + jsonFileTables + "\n" + e.getMessage());
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
