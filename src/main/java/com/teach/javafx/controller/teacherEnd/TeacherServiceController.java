package com.teach.javafx.controller.teacherEnd;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TeacherServiceController {
    public void initialize() {

    }

    public void onCourseChooseBoxClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/teacher-end/course-choose-panel.fxml"));
            Parent newRoot = loader.load();
            Stage stage = new Stage();
            stage.setWidth(1200);
            stage.setHeight(700);
            stage.setResizable(false);
            stage.setTitle("任课系统");
            Scene scene = new Scene(newRoot, 1000,600);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }

    public void onInfoBoxClick(MouseEvent mouseEvent) {

    }

    public void onScoreManagementBoxClick(MouseEvent mouseEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/teacher-end/score-management-panel.fxml"));
            Parent newRoot = loader.load();
            Stage stage = new Stage();
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.setTitle("分数管理");
            Scene scene = new Scene(newRoot, 1000,600);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }
}
