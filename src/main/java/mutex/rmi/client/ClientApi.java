package mutex.rmi.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.control.TextArea;
import mutex.rmi.api.Client;

public class ClientApi extends UnicastRemoteObject implements Client {
    private static final long serialVersionUID = 8817066730528372707L;
    private boolean inCriticalSection;
    private SimpleDateFormat dateFormat;
    private TextArea logsTextArea;

    public ClientApi(TextArea logsTextArea) throws RemoteException {
        this.inCriticalSection = false;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.logsTextArea = logsTextArea;
    }

    @Override
    public synchronized int request(Client clientId) throws RemoteException {
        while (inCriticalSection) {
        }
        logsTextArea
                .appendText(dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\tOtrzymano komunikat\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tMSG\t[type=REQUEST, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\tWysłano odpowiedź na komunikat\n");
        return 1;
    }

    @Override
    public synchronized void receiveMessage(String message) throws RemoteException {
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis())) + message);
    }

    public boolean isInCriticalSection() {
        return inCriticalSection;
    }

    public void enterCriticalSection() {
        inCriticalSection = true;
    }

    public void leaveCriticalSection() {
        inCriticalSection = false;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}