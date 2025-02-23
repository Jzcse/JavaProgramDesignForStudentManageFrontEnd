package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreTableController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> studentNumColumn;
    @FXML
    private TableColumn<Map,String> studentNameColumn;
    @FXML
    private TableColumn<Map,String> classNameColumn;
    @FXML
    private TableColumn<Map,String> courseNumColumn;
    @FXML
    private TableColumn<Map,String> courseNameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> markColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;


    private ArrayList<Map> scoreList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private ComboBox<OptionItem> studentComboBox;


    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;


    private List<OptionItem> courseList;

    private ScoreEditController scoreEditController = null;
    private Stage stage = null;
    public List<OptionItem> getStudentList() {
        return studentList;
    }
    public List<OptionItem> getCourseList() {
        return courseList;
    }


    @FXML
    private void onQueryButtonClick(){
        Integer personId = 0;
        Integer courseId = 0;
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            personId = Integer.parseInt(op.getValue());
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            courseId = Integer.parseInt(op.getValue());
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("personId",personId);
        req.add("courseId",courseId);
        res = HttpRequestUtil.request("/api/score/getScoreList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            scoreList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map map;
        Button editButton;
        for (int j = 0; j < scoreList.size(); j++) {
            map = scoreList.get(j);
            editButton = new Button("编辑");
            editButton.setId("edit"+j);
            editButton.setOnAction(e->{
                editItem(((Button)e.getSource()).getId());
            });
            map.put("edit",editButton);
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }
    public void editItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length()));
        Map data = scoreList.get(j);
        initDialog();
        scoreEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();

    }
    @FXML
    public void initialize() {


        studentNumColumn.setCellValueFactory(new MapValueFactory("studentNum"));  //设置列值工程属性
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));

        DataRequest req =new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/score/getStudentItemOptionList",req); //从后台获取所有学生信息列表集合
        courseList = HttpRequestUtil.requestOptionItemList("/api/score/getCourseItemOptionList",req); //从后台获取所有学生信息列表集合
        OptionItem item = new OptionItem(null,"0","请选择");
        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);
        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader ;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("score-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("成绩录入对话框！");
            stage.setOnCloseRequest(event ->{
                MainApplication.setCanClose(true);
            });
            scoreEditController = (ScoreEditController) fxmlLoader.getController();
            scoreEditController.setScoreTableController(this);
            scoreEditController.init();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void doClose(String cmd, Map data) {
        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer personId = CommonMethod.getInteger(data,"personId");
        if(personId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }
        Integer courseId = CommonMethod.getInteger(data,"courseId");
        if(courseId == null) {
            MessageDialog.showDialog("没有选中课程不能添加保存！");
            return;
        }
        DataRequest req =new DataRequest();
        req.add("personId",personId);
        req.add("courseId",courseId);
        req.add("scoreId",CommonMethod.getInteger(data,"scoreId"));
        req.add("mark",CommonMethod.getInteger(data,"mark"));
        res = HttpRequestUtil.request("/api/score/scoreSave",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            onQueryButtonClick();
        }
    }
    @FXML
    private void onAddButtonClick() {
        initDialog();
        scoreEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onEditButtonClick() {
//        dataTableView.getSelectionModel().getSelectedItems();
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        scoreEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer scoreId = CommonMethod.getInteger(form,"scoreId");
        DataRequest req = new DataRequest();
        req.add("scoreId", scoreId);
        DataResponse res = HttpRequestUtil.request("/api/score/scoreDelete",req);
        if(res.getCode() == 0) {
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

}