package com.teach.javafx.controller.teacherEnd.inner;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ScoreManagementInnerInnerController extends ToolController {

    @FXML
    private Label classname;
    @FXML
    private TextField usualField;
    @FXML
    private TextField midField;
    @FXML
    private TextField endField;
    @FXML
    private Label name;
    @FXML
    private Label num;


    private String studentId, courseId;

    public void initialize(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;

        getDataAndSet();
    }

    private void getDataAndSet() {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("studentId", studentId);
        map.put("courseId", courseId);
        dataRequest.add("score", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/score/getSingleScoreAndStudent", dataRequest);
        if (dataResponse!= null) {
            if (dataResponse.getCode() == 0) {
                Map<String, String> result = (Map<String, String>) dataResponse.getData();
                name.setText(CommonMethod.getString(result, "studentName"));
                num.setText(CommonMethod.getString(result, "studentNum"));
                classname.setText(CommonMethod.getString(result, "className"));
                usualField.setText(CommonMethod.getString(result, "markOfPerformance"));
                midField.setText(CommonMethod.getString(result, "markOfMidTerm"));
                endField.setText(CommonMethod.getString(result, "markOfFinalTerm"));
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接！");
        }
    }

    public void registerScores(ActionEvent actionEvent) {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("studentId", studentId);
        map.put("courseId", courseId);
        map.put("markOfPerformance", usualField.getText());
        map.put("markOfMidTerm", midField.getText());
        map.put("markOfFinalTerm", endField.getText());
        dataRequest.add("score", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/score/rankScore", dataRequest);
        if (dataResponse!= null) {
            if(dataResponse.getCode() == 0){
                MessageDialog.showDialog("成绩录入成功");
                closeWindow();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接！");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) usualField.getScene().getWindow();
        stage.close();
    }
}
