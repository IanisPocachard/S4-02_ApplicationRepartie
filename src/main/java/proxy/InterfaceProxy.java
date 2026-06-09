package proxy;
import database_service.ServiceDatabase;

import java.rmi.Remote;

import donnees_bloquees.InterfaceIncidents;

public interface InterfaceProxy extends Remote {
  public void setIncident(InterfaceIncidents incidents);
  public void setRestaurant(ServiceDatabase restaurants);
}
