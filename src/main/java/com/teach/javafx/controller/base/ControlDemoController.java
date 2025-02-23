package com.teach.javafx.controller.base;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.teach.javafx.models.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * ControlDemoController 登录交互控制类 对应 base/control-demo-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class ControlDemoController {
    @FXML
    private  Button button;
    @FXML
    private  RadioButton maleRadio;
    @FXML
    private  RadioButton femaleRadio;
    @FXML
    private ChoiceBox<Student> choiceBox;
    @FXML
    private ComboBox<Student> comboBox;
    @FXML
    private ListView<Student> listView;

    private List<Student> studentList;

    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    @FXML
    public void initialize() {
        studentList = new ArrayList();
        studentList.add(new Student("001","name1"));
        studentList.add(new Student("002","name2"));
        studentList.add(new Student("003","name3"));
        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(maleRadio, femaleRadio);
        group.selectedToggleProperty().addListener(this::radioChanged);
        initStudentList();
        listView.getItems().addAll(studentList);
        choiceBox.getItems().addAll(studentList);
        comboBox.getItems().addAll(studentList);
    }
    private void initStudentList(){
    }
    public void MapTopControl(){

    }
    public void controlToMap(){

    }
    public void radioChanged(ObservableValue<? extends Toggle> observable,
                        Toggle oldBtn,
                        Toggle newBtn) {
        System.out.println(newBtn);
    }
    @FXML
    public void onButtonClick(){
        String gender = "";
        if(maleRadio.isSelected())
            gender = "1";
        else
            gender = "2";
        System.out.println(gender);
    }
}