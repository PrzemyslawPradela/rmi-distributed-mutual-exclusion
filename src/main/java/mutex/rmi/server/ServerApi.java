package mutex.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.scene.control.TextArea;
import mutex.rmi.api.Client;
import mutex.rmi.api.Server;

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
        // int noOfReplies = 0;
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\t" + clientId
                + " zgłosił żądanie wejścia do sekcji krytycznej\n");
        if (!connectedClients.isEmpty()) {
            clientId.receiveMessage("\tINFO\tWysyłam komunikat REQUEST do pozostałych klientów\n");
            clientId.receiveMessage("\tINFO\tOczekiwanie na odpowiedź od pozostałych klientów\n");
            for (Client c : connectedClients) {
                if (!c.equals(clientId)) {
                    c.request(clientId);
                    clientId.receiveMessage("\tINFO\tOtrzymano komunikat\n");
                    clientId.receiveMessage(
                            "\tMESSAGE\t[type=REPLY, timestamp=" + System.currentTimeMillis() + ", from=" + c + "]\n");
                }
            }
        }
        criticalSectionOccupied = true;
        return "\tINFO\tJesteś w sekcji krytycznej\n";
    }

    @Override
    public String leaveCriticalSection(Client clientId) throws RemoteException {
        criticalSectionOccupied = false;
        return "\tINFO\tOpuszczono sekcję krytyczną\n";
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}