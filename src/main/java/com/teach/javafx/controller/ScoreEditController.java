package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import com.teach.javafx.controller.ScoreTableController;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageController 登录交互控制类 对应 base/message-dialog.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */

public class ScoreEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;
    private List<OptionItem> courseList;
    @FXML
    private ScoreTableController scoreTableController = null;
    private String scoreId;
    @FXML
    private TextField PerformanceMarkField;
    @FXML
    private TextField MidTermMarkField;
    @FXML
    private TextField FinalTermMarkField;
    @FXML
    private TextField PerformanceWeightField;
    @FXML
    private TextField MidTermWeightField;
    @FXML
    private TextField FinalTermWeightField;
    @FXML
    public void initialize() {
    }

    @FXML
    public void okButtonClick(){
        Map data = new HashMap();
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("personId",Integer.parseInt(op.getValue()));
        }
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("courseId", Integer.parseInt(op.getValue()));
        }
        data.put("scoreId",scoreId);
        data.put("PerformanceMark",PerformanceMarkField.getText());
        data.put("MidTermMark",MidTermMarkField.getText());
        data.put("FinalTermMark",FinalTermMarkField.getText());
        data.put("PerformanceWeight",PerformanceWeightField.getText());
        data.put("MidTermWeight",MidTermWeightField.getText());
        data.put("FinalTermWeight",FinalTermWeightField.getText());
        OnSetWeightClick(data);
        PerformanceMarkField.setText("");
        MidTermMarkField.setText("");
        FinalTermMarkField.setText("");
        PerformanceWeightField.setText("");
        MidTermWeightField.setText("");
        FinalTermWeightField.setText("");
        scoreTableController.doClose("ok",data);
    }
    @FXML
    public void OnSetWeightClick(Map form){
        if (form == null) {
            MessageDialog.showDialog("请选定要设置权重的课程");
        }else{
            Integer courseId = CommonMethod.getInteger(form, "courseId");
            String PerformanceWeight = CommonMethod.getString(form, "PerformanceWeight");
            String MidTermWeight = CommonMethod.getString(form, "MidTermWeight");
            String FinalTermWeight = CommonMethod.getString(form, "FinalTermWeight");
            String PerformanceMark = CommonMethod.getString(form, "PerformanceMark");
            String MidTermMark = CommonMethod.getString(form, "MidTermMark");
            String FinalTermMark = CommonMethod.getString(form, "FinalTermMark");
            String scoreId = CommonMethod.getString(form, "scoreId");
            String personId = CommonMethod.getString(form, "personId");
            if (PerformanceWeight == null || PerformanceWeight.isEmpty()) {
                MessageDialog.showDialog("请输入有效的绩效权重");
                return;
            }
            if (MidTermWeight == null || MidTermWeight.isEmpty()) {
                MessageDialog.showDialog("请输入有效的期中权重");
            }
            if (FinalTermWeight == null || FinalTermWeight.isEmpty()) {
                MessageDialog.showDialog("请输入有效的期末权重");
            }
            if (PerformanceMark == null || PerformanceMark.isEmpty()) {
                MessageDialog.showDialog("请输入有效的平时成绩");
            }
            if (MidTermMark == null || MidTermMark.isEmpty()) {
                MessageDialog.showDialog("请输入有效的期中成绩");
            }
            if (FinalTermMark == null || FinalTermMark.isEmpty()) {
                MessageDialog.showDialog("请输入有效的期末成绩");
            }

            Map<String, Object> WeightMap = new HashMap<>();

            WeightMap.put("PerformanceWeight", PerformanceWeight);
            WeightMap.put("MidTermWeight", MidTermWeight);
            WeightMap.put("FinalTermWeight", FinalTermWeight);
            WeightMap.put("courseId", courseId);
            WeightMap.put("PerformanceMark", PerformanceMark);
            WeightMap.put("MidTermMark", MidTermMark);
            WeightMap.put("FinalTermMark", FinalTermMark);
            WeightMap.put("scoreId", scoreId);
            WeightMap.put("personId", personId);
            DataRequest req = new DataRequest();
            req.add("WeightMap", WeightMap);
            DataResponse res = HttpRequestUtil.request("/api/score/setWeight", req);
            if (res!= null && res.getCode() == 0) {
                MessageDialog.showDialog("修改成功");
                MessageDialog.showDialog("设置权重成功");
            } else {
                MessageDialog.showDialog("修改失败: " + (res!= null? res.getMsg() : "请求失败"));
            }
        }
    }
    //---------------------------------------------//


    @FXML
    public void cancelButtonClick(){
        scoreTableController.doClose("cancel",null);
    }

    public void setScoreTableController(ScoreTableController scoreTableController) {
        this.scoreTableController = scoreTableController;
    }
    public void init(){
        studentList =scoreTableController.getStudentList();
        courseList = scoreTableController.getCourseList();
        studentComboBox.getItems().addAll(studentList );
        courseComboBox.getItems().addAll(courseList);
    }
    public void showDialog(Map data){
        if(data == null) {
            scoreId = null;
            studentComboBox.getSelectionModel().select(-1);
            courseComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            courseComboBox.setDisable(false);
            //markField.setText("");
        }else {
            scoreId = CommonMethod.getString(data,"scoreId");
            studentComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(studentList, CommonMethod.getString(data, "personId")));
            courseComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(courseList, CommonMethod.getString(data, "courseId")));
            studentComboBox.setDisable(true);
            courseComboBox.setDisable(true);
            //markField.setText(CommonMethod.getString(data, "mark"));
        }
    }

}
