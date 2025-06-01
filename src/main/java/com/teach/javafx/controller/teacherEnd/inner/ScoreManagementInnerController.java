package com.teach.javafx.controller.teacherEnd.inner;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.controller.studentEnd.GlobalSession;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreManagementInnerController extends ToolController {
    @FXML
    private Label usualWeight;
    @FXML
    private Label midWeight;
    @FXML
    private Label endWeight;
    @FXML
    private TableColumn<Map<String, String>, String> num;
    @FXML
    private TableColumn<Map<String, String>, String> name;
    @FXML
    private TableColumn<Map<String, String>, String> classname;
    @FXML
    private TableColumn<Map<String, String>, String> usualScore;
    @FXML
    private TableColumn<Map<String, String>, String> midTermScore;
    @FXML
    private TableColumn<Map<String, String>, String> endTermScore;
    @FXML
    private TableColumn<Map<String, String>, String> score;
    @FXML
    private TableView<Map<String,String>> tableView;
    @FXML
    private Label courseName;

    private String teacherId, courseId, studentId;
    private ArrayList<Map<String, String>> arrayList = new ArrayList<>();
    private ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList();

    public void initialize(String courseId) {
        this.teacherId = GlobalSession.getInstance().getTeacherId();
        this.courseId = courseId;

        num.setCellValueFactory(new MapValueFactory("studentNum"));
        name.setCellValueFactory(new MapValueFactory("studentName"));
        classname.setCellValueFactory(new MapValueFactory("className"));
        usualScore.setCellValueFactory(new MapValueFactory("markOfPerformance"));
        midTermScore.setCellValueFactory(new MapValueFactory("markOfMidTerm"));
        endTermScore.setCellValueFactory(new MapValueFactory("markOfFinalTerm"));
        score.setCellValueFactory(new MapValueFactory("totalScore"));

        TableView.TableViewSelectionModel<Map<String, String>> tableViewSelectionModel = tableView.getSelectionModel();
        ObservableList<Integer> list = tableViewSelectionModel.getSelectedIndices();
        list.addListener(this::onRowSelect);


        getCourseName();
        getData();
        setTableView();
        setWeights();
    }

    private void onRowSelect(Observable observable) {
        this.studentId = tableView.getSelectionModel().getSelectedItem().get("studentId");
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/teacher-end/inner/score-management-innerinner-panel.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setWidth(400);
            stage.setHeight(300);
            stage.setResizable(false);
            stage.setTitle("成绩");
            Scene scene = new Scene(root, 1000,600);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            //
            ScoreManagementInnerInnerController scoreManagementInnerInnerController = loader.getController();
            scoreManagementInnerInnerController.initialize(studentId, courseId);
            stage.showAndWait();

            getData();
            setTableView();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    private void getCourseName() {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        dataRequest.add("course", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/course/getSingleCourseAndScore", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                Map<String, String> result = (Map<String, String>) dataResponse.getData();
                courseName.setText(CommonMethod.getString(result, "name"));
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接!");
        }
    }

    private void getData() {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("teacherId", teacherId);
        map.put("courseId", courseId);
        dataRequest.add("score", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/score/getStudentAndScore", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                arrayList = (ArrayList<Map<String, String>>) dataResponse.getData();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接！");
        }
    }

    private void setTableView() {
        observableList.clear();
        for(Map<String, String> stringStringMap : arrayList) {
            observableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        tableView.setItems(observableList);
    }

    private void setWeights() {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        dataRequest.add("course", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/course/getSingleCourseAndScore", dataRequest);
        if (dataResponse!= null) {
            if (dataResponse.getCode() == 0) {
                Map<String, String> result = (Map<String, String>) dataResponse.getData();
                usualWeight.setText(CommonMethod.getString(result, "weightOfPerformance"));
                midWeight.setText(CommonMethod.getString(result, "weightOfMidTerm"));
                endWeight.setText(CommonMethod.getString(result, "weightOfFinal"));
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接！");
        }
    }

    public void onChangeClick(ActionEvent actionEvent) {
        // 创建新窗口
        Stage stage = new Stage();
        stage.setTitle("设置权重");

        // 创建三个 Label 和对应的输入框
        Label label1 = new Label("平时成绩权重");
        TextField textField1 = new TextField();
        Label label2 = new Label("期中成绩权重");
        TextField textField2 = new TextField();
        Label label3 = new Label("期末成绩权重");
        TextField textField3 = new TextField();

        // 创建提交按钮
        Button submitButton = new Button("提交");
        submitButton.setOnAction(e -> {
            // 获取输入框的值
            String value1 = textField1.getText();
            String value2 = textField2.getText();
            String value3 = textField3.getText();
            DataRequest dataRequest = new DataRequest();
            Map<String, String> map = new HashMap<>();
            map.put("courseId", courseId);
            map.put("performanceWeight", value1);
            map.put("midTermWeight", value2);
            map.put("finalTermWeight", value3);
            dataRequest.add("score", map);
            DataResponse dataResponse = HttpRequestUtil.request("/api/score/setWeightByCourse", dataRequest);
            if (dataResponse!= null) {
                if (dataResponse.getCode() == 0) {
                    MessageDialog.showDialog("设置成功！");
                    stage.close();
                    getData();
                    setTableView();
                    setWeights();
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            } else {
                MessageDialog.showDialog("请检查网络连接！");
            }

            System.out.println("平时成绩权重: " + value1);
            System.out.println("期中成绩权重: " + value2);
            System.out.println("期末成绩权重: " + value3);
        });
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(label1, textField1, label2, textField2, label3, textField3, submitButton);

        Scene scene = new Scene(vbox, 300, 300);
        stage.setScene(scene);
        stage.show();
    }
}
