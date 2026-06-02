import java.time.LocalDateTime;

public class Client {

    public static void main(String[] args) {

        ServiceDatabase service =
                Database.getInstance(Credentials.USERNAME, Credentials.PASSWORD);

        System.out.println("\n==============================");
        System.out.println("   TEST 1 : LISTE RESTAURANTS");
        System.out.println("==============================");

        for (Restaurant r : Restaurant.readAll()) {
            System.out.println(r);
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 2 : TABLES RESTAURANT 1");
        System.out.println("==============================");

        Restaurant r1 = Restaurant.read(1);

        if (r1 != null) {
            for (TableRestaurant t : TableRestaurant.getTablesByRestaurant(r1)) {
                System.out.println(t);
            }
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 3 : RESERVATION NORMALE");
        System.out.println("==============================");

        LocalDateTime date = LocalDateTime.of(2026, 6, 10, 19, 30);

        Reservation res1 = service.reserverTable(
                r1,
                date,
                2,
                "Dupont",
                "Jean",
                "0600000000"
        );

        System.out.println(res1 != null ? res1 : "ECHEC");

        System.out.println("\n==============================");
        System.out.println("   TEST 4 : CONFLIT (MEME TABLE + DATE)");
        System.out.println("==============================");

        Reservation res2 = service.reserverTable(
                r1,
                date,
                2,
                "Martin",
                "Sophie",
                "0611111111"
        );

        System.out.println(res2 != null ? res2 : "ECHEC ATTENDU (CONFLIT)");

        System.out.println("\n==============================");
        System.out.println("   TEST 5 : CAPACITE INSUFFISANTE");
        System.out.println("==============================");

        Reservation res3 = service.reserverTable(
                r1,
                LocalDateTime.of(2026, 6, 10, 20, 30),
                50, // trop grand
                "Big",
                "Group",
                "0699999999"
        );

        System.out.println(res3 != null ? res3 : "ECHEC ATTENDU (CAPACITE)");

        System.out.println("\n==============================");
        System.out.println("   TEST 6 : RESTAURANT INEXISTANT");
        System.out.println("==============================");

        Restaurant fake = Restaurant.read(99999);

        if (fake == null) {
            System.out.println("OK : restaurant inexistant");
        } else {
            System.out.println("ERREUR : restaurant devrait être null");
        }

        System.out.println("\n==============================");
        System.out.println("   TEST 7 : STRESS TEST RESERVATIONS");
        System.out.println("==============================");

        for (int i = 0; i < 5; i++) {

            Reservation r = service.reserverTable(
                    r1,
                    LocalDateTime.of(2026, 6, 11, 19, 0),
                    2,
                    "User" + i,
                    "Test",
                    "060000000" + i
            );

            System.out.println("Reservation " + i + " : " +
                    (r != null ? "OK" : "ECHEC"));
        }

        System.out.println("\n==============================");
        System.out.println("   FIN DES TESTS");
        System.out.println("==============================");
    }
}