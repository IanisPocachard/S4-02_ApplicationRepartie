package database_service;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import proxy.InterfaceProxy;

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

        System.out.println("[DATABASE] Réinitialisation des tables...");
        Reservation.dropAll();
        System.out.println("[DATABASE] Table Reservation réinitialisée.");
        TableRestaurant.dropAll();
        System.out.println("[DATABASE] Table TableRestaurant réinitialisée.");
        Restaurant.dropAll();
        System.out.println("[DATABASE] Table Restaurant réinitialisée.");

        System.out.println("\n[DATABASE] Remplissage des tables...");
        try {
            database.chargerRestaurants(jsonFileRestaurant);
            System.out.println("[DATABASE] Table Restaurant peuplée.");
        } catch (IOException e) {
            System.err.println("[DATABASE] Erreur : fichier non trouvé " + jsonFileRestaurant + "\n" + e.getMessage());
        }
        try {
            database.chargerTables(jsonFileTables);
            System.out.println("[DATABASE] Table TableRestaurant peuplée.");
        } catch (IOException e) {
            System.err.println("[DATABASE] Erreur : fichier non trouvé " + jsonFileTables + "\n" + e.getMessage());
        }

        try {
            ServiceDatabase rd = (ServiceDatabase) UnicastRemoteObject.exportObject((ServiceDatabase) database, 0);
            Registry registry = LocateRegistry.getRegistry(1099);

            // Lier le service Database au proxy
            InterfaceProxy serviceProxy = (InterfaceProxy) registry.lookup("proxy");
            serviceProxy.setRestaurant(rd);
            System.out.println("\n[DATABASE] Service Database lié au proxy !");

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("\n[DATABASE] Service Database lancé !");
    }
}
