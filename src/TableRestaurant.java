import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TableRestaurant {

    private int id, capacite;
    private Restaurant restaurant;

    public TableRestaurant(int id, int capacite, int idRestaurant) {
        this.id = id;
        this.capacite = capacite;
        this.restaurant = Restaurant.read(idRestaurant);
    }

    @Override
    public String toString() {
        return "TableRestaurant{" +
                "id=" + id +
                ", capacite=" + capacite +
                ", restaurant=" + (restaurant != null ? restaurant.getNom() : "null") +
                '}';
    }


    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        json.put("id", id);
        json.put("capacite", capacite);

        if (restaurant != null) {
            json.put("restaurant", restaurant.toJson());
        } else {
            json.put("restaurant", JSONObject.NULL);
        }

        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public static TableRestaurant read(int id) {
        Connection connection = Database.getInstance(
                Credentials.USERNAME,
                Credentials.PASSWORD
        ).getConnection();

        String sql =
                "SELECT id, capacite, id_restaurant FROM TableRestaurant WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return new TableRestaurant(
                            rs.getInt("id"),
                            rs.getInt("capacite"),
                            rs.getInt("id_restaurant")
                    );
                }

                return null; // no restaurant found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<TableRestaurant> getTablesByRestaurant(Restaurant restaurant, int nbPersonnes) {

        Connection connection = Database.getInstance(
                Credentials.USERNAME,
                Credentials.PASSWORD
        ).getConnection();

        String sql =
                "SELECT id, capacite, id_restaurant " +
                        "FROM TableRestaurant " +
                        "WHERE id_restaurant = ? " +
                        "AND capacite >= ? " +
                        "ORDER BY capacite ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, restaurant.getId());
            statement.setInt(2, nbPersonnes);

            try (ResultSet rs = statement.executeQuery()) {

                ArrayList<TableRestaurant> tables = new ArrayList<>();

                while (rs.next()) {
                    tables.add(new TableRestaurant(
                            rs.getInt("id"),
                            rs.getInt("capacite"),
                            rs.getInt("id_restaurant")
                    ));
                }

                return tables;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<TableRestaurant> getTablesByRestaurant(Restaurant restaurant) {
        return getTablesByRestaurant(restaurant, Integer.MAX_VALUE);
    }

    public static TableRestaurant lockById(Connection connection, int id) throws SQLException {

        String sql = """
        SELECT id, capacite, id_restaurant
        FROM TableRestaurant
        WHERE id = ?
        FOR UPDATE
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new TableRestaurant(
                            rs.getInt("id"),
                            rs.getInt("capacite"),
                            rs.getInt("id_restaurant")
                    );
                }
            }
        }

        return null;
    }
}
