package com.teach.javafx.controller.studentEnd;


import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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

    /**
     * open a new window for students to request for leave
     * @param mouseEvent 监听到的鼠标事件
     */
    public void onLeaveBoxClick(MouseEvent mouseEvent)  {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/student-service-leave-request-panel.fxml"));
            Parent newRoot = loader.load();
            Stage stage = new Stage();
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.setResizable(false);
            stage.setTitle("学生请假请求填写");
            Scene scene = new Scene(newRoot, 500, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
