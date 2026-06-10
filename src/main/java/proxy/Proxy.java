package proxy;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;

import donnees_bloquees.InterfaceIncidents;
import database_service.ServiceDatabase;

public class Proxy implements InterfaceProxy {
  private InterfaceIncidents incident = null;
  private boolean isIncidentReady = false;
  private ServiceDatabase restaurant = null;
  private boolean isRestaurantReady = false;
  
  public Proxy() {}
  
  public void setIncident(InterfaceIncidents incident) {
    System.out.println("service RMI pour les incidents prêt");
    this.incident = incident;
    this.isIncidentReady = true;
    if (this.isRestaurantReady) this.lancerServeurHttp();
  }
  
  public void setRestaurant(ServiceDatabase restaurant) throws RemoteException {
    System.out.println("service RMI pour la base de données prêt");
    this.restaurant = restaurant;
    this.isRestaurantReady = true;
    if (this.isIncidentReady) this.lancerServeurHttp();
  }

  public InterfaceIncidents getIncidents() {
    return this.incident;
  }

  public ServiceDatabase getRestaurants() {
    return this.restaurant;
  }
  
  public void lancerServeurHttp () {
    System.out.println("LANCEMENT DU SERVEUR HTTP");

    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);// InetSocketAdress --> représentation d'une adresse IP + port (adresse local et port 8080 ici) | le 0 c'est pour les backlogs (max 0 ici)
      server.createContext("/", new ProxyHandler(this)); //configuration du handler pour le chemin "/" (home)
      server.setExecutor(null); //pas d'objet Executor pour le proxy  --> Executor : remplace la création de Thread explicite (executor.execute(new RunnableTask()); | new Thread(new RunnableTask()).start();)
      
      server.start(); // avant de start il faut s'assurer que les deux services RMI sont connectés au proxy.Proxy. --> Ici ok puisque isIncidentReady et isRestaurantsReady sont true
    }catch (Exception e) {
      e.printStackTrace();
    }
		
  }

  
}

