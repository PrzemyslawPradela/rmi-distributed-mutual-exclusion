package rmi.mutex.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.scene.control.TextArea;
import rmi.mutex.api.Client;
import rmi.mutex.api.Server;

public class ServerApi extends UnicastRemoteObject implements Server {
    private static final long serialVersionUID = 6177858376321750016L;
    private CopyOnWriteArrayList<Client> connectedClients;
    private SimpleDateFormat dateFormat;
    private boolean criticalSectionOccupied;
    private TextArea logsTextArea;

    public ServerApi(TextArea logsTextArea) throws RemoteException {
        this.connectedClients = new CopyOnWriteArrayList<Client>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.criticalSectionOccupied = false;
        this.logsTextArea = logsTextArea;
    }

    @Override
    public synchronized String connect(Client clientId) throws RemoteException {
        connectedClients.add(clientId);
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tINFO\t\tNowy klient nawiązał połączenie z serwerem\n");
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tEVENT\t\t[type=CONNECTION, timestamp="
                        + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        return "\tINFO\t\tPołączono z serwerem\n";
    }

    @Override
    public synchronized String disconnect(Client clientId) throws RemoteException {
        connectedClients.remove(clientId);
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tINFO\t\tJeden klient zakończył połączenie z serwerem\n");
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tEVENT\t\t[type=DISCONNECTION, timestamp="
                        + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        return "\tINFO\t\tRozłączono z serwerem\n";
    }

    @Override
    public synchronized void enterCriticalSection(Client clientId) throws RemoteException, InterruptedException {
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tINFO\t\tJeden klient zgłosił żądanie wejścia do sekcji krytycznej\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tEVENT\t\t[type=WANTED, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");

        if (connectedClients.size() > 1) {
            clientId.receiveMessage("\tINFO\t\tWysyłam komunikat REQUEST do pozostałych klientów\n");
            clientId.receiveMessage("\tINFO\t\tOczekiwanie na odpowiedź od pozostałych klientów\n");
        }

        synchronized (this) {
            while (criticalSectionOccupied) {
                wait();
            }
        }

        for (Client c : connectedClients) {
            if (!c.equals(clientId)) {
                c.request(clientId);
                clientId.receiveMessage("\tINFO\t\tOtrzymano komunikat\n");
                clientId.receiveMessage(
                        "\tMESSAGE\t\t[type=REPLY, timestamp=" + System.currentTimeMillis() + ", from=" + c + "]\n");
            }
        }

        criticalSectionOccupied = true;
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\t\tSekcja krytyczna zajęta\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tEVENT\t\t[type=HELD, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        clientId.receiveMessage("\tINFO\t\tJesteś w sekcji krytycznej\n");
    }

    @Override
    public synchronized String leaveCriticalSection(Client clientId) throws RemoteException {
        synchronized (this) {
            criticalSectionOccupied = false;
            this.notifyAll();
        }
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\t\tSekcja krytyczna wolna\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tEVENT\t\t[type=RELEASED, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        return "\tINFO\t\tOpuszczono sekcję krytyczną\n";
    }

    @Override
    public synchronized boolean isCriticalSectionOccupied() throws RemoteException {
        return criticalSectionOccupied;
    }

    public void kickAll() throws RemoteException {
        if (!connectedClients.isEmpty()) {
            for (Client c : connectedClients) {
                c.kick();
                logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                        + "\tEVENT\t\t[type=KICK, timestamp=" + System.currentTimeMillis() + ", clientID=" + c + "]\n");
            }
        }
    }

    public CopyOnWriteArrayList<Client> getConnectedClients() {
        return connectedClients;
    }
}