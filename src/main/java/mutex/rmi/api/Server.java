package mutex.rmi.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    String connect(Client clientId) throws RemoteException;

    String disconnect(Client clientId) throws RemoteException;

    String enterCriticalSection(Client clientId) throws RemoteException;

    String leaveCriticalSection(Client clientId) throws RemoteException;

    boolean isCriticalSectionOccupied() throws RemoteException;
}