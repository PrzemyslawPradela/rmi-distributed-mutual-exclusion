package com.pradela.mutex.client;

import com.pradela.mutex.api.Client;
import com.pradela.mutex.api.Server;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

class ClientApi extends UnicastRemoteObject implements Client {
    private static final long serialVersionUID = -6933774609374036970L;
    private final SimpleDateFormat dateFormat;
    private boolean connected;
    private final TextArea logsTextArea;
    private final CopyOnWriteArrayList<Button> buttonsList;
    private final CopyOnWriteArrayList<TextField> txtFieldsList;
    private volatile boolean inCriticalSection;
    private Server server;

    ClientApi(TextArea logsTextArea, CopyOnWriteArrayList<Button> buttonsList,
              CopyOnWriteArrayList<TextField> txtFieldsList) throws RemoteException {
        this.inCriticalSection = false;
        this.connected = false;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.logsTextArea = logsTextArea;
        this.buttonsList = buttonsList;
        this.txtFieldsList = txtFieldsList;
    }

    @Override
    public synchronized void request(Client clientId) {
        while (inCriticalSection) ;
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\t\tOtrzymano komunikat\n");
        logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                + "\tMESSAGE\t\t[type=REQUEST, timestamp=" + System.currentTimeMillis() + ", from=" + clientId + "]\n");
        logsTextArea.appendText(
                dateFormat.format(new Date(System.currentTimeMillis())) + "\tINFO\t\tWysłano odpowiedź na komunikat\n");
    }

    @Override
    public synchronized void receiveMessage(String message) {
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

    boolean isInCriticalSection() {
        return inCriticalSection;
    }

    void enterCriticalSection() {
        inCriticalSection = true;
    }

    void leaveCriticalSection() {
        inCriticalSection = false;
    }

    boolean isConnected() {
        return connected;
    }

    void disconnect() {
        connected = false;
    }

    void connect() {
        connected = true;
    }

    void setServer(Server server) {
        this.server = server;
    }
}