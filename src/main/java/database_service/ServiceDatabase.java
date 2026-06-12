package database_service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

public interface ServiceDatabase extends Remote {

    public String getCoordonneesRestaurants() throws RemoteException;

    public String reserverTable(
        int idRestaurant,
        LocalDateTime date,
        int nbPersonnes,
        String nom,
        String prenom,
        String telephone
    ) throws RemoteException, ReservationImpossibleException;

}
