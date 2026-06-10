package donnees_bloquees;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceIncidents extends Remote {
    String fetchAPIIncidents() throws RemoteException; // méthode permettant de récupérer les données d'incidents au format JSON, cette méthode va être appelée à distance par RMI depuis le proxy HTTP
}