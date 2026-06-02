import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

public interface InterfaceProxy extends Remote{
   public void setIncidents(InterfaceIncidents incidents);
   public void setRestaurants(InterfaceRestaurants restaurants);
}
