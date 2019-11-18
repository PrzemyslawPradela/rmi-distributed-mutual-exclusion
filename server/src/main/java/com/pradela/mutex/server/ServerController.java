package com.pradela.mutex.server;

import com.pradela.mutex.utils.DigitsValidator;
import com.pradela.mutex.utils.IpAddressValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerController {
    private final IpAddressValidator ipAddressValidator = new IpAddressValidator();
    private final DigitsValidator digitsValidator = new DigitsValidator();
    private final Alert errorAlert = new Alert(AlertType.ERROR);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Registry registry;
    private ServerApi server;
    private boolean registryRunning = false;

    @FXML
    private TextArea logsTextArea;

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button startBtn;

    @FXML
    private Button stopBtn;

    @FXML
    void initialize() {
        errorAlert.setTitle("Błąd");

        startBtn.setOnAction(a -> {
            if (ipTextField.getText().isEmpty()) {
                logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                        + "\tERROR\t\tNie podano adresu IP serwera\n");
                errorAlert.setHeaderText("Pole adresu IP nie może być puste!");
                errorAlert.showAndWait();
            } else if (portTextField.getText().isEmpty()) {
                logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                        + "\tERROR\t\tNie podano portu serwera\n");
                errorAlert.setHeaderText("Pole portu może być puste!");
                errorAlert.showAndWait();
            } else if (nameTextField.getText().isEmpty()) {
                logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                        + "\tERROR\t\tNie podano nazwy serwera\n");
                errorAlert.setHeaderText("Pole nazwy nie może być puste!");
                errorAlert.showAndWait();
            } else if (ipAddressValidator.validate(ipTextField.getText())) {
                logsTextArea.appendText(
                        dateFormat.format(new Date(System.currentTimeMillis())) + "\tERROR\t\tZły format adresu IP\n");
                errorAlert.setHeaderText("Zły format adresu IP!");
                errorAlert.showAndWait();
            } else if (digitsValidator.validate(portTextField.getText())) {
                logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                        + "\tERROR\t\tZły format numeru portu\n");
                errorAlert.setHeaderText("Numer portu może zawierać tylko cyfry!");
                errorAlert.showAndWait();
            } else {
                System.setProperty("java.rmi.server.hostname", ipTextField.getText());

                try {
                    server = new ServerApi(logsTextArea);
                    registry = LocateRegistry.createRegistry(Integer.parseInt(portTextField.getText()));
                    Naming.rebind("rmi://" + ipTextField.getText() + ":" + portTextField.getText() + "/"
                            + nameTextField.getText(), server);
                    logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                            + "\tINFO\t\tSerwer został uruchomiony\n");

                    registryRunning = true;

                    ipTextField.setDisable(true);
                    portTextField.setDisable(true);
                    nameTextField.setDisable(true);

                    stopBtn.setDisable(false);
                    startBtn.setDisable(true);
                } catch (RemoteException | MalformedURLException e) {
                    logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                            + "\tERROR\t\tNie można uruchomić serwera. Port w użyciu lub nieprawidłowy adres IP\n");
                    errorAlert.setHeaderText("Nie można uruchomić serwera!");
                    errorAlert.setContentText("Port w użyciu lub nieprawidłowy adres IP");
                    errorAlert.showAndWait();
                    e.printStackTrace();
                }
            }
        });

        stopBtn.setOnAction(a -> {
            try {
                stopBtn.setDisable(true);
                server.kickAll();
                UnicastRemoteObject.unexportObject(registry, true);
                logsTextArea.appendText(dateFormat.format(new Date(System.currentTimeMillis()))
                        + "\tINFO\t\tSerwer został wyłączony\n");

                registryRunning = false;

                ipTextField.setDisable(false);
                portTextField.setDisable(false);
                nameTextField.setDisable(false);

                startBtn.setDisable(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    void handleExit() throws RemoteException {
        if (registryRunning) {
            server.kickAll();
            UnicastRemoteObject.unexportObject(registry, true);
            Platform.exit();
            System.exit(0);
        } else {
            Platform.exit();
            System.exit(0);
        }
    }
}