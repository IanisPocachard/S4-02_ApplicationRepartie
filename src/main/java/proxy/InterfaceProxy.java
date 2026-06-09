package proxy;

import java.rmi.Remote;

public interface InterfaceProxy extends Remote {
  public void setIncidents(InterfaceIncidents incidents);
  public void setRestaurants(InterfaceRestaurants restaurants);
}
