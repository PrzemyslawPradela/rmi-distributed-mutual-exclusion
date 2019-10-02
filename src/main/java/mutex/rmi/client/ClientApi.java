package mutex.rmi.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import mutex.rmi.api.Client;

public class ClientApi extends UnicastRemoteObject implements Client {
    private static final long serialVersionUID = 8817066730528372707L;

    protected ClientApi() throws RemoteException {
    }
}