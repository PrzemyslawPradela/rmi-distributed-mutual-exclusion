package rmi.mutex.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import rmi.mutex.api.Server;
import rmi.mutex.utils.DigitsValidator;
import rmi.mutex.utils.IpAddressValidator;

public class ClientController {
    private IpAddressValidator ipAddressValidator = new IpAddressValidator();
    private DigitsValidator digitsValidator = new DigitsValidator();
    private Alert errorAlert = new Alert(AlertType.ERROR);
    private boolean connected = false;
    private ClientApi client;
    private Server server;

    @FXML
    private TextArea logsTextArea;

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField hostTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField serverNameTextField;

    @FXML
    private Label criticalSectionStatus;

    @FXML
    private Button connectBtn;

    @FXML
    private Button disconnectBtn;

    @FXML
    private Button enterCriticalSectionBtn;

    @FXML
    private Button leaveCriticalSectionBtn;

    @FXML
    void initialize() {
        errorAlert.setTitle("Błąd");

        connectBtn.setOnAction(a -> {
            if (ipTextField.getText().isEmpty()) {
                logsTextArea.appendText(client.getDateFormat()
                        .format(new Date(System.currentTimeMillis()) + "\tERROR\tNie podano adresu IP\n"));
                errorAlert.setHeaderText("Pole adresu IP nie może być puste!");
                errorAlert.showAndWait();
            } else if (hostTextField.getText().isEmpty()) {
                logsTextArea.appendText(client.getDateFormat()
                        .format(new Date(System.currentTimeMillis()) + "\tERROR\tNie podano adresu IP serwera\n"));
                errorAlert.setHeaderText("Pole adresu IP serwera nie może być puste!");
                errorAlert.showAndWait();
            } else if (portTextField.getText().isEmpty()) {
                logsTextArea.appendText(client.getDateFormat()
                        .format(new Date(System.currentTimeMillis()) + "\tERROR\tNie podano portu serwera\n"));
                errorAlert.setHeaderText("Pole portu może być puste!");
                errorAlert.showAndWait();
            } else if (serverNameTextField.getText().isEmpty()) {
                logsTextArea.appendText(client.getDateFormat()
                        .format(new Date(System.currentTimeMillis()) + "\tERROR\tNie podano nazwy serwera\n"));
                errorAlert.setHeaderText("Pole nazwy serwera nie może być puste!");
                errorAlert.showAndWait();
            } else if (!ipAddressValidator.validate(ipTextField.getText())
                    || !ipAddressValidator.validate(hostTextField.getText())) {
                logsTextArea.appendText(client.getDateFormat()
                        .format(new Date(System.currentTimeMillis()) + "\tERROR\tZły format adresu IP\n"));
                errorAlert.setHeaderText("Zły format adresu IP!");
                errorAlert.showAndWait();
            } else if (!digitsValidator.validate(portTextField.getText())) {
                logsTextArea.appendText(client.getDateFormat()
                        .format(new Date(System.currentTimeMillis()) + "\tERROR\tZły format numeru portu\n"));
                errorAlert.setHeaderText("Numer portu może zawierać tylko cyfry!");
                errorAlert.showAndWait();
            } else {
                // System.setProperty("java.rmi.server.hostname", ipTextField.getText());
                try {
                    client = new ClientApi(logsTextArea);
                    Registry registry = LocateRegistry.getRegistry(hostTextField.getText(),
                            Integer.parseInt(portTextField.getText()));
                    server = (Server) registry.lookup(serverNameTextField.getText());
                    logsTextArea.appendText(client.getDateFormat().format(new Date(System.currentTimeMillis()))
                            + server.connect(client));

                    connected = true;

                    ipTextField.setDisable(true);
                    hostTextField.setDisable(true);
                    portTextField.setDisable(true);
                    serverNameTextField.setDisable(true);

                    connectBtn.setDisable(true);
                    disconnectBtn.setDisable(false);
                    enterCriticalSectionBtn.setDisable(false);

                    Task<Void> refreshCriticalSectionStatus = new Task<Void>() {
                        @Override
                        public Void call() throws Exception {
                            while (true) {
                                Platform.runLater(() -> {
                                    try {
                                        if (server.isCriticalSectionOccupied()) {
                                            criticalSectionStatus.setText("ZAJĘTA");
                                            criticalSectionStatus.setTextFill(Color.RED);
                                        } else {
                                            criticalSectionStatus.setText("WOLNA");
                                            criticalSectionStatus.setTextFill(Color.GREEN);
                                        }
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                });
                                Thread.sleep(1000);
                            }
                        }
                    };
                    Thread th = new Thread(refreshCriticalSectionStatus);
                    th.setDaemon(true);
                    th.start();

                } catch (RemoteException | NotBoundException e) {
                    logsTextArea.appendText(client.getDateFormat().format(
                            new Date(System.currentTimeMillis()) + "\tERROR\tNie udało się połączyć z serwerem\n"));
                    errorAlert.setHeaderText("Nie można połączyć z serwerem!");
                    errorAlert.showAndWait();
                    e.printStackTrace();
                }
            }
        });

        disconnectBtn.setOnAction(event -> {
            try {
                logsTextArea.appendText(client.getDateFormat().format(new Date(System.currentTimeMillis()))
                        + server.disconnect(client));
                connected = false;

                ipTextField.setDisable(false);
                hostTextField.setDisable(false);
                portTextField.setDisable(false);
                serverNameTextField.setDisable(false);

                connectBtn.setDisable(false);
                disconnectBtn.setDisable(true);
                enterCriticalSectionBtn.setDisable(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        enterCriticalSectionBtn.setOnAction(a -> {
            logsTextArea.appendText(client.getDateFormat().format(new Date(System.currentTimeMillis()))
                    + "\tINFO\tZgłoszono żądanie wejścia do sekcji krytycznej\n");
            try {
                server.enterCriticalSection(client);
                client.enterCriticalSection();

                leaveCriticalSectionBtn.setDisable(false);
                enterCriticalSectionBtn.setDisable(true);
                disconnectBtn.setDisable(true);
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        leaveCriticalSectionBtn.setOnAction(a -> {
            try {
                logsTextArea.appendText(client.getDateFormat().format(new Date(System.currentTimeMillis()))
                        + server.leaveCriticalSection(client));
                client.leaveCriticalSection();

                leaveCriticalSectionBtn.setDisable(true);
                enterCriticalSectionBtn.setDisable(false);
                disconnectBtn.setDisable(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleExit() throws RemoteException {
        if (connected) {
            if (client.isInCriticalSection()) {
                server.leaveCriticalSection(client);
            } else {
                server.disconnect(client);
                Platform.exit();
                System.exit(0);
            }
        } else {
            Platform.exit();
            System.exit(0);
        }
    }
}