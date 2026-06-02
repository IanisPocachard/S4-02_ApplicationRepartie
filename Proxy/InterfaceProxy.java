import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

public interface InterfaceProxy extends Remote{
   public setIncidents(InterfaceIncidents incidents);
   public setRestaurants(InterfaceRestaurants restaurants);
}
