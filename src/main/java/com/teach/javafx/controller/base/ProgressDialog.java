package com.teach.javafx.controller.base;

import com.teach.javafx.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * MessageDialog 消息对话框工具类 可以显示提示信息，用户选择确认信息和PDF显示
 */
public class ProgressDialog {

    private  ProgressController progressController= null;
    private static ProgressDialog instance = new ProgressDialog();


    private ProgressDialog() {
        FXMLLoader fxmlLoader ;
        Scene scene = null;
        Stage stage;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/progress-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 300, 100);
            stage = new Stage();
            stage.initOwner(null);
            stage.setScene(scene);
            stage.setTitle("进度显示对话框");
            progressController = (ProgressController) fxmlLoader.getController();
            progressController.setStage(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showDialog(ProgressProcessor progressProcessor, int size) {
        if(instance == null)
            return;
        if(instance.progressController == null)
            return;
        instance.progressController.showDialog(progressProcessor,size);
    }

}
