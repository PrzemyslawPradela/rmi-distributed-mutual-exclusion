package mutex.rmi.server.main;

import java.io.IOException;
import java.rmi.NoSuchObjectException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartServer extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("server.fxml"));
        Image icon = new Image(this.getClass().getResourceAsStream("server.png"));
        Parent root = fxmlLoader.load();
        ServerController fxmController = fxmlLoader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("Server");
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            try {
                fxmController.handleExit();
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}