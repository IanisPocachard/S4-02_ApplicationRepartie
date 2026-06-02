import java.util.Date;

public class Reservation {

    private int id;
    private String nomClient, prenomClient, numeroTelephone;
    private int nbPersonnes;
    private Restaurant restaurant;
    private Date date;

    public Reservation(
            int id,
            String nomClient,
            String prenomClient,
            String numeroTelephone,
            int nbPersonnes,
            int idRestaurant,
            Date date
    ) {
        this.id = id;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.numeroTelephone = numeroTelephone;
        this.nbPersonnes = nbPersonnes;
        this.restaurant = Restaurant.read(idRestaurant);
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
