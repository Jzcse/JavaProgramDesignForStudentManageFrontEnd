module com.teach.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires com.google.gson;
    requires java.net.http;
    requires jdk.security.jgss;
    requires com.fasterxml.jackson.databind;
    requires org.apache.pdfbox;
    requires easyexcel.core;
//    requires kernel;
//    requires layout;
    requires java.compiler;
    requires com.github.librepdf.openpdf;

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
    exports com.teach.javafx.controller.studentEnd;
    exports com.teach.javafx.controller.inner;
    opens com.teach.javafx.controller.studentEnd to com.google.gson, javafx.fxml;
    opens com.teach.javafx.controller.inner to com.google.gson, javafx.fxml;

    opens com.teach.javafx.controller.studentEnd.inner to javafx.fxml;
    exports com.teach.javafx.controller.studentEnd.inner;

    opens com.teach.javafx.controller.teacherEnd to javafx.fxml;
    exports com.teach.javafx.controller.teacherEnd;

    opens com.teach.javafx.controller.teacherEnd.inner to javafx.fxml;
    exports com.teach.javafx.controller.teacherEnd.inner;

}