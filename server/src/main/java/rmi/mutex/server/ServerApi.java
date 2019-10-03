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
    private static final long serialVersionUID = 1L;
    private CopyOnWriteArrayList<Client> connectedClients;
    private SimpleDateFormat dateFormat;
    private boolean criticalSectionOccupied;
    private TextArea logsTextArea;

    public ServerApi(TextArea logsTextArea) throws RemoteException {
        this.connectedClients = new CopyOnWriteArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.criticalSectionOccupied = false;
        this.logsTextArea = logsTextArea;
    }

    @Override
    public synchronized String connect(Client clientId) throws RemoteException {
        connectedClients.add(clientId);
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tINFO\tNowy klient nawiązał połączenie z serwerem\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tEVENT\t[type=CONNECTION, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        return "\tINFO\tPołączono z serwerem\n";
    }

    @Override
    public synchronized String disconnect(Client clientId) throws RemoteException {
        connectedClients.remove(clientId);
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tINFO\tJeden klient zakończył połączenie z serwerem\n");
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tEVENT\t[type=DISCONNECTION, timestamp="
                        + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        return "\tINFO\tRozłączono z serwerem\n";
    }

    @Override
    public synchronized String enterCriticalSection(Client clientId) throws RemoteException {
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tINFO\tJeden klient zgłosił żądanie wejścia do sekcji krytycznej\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tEVENT\t[type=WANTED, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");

        if (connectedClients.size() > 1) {
            clientId.receiveMessage("\tINFO\tWysyłam komunikat REQUEST do pozostałych klientów\n");
            clientId.receiveMessage("\tINFO\tOczekiwanie na odpowiedź od pozostałych klientów\n");
            for (Client c : connectedClients) {
                if (!c.equals(clientId)) {
                    c.request(clientId);
                    clientId.receiveMessage("\tINFO\tOtrzymano komunikat\n");
                    clientId.receiveMessage(
                            "\tMSG\t[type=REPLY, timestamp=" + System.currentTimeMillis() + ", from=" + c + "]\n");
                }
            }
        }
        criticalSectionOccupied = true;
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\tSekcja krytyczna zajęta\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tEVENT\t[type=HELD, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        return "\tINFO\tJesteś w sekcji krytycznej\n";
    }

    @Override
    public synchronized String leaveCriticalSection(Client clientId) throws RemoteException {
        criticalSectionOccupied = false;
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\tSekcja krytyczna wolna\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tEVENT\t[type=RELEASED, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        return "\tINFO\tOpuszczono sekcję krytyczną\n";
    }

    @Override
    public synchronized boolean isCriticalSectionOccupied() throws RemoteException {
        return criticalSectionOccupied;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}