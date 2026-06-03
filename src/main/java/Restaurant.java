import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;

public class Restaurant {

    private int id;
    private String nom, adresse;
    private double latitude, longitude;

    public Restaurant(
            int id,
            String nom,
            String adresse,
            double latitude,
            double longitude
    ) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        json.put("id", id);
        json.put("nom", nom);
        json.put("adresse", adresse);
        json.put("latitude", latitude);
        json.put("longitude", longitude);

        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static Restaurant read(int id) {
        Connection connection = Database.getInstance(
                Credentials.USERNAME,
                Credentials.PASSWORD
        ).getConnection();

        String sql = "SELECT id, nom, adresse, latitude, longitude FROM Restaurant WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return new Restaurant(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                    );
                }

                return null; // no restaurant found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Restaurant> readAll() {
        Connection connection = Database.getInstance(
                Credentials.USERNAME,
                Credentials.PASSWORD
        ).getConnection();

        String sql = "SELECT id, nom, adresse, latitude, longitude FROM Restaurant";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet rs = statement.executeQuery()) {
                ArrayList<Restaurant> restaurants = new ArrayList<>();
                while (rs.next()) {
                    restaurants.add(new Restaurant(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("adresse"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude")
                    ));
                }

                return restaurants;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
