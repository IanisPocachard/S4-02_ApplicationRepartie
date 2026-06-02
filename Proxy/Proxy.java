import java.net.http.Http;
import java.net.InetSocketAdress;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.RemoteException;
import java.rmi.Remote;

public class Proxy implements InterfaceProxy {
  private InterfaceIncidents incidents = null;
  private boolean isIncidentsReady;
  private InterfaceRestaurants restaurants = null;
  private boolean isRestaurantReady;
  
  public Proxy() {
     this.isIncidentsReady = false;
     this.isRestaurantReady = false;
  }
  
  public setIncidents(InterfaceIncidents incidents) {
    this.incidents = incidents;
    this.isIncidentsReady = true;
    if (this.isRestaurantsReady) this.lancerServeurHttp();
  }
  
  public setRestaurants(InterfaceRestaurants restaurants) {
    this.restaurants = restaurants;
    this.isRestaurantsReady = true;
    if (this.isIncidentsReady) this.lancerServeurHttp();
  }
  
  public void lancerAnnuaire() {
    Proxy proxy = new Proxy();
		InterfaceProxy proxy = (InterfaceProxy) UnicastRemoteObject.exportObject(proxy, 0);

		Registry reg = LocateRegistry.createRegistry(1099);
		reg.rebind("proxy", i);
  }
  
  public void lancerServeurHttp () {
		
		HttpServeur server = HttpServer.create(new InetSocketAdress(8080), 0)// InetSocketAdress --> représentation d'une adresse IP + port (adresse local et port 8080 ici) | le 0 c'est pour les backlogs (max 0 ici)
		server.createContext("/", new ProxyHandler()); //configuration du handler pour le chemin "/" (home)
		server.setExecutor(null); //pas d'objet Executor pour le proxy  --> Executor : remplace la création de Thread explicite (executor.execute(new RunnableTask()); | new Thread(new RunnableTask()).start();)
		
		server.start(); // avant de start il faut s'assurer que les deux services RMI sont connectés au Proxy. --> Ici ok puisque isIncidentsReady et isRestaurantsReady sont true
		
  }

  
}

