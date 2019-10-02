module mutex.rmi {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires transitive javafx.graphics;

    opens mutex.rmi.server.main to javafx.fxml;
    opens mutex.rmi.client.main to javafx.fxml;

    exports mutex.rmi.api;
    exports mutex.rmi.client;
    exports mutex.rmi.client.main;
    exports mutex.rmi.server;
    exports mutex.rmi.server.main;
    exports mutex.rmi.utils;
}