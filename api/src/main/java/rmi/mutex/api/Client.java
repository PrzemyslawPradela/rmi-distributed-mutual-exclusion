package rmi.mutex.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client extends Remote {
    int request(Client clientId) throws RemoteException;

    void receiveMessage(String message) throws RemoteException;
}