package com.teach.javafx.controller.base;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;




public class ProgressController {
    class ProcessThread extends Thread{
        public void run(){
            for(int i = 0; i <size;i++) {
                progressProcessor.step(i);
                progressBar.setProgress((double)i/size);
                try {
                    sleep(100);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int size=100;
    private ProgressProcessor progressProcessor= null;
    @FXML
    private ProgressBar progressBar;
    private ProcessThread processThread= null;
    private Stage stage;
    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    @FXML
    public void initialize() {
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void showDialog(ProgressProcessor progressProcessor, int size) {
        if(processThread == null) {
            this.progressProcessor =progressProcessor;
            this.size = size;
            processThread = new ProcessThread();
            processThread.start();
        }
        this.stage.show();
    }
}
