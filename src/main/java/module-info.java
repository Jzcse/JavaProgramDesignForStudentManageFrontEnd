module com.teach.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.logging;
    requires com.google.gson;
    requires java.net.http;

    opens com.teach.javafx to javafx.fxml;
    opens com.teach.javafx.request to com.google.gson, javafx.fxml;
    opens com.teach.javafx.controller.base to com.google.gson, javafx.fxml;
    opens com.teach.javafx.controller to com.google.gson, javafx.fxml;
    opens com.teach.javafx.models to javafx.base,com.google.gson;
    opens com.teach.javafx.util to com.google.gson, javafx.fxml;

    exports com.teach.javafx;
    exports com.teach.javafx.controller;
    exports com.teach.javafx.controller.base;
    exports com.teach.javafx.request;
    exports com.teach.javafx.util;

}