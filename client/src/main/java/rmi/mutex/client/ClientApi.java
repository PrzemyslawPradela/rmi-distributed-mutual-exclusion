package rmi.mutex.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import rmi.mutex.api.Client;
import rmi.mutex.api.Server;

public class ClientApi extends UnicastRemoteObject implements Client {
    private static final long serialVersionUID = -6933774609374036970L;
    private boolean inCriticalSection;
    private boolean connected;
    private SimpleDateFormat dateFormat;
    private TextArea logsTextArea;
    private CopyOnWriteArrayList<Button> buttonsList;
    private CopyOnWriteArrayList<TextField> txtFieldsList;
    private Server server;

    public ClientApi(TextArea logsTextArea, CopyOnWriteArrayList<Button> buttonsList,
            CopyOnWriteArrayList<TextField> txtFieldsList) throws RemoteException {
        this.inCriticalSection = false;
        this.connected = false;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.logsTextArea = logsTextArea;
        this.buttonsList = buttonsList;
        this.txtFieldsList = txtFieldsList;
    }

    @Override
    public synchronized int request(Client clientId) throws RemoteException {
        while (inCriticalSection) {
        }
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\t\tOtrzymano komunikat\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tMESSAGE\t\t[type=REQUEST, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\t\tWysłano odpowiedź na komunikat\n");
        return 1;
    }

    @Override
    public synchronized void receiveMessage(String message) throws RemoteException {
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis())) + message);
    }

    @Override
    public synchronized void kick() throws RemoteException {
        if (inCriticalSection) {
            server.leaveCriticalSection(this);
            inCriticalSection = false;
            Platform.runLater(() -> {
                buttonsList.get(2).setDisable(true);
                buttonsList.get(3).setDisable(true);
            });
        }
        Platform.runLater(() -> {
            txtFieldsList.forEach(tf -> tf.setDisable(false));
            buttonsList.get(0).setDisable(false);
            buttonsList.get(1).setDisable(true);
            buttonsList.get(2).setDisable(true);
        });
        server.disconnect(this);
        connected = false;
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tINFO\t\tPołączenie z serwerem zostało przerwane\n");
        server = null;
        System.gc();
        System.runFinalization();
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

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        connected = false;
    }

    public void connect() {
        connected = true;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}