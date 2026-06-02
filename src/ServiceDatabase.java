import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public interface ServiceDatabase {

    public ArrayList<HashMap<Double, Double>> getCoordonneesRestaurants();

    public Reservation reserverTable(
        Restaurant restaurant,
        LocalDateTime date,
        int nbPersonnes,
        String nom,
        String prenom,
        String telephone
    );

}
