import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getCoordonneesRestaurants() {

        ArrayList<Restaurant> restaurants = Restaurant.readAll();

        JSONArray jsonArray = new JSONArray();

        for (Restaurant restaurant : restaurants) {
            jsonArray.put(new JSONObject(restaurant.toJson()));
        }

        return jsonArray.toString();
    }

    @Override
    public String reserverTable(
            Restaurant restaurant,
            LocalDateTime date,
            int nbPersonnes,
            String nom,
            String prenom,
            String telephone
    ) {

        ArrayList<TableRestaurant> tables =
                TableRestaurant.getTablesByRestaurant(restaurant, nbPersonnes);

        for (TableRestaurant table : tables) {

            if (Reservation.isTableAvailable(table.getId(), date)) {

                int id = (int) (System.currentTimeMillis() % 1_000_000);

                Reservation r = new Reservation(
                        id,
                        nom,
                        prenom,
                        telephone,
                        nbPersonnes,
                        restaurant.getId(),
                        table.getId(),
                        date
                );

                Reservation.create(r, table.getId());

                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("reservation", new JSONObject(r.toJson()));

                return response.toString();
            }
        }

        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("message", "no_table_available");

        return response.toString();
    }

}
