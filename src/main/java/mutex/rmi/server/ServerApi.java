package mutex.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import mutex.rmi.api.Server;

public class ServerApi extends UnicastRemoteObject implements Server {
    private static final long serialVersionUID = 1L;

    protected ServerApi() throws RemoteException {
    }
    
}