package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

public class CourseChildController {
    @FXML
    private HBox weekDaysHBox;

    @FXML
    private VBox timeVBox;

    @FXML
    private TableView<CourseTableModel> courseTableView;

    @FXML
    private TableColumn<CourseTableModel, String> timeColumn;

    @FXML
    private TableColumn<CourseTableModel, String> mondayColumn;
    @FXML
    private TableColumn<CourseTableModel, String> tuesdayColumn;
    @FXML
    private TableColumn<CourseTableModel, String> wednesdayColumn;
    @FXML
    private TableColumn<CourseTableModel, String> thursdayColumn;
    @FXML
    private TableColumn<CourseTableModel, String> fridayColumn;
    @FXML
    private TableColumn<CourseTableModel, String> saturdayColumn;
    @FXML
    private TableColumn<CourseTableModel, String> sundayColumn;
    private String weekDays = "星期一,星期二,星期三,星期四,星期五,星期六,星期日";

    private ObservableList<CourseTableModel> courseData = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        // 初始化表格列
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        mondayColumn.setCellValueFactory(cellData -> cellData.getValue().mondayProperty());
        tuesdayColumn.setCellValueFactory(cellData -> cellData.getValue().tuesdayProperty());
        wednesdayColumn.setCellValueFactory(cellData -> cellData.getValue().wednesdayProperty());
        thursdayColumn.setCellValueFactory(cellData -> cellData.getValue().thursdayProperty());
        fridayColumn.setCellValueFactory(cellData -> cellData.getValue().fridayProperty());
        saturdayColumn.setCellValueFactory(cellData -> cellData.getValue().saturdayProperty());
        sundayColumn.setCellValueFactory(cellData -> cellData.getValue().sundayProperty());

        // 加载课程数据
        loadCourseData();
    }

    // 添加时间段的定义
    private static final String[] TIME_SLOTS = {
        "第一节：08:00-09:50",
        "第二节：10:10-12:00",
        "第三节：14:00-15:50",
        "第四节：16:10-18:00",
        "第五节：19:00-20:50"
    };

    private void loadCourseData() {
        // 假设这是前端代码
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("weekDays", "星期一,星期二,星期三,星期四,星期五,星期六,星期日");
        res = HttpRequestUtil.request("/api/course/getWeekDaysList", req);

        // 修改为处理后端返回的Map<String, List<Map<String, String>>>结构
        Object data = res.getData();
        Map<String, List<Map<String, String>>> courseMap = (Map<String, List<Map<String, String>>>) data;

        // 清空旧数据
        courseData.clear();

        // 按照时间段顺序创建课程表
        for (String timeSlot : TIME_SLOTS) {
            // 初始化每天的课程名称
            String[] courses = new String[7];
            for (int i = 0; i < courses.length; i++) {
                courses[i] = "";
            }

            // 遍历每一天的课程
            for (Map.Entry<String, List<Map<String, String>>> entry : courseMap.entrySet()) {
                String day = entry.getKey();
                List<Map<String, String>> dayCourses = entry.getValue();

                // 查找当前时间段的课程
                for (Map<String, String> courseInfo : dayCourses) {
                    if (courseInfo != null && timeSlot.equals(courseInfo.get("time"))) {
                        String courseName = courseInfo.get("name");
                        switch (day) {
                            case "星期一": courses[0] = courses[0].isEmpty() ? courseName : courses[0] + "\n" + courseName; break;
                            case "星期二": courses[1] = courses[1].isEmpty() ? courseName : courses[1] + "\n" + courseName; break;
                            case "星期三": courses[2] = courses[2].isEmpty() ? courseName : courses[2] + "\n" + courseName; break;
                            case "星期四": courses[3] = courses[3].isEmpty() ? courseName : courses[3] + "\n" + courseName; break;
                            case "星期五": courses[4] = courses[4].isEmpty() ? courseName : courses[4] + "\n" + courseName; break;
                            case "星期六": courses[5] = courses[5].isEmpty() ? courseName : courses[5] + "\n" + courseName; break;
                            case "星期日": courses[6] = courses[6].isEmpty() ? courseName : courses[6] + "\n" + courseName; break;
                        }
                    }
                }
            }

            // 添加课程行
            courseData.add(new CourseTableModel(
                timeSlot,
                courses[0], courses[1], courses[2],
                courses[3], courses[4], courses[5], courses[6]
            ));
        }

        // 绑定数据到 TableView
        courseTableView.setItems(courseData);
    }

}


// 新增数据模型类
class CourseTableModel {
    private final StringProperty time;
    private final StringProperty monday;
    private final StringProperty tuesday;
    private final StringProperty wednesday;
    private final StringProperty thursday;
    private final StringProperty friday;
    private final StringProperty saturday;
    private final StringProperty sunday;

    public CourseTableModel(String time, String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
        this.time = new SimpleStringProperty(time);
        this.monday = new SimpleStringProperty(monday);
        this.tuesday = new SimpleStringProperty(tuesday);
        this.wednesday = new SimpleStringProperty(wednesday);
        this.thursday = new SimpleStringProperty(thursday);
        this.friday = new SimpleStringProperty(friday);
        this.saturday = new SimpleStringProperty(saturday);
        this.sunday = new SimpleStringProperty(sunday);
    }

    public StringProperty timeProperty() {
        return time;
    }

    public StringProperty mondayProperty() {
        return monday;
    }

    public StringProperty tuesdayProperty() {
        return tuesday;
    }

    public StringProperty wednesdayProperty() {
        return wednesday;
    }

    public StringProperty thursdayProperty() {
        return thursday;
    }

    public StringProperty fridayProperty() {
        return friday;
    }

    public StringProperty saturdayProperty() {
        return saturday;
    }

    public StringProperty sundayProperty() {
        return sunday;
    }

}
