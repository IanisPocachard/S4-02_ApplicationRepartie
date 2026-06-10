package proxy;
import database_service.ServiceDatabase;

import java.rmi.Remote;
import java.rmi.RemoteException;

import donnees_bloquees.InterfaceIncidents;

public interface InterfaceProxy extends Remote {
  public void setIncident(InterfaceIncidents incidents) throws RemoteException;
  public void setRestaurant(ServiceDatabase restaurants) throws RemoteException;
}
