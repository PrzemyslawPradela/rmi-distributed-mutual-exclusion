module rmi.mutex.server {
    requires java.rmi;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive rmi.mutex.api;
    requires rmi.mutex.utils;

    opens rmi.mutex.server to javafx.fxml;

    exports rmi.mutex.server;
}