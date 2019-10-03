module rmi.mutex.client {
    requires java.rmi;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.lang3;
    requires transitive rmi.mutex.api;
    requires rmi.mutex.utils;

    opens rmi.mutex.client to javafx.fxml;

    exports rmi.mutex.client;
}