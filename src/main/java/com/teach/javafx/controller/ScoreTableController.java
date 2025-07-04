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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @FXML
    private TableColumn<Map,String> performanceMarkColumn;
    @FXML
    private TableColumn<Map,String> midTermMarkColumn;
    @FXML
    private TableColumn<Map,String> finalTermMarkColumn;
    @FXML
    private TableColumn<Map,String> performanceWeightColumn;
    @FXML
    private TableColumn<Map,String> midTermWeightColumn;
    @FXML
    private TableColumn<Map,String> finalTermWeightColumn;

    @FXML
    private TextField courseIdTextField;
    @FXML
    private Button importButton;


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
        Integer studentId = 0;
        Integer courseId = 0;
        OptionItem op;
        op = studentComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            studentId = Integer.parseInt(op.getValue());
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            courseId = Integer.parseInt(op.getValue());
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("studentId", studentId);
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

        performanceMarkColumn.setCellValueFactory(new MapValueFactory<>("PerformanceMark"));
        midTermMarkColumn.setCellValueFactory(new MapValueFactory<>("MidTermMark"));
        finalTermMarkColumn.setCellValueFactory(new MapValueFactory<>("FinalTermMark"));
        performanceWeightColumn.setCellValueFactory(new MapValueFactory<>("PerformanceWeight"));
        midTermWeightColumn.setCellValueFactory(new MapValueFactory<>("MidTermWeight"));
        finalTermWeightColumn.setCellValueFactory(new MapValueFactory<>("FinalTermWeight"));

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
            scene = new Scene(fxmlLoader.load(), 400, 400);
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
        req.add("PerformanceWeight",CommonMethod.getInteger(data,"PerformanceWeight"));
        req.add("MidTermWeight",CommonMethod.getInteger(data,"MidTermWeight"));
        req.add("FinalTermWeight",CommonMethod.getInteger(data,"FinalTermWeight"));
        req.add("PerformanceMark",CommonMethod.getInteger(data,"PerformanceMark"));
        req.add("MidTermMark",CommonMethod.getInteger(data,"MidTermMark"));
        req.add("FinalTermMark",CommonMethod.getInteger(data,"FinalTermMark"));
        res = HttpRequestUtil.request("/api/score/scoreSave",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            MessageDialog.showDialog("保存成功");
            onQueryButtonClick();
        }
    }
    @FXML
    private void onAddButtonClick() {
        initDialog();
        scoreEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
        onQueryButtonClick();
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
    @FXML
    private void onImportButtonClick() {
        if (courseIdTextField.getText().isEmpty()) {
            MessageDialog.showDialog("请输入课程ID");
            return;
        }
        String courseNum = String.valueOf(courseIdTextField.getText());
        DataRequest req = new DataRequest();
        Map course = new HashMap();
        course.put("num", courseNum);
        req.add("course", course);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseIdByNum", req);
        Map result = (Map) res.getData();
        Integer courseId = CommonMethod.getInteger(result, "courseId");
        Map score = new HashMap();
        score.put("courseId", courseId);

        req.add("score", score);
        res = HttpRequestUtil.request("/api/score/ScoreInitializationByCourse", req);
        if (res != null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("初始化成功");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
            courseIdTextField.setText("");
        } else {
            MessageDialog.showDialog("请检查网络连接！");
        }

    }
    }
