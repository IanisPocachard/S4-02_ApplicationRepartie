package proxy;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Proxy implements InterfaceProxy {
  private InterfaceIncidents incidents = null;
  private boolean isIncidentsReady = false;
  private InterfaceRestaurants restaurants = null;
  private boolean isRestaurantReady = false;
  
  public Proxy() {}
  
  public void setIncidents(InterfaceIncidents incidents) {
    this.incidents = incidents;
    this.isIncidentsReady = true;
    if (this.isRestaurantsReady) this.lancerServeurHttp();
  }
  
  public void setRestaurants(InterfaceRestaurants restaurants) {
    this.restaurants = restaurants;
    this.isRestaurantsReady = true;
    if (this.isIncidentsReady) this.lancerServeurHttp();
  }

  public InterfaceIncidents getIncidents() {
    return this.incidents;
  }

  public InterfaceRestaurants getRestaurants() {
    return this.restaurants;
  }
  
  public void lancerServeurHttp () {
		try {
      HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);// InetSocketAdress --> représentation d'une adresse IP + port (adresse local et port 8080 ici) | le 0 c'est pour les backlogs (max 0 ici)
      server.createContext("/", new ProxyHandler(this)); //configuration du handler pour le chemin "/" (home)
      server.setExecutor(null); //pas d'objet Executor pour le proxy  --> Executor : remplace la création de Thread explicite (executor.execute(new RunnableTask()); | new Thread(new RunnableTask()).start();)
      
      server.start(); // avant de start il faut s'assurer que les deux services RMI sont connectés au proxy.Proxy. --> Ici ok puisque isIncidentsReady et isRestaurantsReady sont true
    }catch (Exception e) {
      e.printStackTrace();
    }
		
  }

  
}

