package com.teach.javafx.controller;


import javafx.fxml.FXML;

import javafx.scene.control.Button;

public class StudentServiceController {
    @FXML
    private Button mainButton;

    public void initialize() {
        // Initialize the controller
        System.err.println("successful initialization");
    }

    public void onMainButtonClick() {
        // Logic to handle the main button action
        System.err.println("Main button clicked!");
    }
}
