package rmi.mutex.client;

import java.io.IOException;
import java.rmi.RemoteException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartClient extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("client.fxml"));
        Image icon = new Image(this.getClass().getResourceAsStream("client.png"));
        Parent root = fxmlLoader.load();
        ClientController fxmController = fxmlLoader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("Client");
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(close -> {
            try {
                fxmController.handleExit();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}