package database_service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Database implements ServiceDatabase {

    private Connection connection;
    private static Database instance;

    private Database(String identifiant, String mdp) {
        // URL de la base de données
        String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";

        // Test si le driver JDBC est disponible
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(java.lang.ClassNotFoundException e) {
            System.err.println("ClassNotFoundException: " + e.getMessage());
        }

        try {
            connection = DriverManager.getConnection(url, identifiant, mdp);
            connection.setAutoCommit(false);
            instance = this;
        } catch (SQLException e) {
            System.err.println("SQLException (try): " + e.getMessage());
        }
    }

    public static Database getInstance(String identifiant, String mdp) {
        if (instance == null) {
            instance = new Database(identifiant, mdp);
        }
        return instance;
    }

    public void chargerRestaurants(String fichier) throws IOException {
        String contenu = Files.readString(Path.of(fichier));

        JSONArray jsonArray = new JSONArray(contenu);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            Restaurant r = new Restaurant(
                    obj.getInt("id"),
                    obj.getString("nom"),
                    obj.getString("adresse"),
                    obj.getDouble("latitude"),
                    obj.getDouble("longitude")
            );

            r.insert();
        }
    }

    public void chargerTables(String fichier) throws IOException {
        String contenu = Files.readString(Path.of(fichier));

        JSONArray jsonArray = new JSONArray(contenu);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            TableRestaurant table = new TableRestaurant(
                    obj.getInt("id"),
                    obj.getInt("capacite"),
                    obj.getInt("restaurantId")
            );

            table.insert();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getCoordonneesRestaurants() {
        System.out.println("[DATABASE] Récupération des restaurants...");

        ArrayList<Restaurant> restaurants = Restaurant.readAll();

        JSONArray jsonArray = new JSONArray();

        for (Restaurant restaurant : restaurants) {
            jsonArray.put(restaurant.toJson());
        }

        System.out.println("[DATABASE] Récupération des restaurants efectuée.");

        return jsonArray.toString();
    }

    @Override
    public String reserverTable(
            int idRestaurant,
            LocalDateTime date,
            int nbPersonnes,
            String nom,
            String prenom,
            String telephone
    ) throws ReservationImpossibleException {

        System.out.println("[DATABASE] Tentative de réservation d'une table...");

        Connection connection =
                Database.getInstance(Credentials.USERNAME, Credentials.PASSWORD)
                        .getConnection();

        try {

            // début transaction
            connection.setAutoCommit(false);

            Restaurant restaurant = Restaurant.read(idRestaurant);

            if (restaurant == null) {
                throw new ReservationImpossibleException("Restaurant introuvable.");
            }

            ArrayList<TableRestaurant> tables =
                    TableRestaurant.getTablesByRestaurant(restaurant, nbPersonnes);

            for (TableRestaurant t : tables) {

                // verrou physique de la ligne
                TableRestaurant locked =
                        TableRestaurant.lockById(connection, t.getId());

                if (locked == null) continue;

                // check disponibilité dans la transaction
                if (Reservation.isTableAvailable(connection, locked.getId(), date)) {

                    int id = (int) (System.currentTimeMillis() % 1_000_000);

                    Reservation r = new Reservation(
                            id,
                            nom,
                            prenom,
                            telephone,
                            nbPersonnes,
                            restaurant.getId(),
                            locked.getId(),
                            date
                    );

                    Reservation.create(r, locked.getId());

                    connection.commit();

                    System.out.println("[DATABASE] Réservation de " + nom + " " + prenom
                            + " au restaurant " + restaurant.getNom()
                            + " et à la date et heure " + date + " réalisée.");

                    return r.toJson().toString();
                }
            }

            connection.rollback();

            throw new ReservationImpossibleException(
                    "Réservation de " + nom + " " + prenom
                    + " au restaurant " + restaurant.getNom()
                    + " et à la date et heure " + date + " impossible."
            );

        } catch (ReservationImpossibleException e) {
            try {
                connection.rollback();
            } catch (Exception ignored) {}

            throw e;

        } catch (Exception e) {

            try {
                connection.rollback();
            } catch (Exception ignored) {}

            throw new ReservationImpossibleException(e.getMessage());
        }
    }

}
