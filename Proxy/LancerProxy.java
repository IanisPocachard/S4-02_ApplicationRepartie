class LancerProxy {
    public static void main (String[] args) {
        Proxy proxy = new Proxy();
        InterfaceProxy proxy = (InterfaceProxy) UnicastRemoteObject.exportObject(proxy, 0);

		Registry reg = LocateRegistry.getRegistry(1099); // lancer via rmiregistry
		reg.rebind("proxy", proxy);
    }
}