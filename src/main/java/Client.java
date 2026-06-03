import java.rmi.RemoteException;
import java.time.LocalDateTime;

public class Client {

    public static void main(String[] args) {

        ServiceDatabase service =
                Database.getInstance(Credentials.USERNAME, Credentials.PASSWORD);

        System.out.println("\n==============================");
        System.out.println("   TEST 1 : LISTE RESTAURANTS (JSON)");
        System.out.println("==============================");

        try {
            System.out.println(service.getCoordonneesRestaurants());
        } catch (RemoteException e) {
            System.err.println("Erreur lors de la récupération des restaurants : "
                    + e.getMessage());
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 2 : TABLES RESTAURANT 1");
        System.out.println("==============================");

        Restaurant r1 = Restaurant.read(1);

        if (r1 != null) {
            for (TableRestaurant t : TableRestaurant.getTablesByRestaurant(r1)) {
                System.out.println(t.toJson());
            }
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 3 : RESERVATION NORMALE");
        System.out.println("==============================");

        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 19, 30);

        try {
            String res1 = service.reserverTable(
                    r1,
                    date,
                    2,
                    "Dupont",
                    "Jean",
                    "0600000000"
            );

            System.out.println(res1);

        } catch (RemoteException e) {
            System.err.println("Erreur réservation : " + e.getMessage());
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 4 : CONFLIT (MEME TABLE + DATE)");
        System.out.println("==============================");

        try {
            String res2 = service.reserverTable(
                    r1,
                    date,
                    2,
                    "Martin",
                    "Sophie",
                    "0611111111"
            );

            System.out.println(res2);

        } catch (RemoteException e) {
            System.err.println("Erreur réservation : " + e.getMessage());
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 5 : CAPACITE INSUFFISANTE");
        System.out.println("==============================");

        try {
            String res3 = service.reserverTable(
                    r1,
                    LocalDateTime.of(2026, 6, 10, 20, 30),
                    50,
                    "Big",
                    "Group",
                    "0699999999"
            );

            System.out.println(res3);

        } catch (RemoteException e) {
            System.err.println("Erreur réservation : " + e.getMessage());
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 6 : RESTAURANT INEXISTANT");
        System.out.println("==============================");

        Restaurant fake = Restaurant.read(99999);

        if (fake == null) {
            System.out.println("{\"status\":\"ok\",\"message\":\"restaurant_not_found\"}");
        } else {
            System.out.println("{\"status\":\"error\",\"message\":\"unexpected_state\"}");
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 7 : STRESS TEST RESERVATIONS");
        System.out.println("==============================");

        for (int i = 0; i < 5; i++) {

            try {
                String result = service.reserverTable(
                        r1,
                        LocalDateTime.of(2026, 6, 11, 19, 0),
                        2,
                        "User" + i,
                        "Test",
                        "060000000" + i
                );

                System.out.println("Reservation " + i + " : " + result);

            } catch (RemoteException e) {
                System.err.println("Reservation " + i
                        + " : erreur distante - " + e.getMessage());
            }
        }

        System.out.println("\n==============================");
        System.out.println("   FIN DES TESTS");
        System.out.println("==============================");
    }
}