import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Date;

public class Reservation {

    private int id;
    private String nomClient, prenomClient, numeroTelephone;
    private int nbPersonnes;
    private Restaurant restaurant;
    private TableRestaurant tableRestaurant;
    private LocalDateTime date;

    public Reservation(
            int id,
            String nomClient,
            String prenomClient,
            String numeroTelephone,
            int nbPersonnes,
            int idRestaurant,
            int idTableRestaurant,
            LocalDateTime date
    ) {
        this.id = id;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.numeroTelephone = numeroTelephone;
        this.nbPersonnes = nbPersonnes;
        this.restaurant = Restaurant.read(idRestaurant);
        this.tableRestaurant = TableRestaurant.read(idTableRestaurant);
        this.date = date;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", nomClient='" + nomClient + '\'' +
                ", prenomClient='" + prenomClient + '\'' +
                ", telephone='" + numeroTelephone + '\'' +
                ", nbPersonnes=" + nbPersonnes +
                ", restaurant=" + (restaurant != null ? restaurant.getNom() : "null") +
                ", tableId=" + (tableRestaurant != null ? tableRestaurant.getId() : "null") +
                ", date=" + date +
                '}';
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();

        json.put("id", id);
        json.put("nomClient", nomClient);
        json.put("prenomClient", prenomClient);
        json.put("numeroTelephone", numeroTelephone);
        json.put("nbPersonnes", nbPersonnes);
        json.put("date", date.toString());

        if (restaurant != null) {
            json.put("restaurant", restaurant.toJson());
        } else {
            json.put("restaurant", JSONObject.NULL);
        }

        if (tableRestaurant != null) {
            json.put("tableRestaurant", tableRestaurant.toJson());
        } else {
            json.put("tableRestaurant", JSONObject.NULL);
        }

        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getPrenomClient() {
        return prenomClient;
    }

    public void setPrenomClient(String prenomClient) {
        this.prenomClient = prenomClient;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public int getNbPersonnes() {
        return nbPersonnes;
    }

    public void setNbPersonnes(int nbPersonnes) {
        this.nbPersonnes = nbPersonnes;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public TableRestaurant getTableRestaurant() {
        return tableRestaurant;
    }

    public void setTableRestaurant(TableRestaurant tableRestaurant) {
        this.tableRestaurant = tableRestaurant;
    }

    public static void create(Reservation r, int tableId) {

        Connection connection = Database.getInstance(
                Credentials.USERNAME,
                Credentials.PASSWORD
        ).getConnection();

        String sql =
                "INSERT INTO Reservation " +
                        "(id, nom_client, prenom_client, numero_telephone, " +
                        "nb_personnes, id_restaurant, id_tableRestaurant, date_reservation) " +
                        "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, r.getId());
            stmt.setString(2, r.getNomClient());
            stmt.setString(3, r.getPrenomClient());
            stmt.setString(4, r.getNumeroTelephone());
            stmt.setInt(5, r.getNbPersonnes());
            stmt.setInt(6, r.getRestaurant().getId());
            stmt.setInt(7, tableId);
            stmt.setTimestamp(8, Timestamp.valueOf(r.getDate()));

            stmt.executeUpdate();

            connection.commit();

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
        }
    }

    public static boolean isTableAvailable(int tableId, LocalDateTime date) {

        Connection connection = Database.getInstance(
                Credentials.USERNAME,
                Credentials.PASSWORD
        ).getConnection();

        String sql =
                "SELECT COUNT(*) FROM Reservation " +
                        "WHERE id_tableRestaurant = ? AND date_reservation = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            stmt.setTimestamp(2, Timestamp.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
