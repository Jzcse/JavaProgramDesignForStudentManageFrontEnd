package com.teach.javafx.controller.studentEnd;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.controller.studentEnd.inner.CourseChooseDetailController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.invoke.CallSite;
import java.util.*;

import javafx.beans.property.StringProperty;

public class CourseChooseController extends ToolController {

    // 当前课程表的元素
    @FXML
    private TableView<CourseTableModel> curriculumView;
    @FXML
    private TableColumn<CourseTableModel,String> timeColumn;
    @FXML
    private TableColumn<CourseTableModel,String> monday;
    @FXML
    private TableColumn<CourseTableModel,String> tuesday;
    @FXML
    private TableColumn<CourseTableModel,String> wednesday;
    @FXML
    private TableColumn<CourseTableModel,String> thursday;
    @FXML
    private TableColumn<CourseTableModel,String> friday;
    @FXML
    private TableColumn<CourseTableModel,String> saturday;
    @FXML
    private TableColumn<CourseTableModel,String> sunday;

    // 右侧VBox的元素
    @FXML
    private Label credit;
    @FXML
    private Button courseChooseResultButton;

    // 全部课程的表格元素
    @FXML
    private TableView<Map<String, String>> courseTableView;
    @FXML
    private TableColumn<Map<String, String>,String> courseNumColumn;
    @FXML
    private TableColumn<Map<String, String>, String> courseNameColumn;
    @FXML
    private TableColumn<Map<String, String>, String> courseCreditColumn;
    @FXML
    private TableColumn<Map<String, String>, String> courseClassroomColumn;
    @FXML
    private TableColumn<Map<String, String>, String> courseDayOfWeekColumn;
    @FXML
    private TableColumn<Map<String, String>, String> courseTimeColumn;

    // 全局变量
    // 课程表相关
    private ArrayList<Map<String, String>> courseListForCurriculum = new ArrayList<>();
    private ObservableList<CourseTableModel> CurriculumObservableList = FXCollections.observableArrayList();

    private String weekDays = "星期一,星期二,星期三,星期四,星期五,星期六,星期日";
    private ObservableList<CourseTableModel> courseData = FXCollections.observableArrayList();

    private static final String[] TIME_SLOTS = {
            "第一节：08:00-09:50",
            "第二节：10:10-12:00",
            "第三节：14:00-15:50",
            "第四节：16:10-18:00",
            "第五节：19:00-20:50"
    };

    private ObservableList<Map<String, String>> courseObservableList = FXCollections.observableArrayList();
    private ArrayList<Map<String, String>> courseList = new ArrayList<>();
    private String studentId, courseId;

    public void initialize(){
        studentId = GlobalSession.getInstance().getStudentId();

        courseNumColumn.setCellValueFactory(new MapValueFactory("num"));
        courseNameColumn.setCellValueFactory(new MapValueFactory("name"));
        courseCreditColumn.setCellValueFactory(new MapValueFactory("credit"));
        courseClassroomColumn.setCellValueFactory(new MapValueFactory("classroom"));
        courseDayOfWeekColumn.setCellValueFactory(new MapValueFactory("dayOfWeek"));
        courseTimeColumn.setCellValueFactory(new MapValueFactory("time"));

        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        monday.setCellValueFactory(cellData -> cellData.getValue().mondayProperty());
        tuesday.setCellValueFactory(cellData -> cellData.getValue().tuesdayProperty());
        wednesday.setCellValueFactory(cellData -> cellData.getValue().wednesdayProperty());
        thursday.setCellValueFactory(cellData -> cellData.getValue().thursdayProperty());
        friday.setCellValueFactory(cellData -> cellData.getValue().fridayProperty());
        saturday.setCellValueFactory(cellData -> cellData.getValue().saturdayProperty());
        sunday.setCellValueFactory(cellData -> cellData.getValue().sundayProperty());

        TableView.TableViewSelectionModel<Map<String, String>> tableViewSelectionModel = courseTableView.getSelectionModel();
        ObservableList<Integer> list = tableViewSelectionModel.getSelectedIndices();
        list.addListener(this::onRowSelect);

        getData();
        setTableView();
        freshCredit();
        loadCourseData();

    }

    private void getData() {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("numName", "");
        dataRequest.add("page", 0);
        dataRequest.add("size", 50);
        DataResponse dataResponse = HttpRequestUtil.request("/api/course/getCourseList", dataRequest);
        if (dataResponse != null) {
            if(dataResponse.getCode() == 0) {
                courseList = (ArrayList<Map<String, String>>) dataResponse.getData();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("加载失败，请检查网络!");
        }
    }

    private void freshCredit() {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("studentId", studentId);
        dataRequest.add("selection", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/course/creditsCount", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                Map<String, String> result = (Map<String, String>) dataResponse.getData();
                credit.setText(CommonMethod.getString(result, "totalCredits"));
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请假查网络！");
        }
    }

    private void setTableView(){
        courseObservableList.clear();
        for(Map<String, String> stringStringMap : courseList){
            courseObservableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        courseTableView.setItems(courseObservableList);
    }

    private void onRowSelect(ListChangeListener.Change<? extends Integer> change) {
        courseId = courseTableView.getSelectionModel().getSelectedItem().get("courseId");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/student-end/inner/course-choose-detail-panel.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.setTitle("当前课程");
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            //
            CourseChooseDetailController detailController = loader.getController();
            detailController.initialize(courseId);
            stage.showAndWait();
            freshCredit();
            loadCourseData();
            // TODO 刷新状态
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    // 打开选课结果子窗口
    public void onCourseChooseResultButtonClick(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/student-end/inner/course-choose-result-inner-panel.fxml"));
            Parent newRoot = loader.load();
            Stage stage = new Stage();
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.setTitle("选课结果");
            Scene scene = new Scene(newRoot, 1000,600);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            freshCredit();
            loadCourseData();
            // TODO 刷新当前窗口的信息
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    private void loadCourseData() {
        // 假设这是前端代码
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("studentId", studentId);
        dataRequest.add("selection", map);
        DataResponse dataResponse= HttpRequestUtil.request("/api/course/getStudentCourseList", dataRequest);
        Map<String, List<Map<String, String>>> courseMap;
        if(dataResponse != null) {
            if (dataResponse.getCode() == 0){
                 courseMap = (Map<String, List<Map<String, String>>>) dataResponse.getData();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
                return;
            }
        } else {
            MessageDialog.showDialog("请检查网络连接!");
            return;
        }

        courseData.clear();
        for (String timeSlot : TIME_SLOTS) {
            String[] courses = new String[7];
            Arrays.fill(courses, "");

            // 遍历每一天的课程
            for (Map.Entry<String, List<Map<String, String>>> entry : courseMap.entrySet()) {
                String day = entry.getKey();
                List<Map<String, String>> dayCourses = entry.getValue();
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
            courseData.add(new CourseTableModel(
                    timeSlot,
                    courses[0], courses[1], courses[2],
                    courses[3], courses[4], courses[5], courses[6]
            ));
        }
        curriculumView.setItems(courseData);
    }

}

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