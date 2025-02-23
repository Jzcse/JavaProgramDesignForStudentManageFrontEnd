package com.teach.javafx.controller;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.cell.MapValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CourseController 登录交互控制类 对应 course-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class CourseController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> preCourseColumn;
    @FXML
    private TableColumn<Map,FlowPane> operateColumn;

    private List<Map> courseList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private void onQueryButtonClick(){
        DataResponse res;
        DataRequest req =new DataRequest();
        res = HttpRequestUtil.request("/api/course/getCourseList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            courseList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
       observableList.clear();
       Map map;
        FlowPane flowPane;
        Button saveButton,deleteButton;
            for (int j = 0; j < courseList.size(); j++) {
                map = courseList.get(j);
                flowPane = new FlowPane();
                flowPane.setHgap(10);
                flowPane.setAlignment(Pos.CENTER);
                saveButton = new Button("修改保存");
                saveButton.setId("save"+j);
                saveButton.setOnAction(e->{
                    saveItem(((Button)e.getSource()).getId());
                });
                deleteButton = new Button("删除");
                deleteButton.setId("delete"+j);
                deleteButton.setOnAction(e->{
                    deleteItem(((Button)e.getSource()).getId());
                });
                flowPane.getChildren().addAll(saveButton,deleteButton);
                map.put("operate",flowPane);
                observableList.addAll(FXCollections.observableArrayList(map));
            }
            dataTableView.setItems(observableList);
    }
    public void saveItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length()));
        Map data = courseList.get(j);
        System.out.println(data);
    }
    public void deleteItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(5,name.length()));
        Map data = courseList.get(j);
        System.out.println(data);
    }

    @FXML
    public void initialize() {
        numColumn.setCellValueFactory(new MapValueFactory("num"));
        numColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        numColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("num", event.getNewValue());
        });
        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("name", event.getNewValue());
        });
        creditColumn.setCellValueFactory(new MapValueFactory("credit"));
        creditColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        creditColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("credit", event.getNewValue());
        });
        preCourseColumn.setCellValueFactory(new MapValueFactory("preCourse"));
        preCourseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        preCourseColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("preCourse", event.getNewValue());
        });
        operateColumn.setCellValueFactory(new MapValueFactory("operate"));
        dataTableView.setEditable(true);
        onQueryButtonClick();
    }


}
