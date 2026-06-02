public class TableRestaurant {

    private int id, capacite;
    private Restaurant restaurant;

    public TableRestaurant(int id, int capacite, int idRestaurant) {
        this.id = id;
        this.capacite = capacite;
        this.restaurant = Restaurant.read(idRestaurant);
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
}
