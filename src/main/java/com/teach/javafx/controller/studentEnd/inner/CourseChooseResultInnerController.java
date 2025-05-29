package com.teach.javafx.controller.studentEnd.inner;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.controller.studentEnd.GlobalSession;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseChooseResultInnerController extends ToolController {

    @FXML
    private TableView<Map<String, String>> courseTableView;
    @FXML
    private TableColumn<Map<String, String>, String> courseNumColumn;
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
    @FXML
    private TableColumn<Map<String, String>, String> coursePreCourseName;

    private String studentId;
    private ArrayList<Map<String, String>> courseList = new ArrayList<>();
    private ObservableList<Map<String, String>> courseObservableList = FXCollections.observableArrayList();

    public void initialize() {
        //
        this.studentId = GlobalSession.getInstance().getStudentId();

        // 列值工程属性
        courseClassroomColumn.setCellValueFactory(new MapValueFactory("classroom"));
        courseNameColumn.setCellValueFactory(new MapValueFactory("name"));
        courseTimeColumn.setCellValueFactory(new MapValueFactory("time"));
        coursePreCourseName.setCellValueFactory(new MapValueFactory("preCourseName"));
        courseDayOfWeekColumn.setCellValueFactory(new MapValueFactory("dayOfWeek"));
        courseCreditColumn.setCellValueFactory(new MapValueFactory("credit"));
        courseNumColumn.setCellValueFactory(new MapValueFactory("num"));

        // 添加操作列
        TableColumn<Map<String, String>, Void> operationColumn = new TableColumn<>();
        operationColumn.setCellFactory(createButtonCellFactory());
        courseTableView.getColumns().add(operationColumn);

        getData();
        setTableView();

    }

    private void getData() {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("studentId", studentId);
        dataRequest.add("selection", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/course/getStudentCourseListResult", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                courseList = (ArrayList<Map<String, String>>) dataResponse.getData();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接！");
        }
    }

    private void setTableView() {
        courseObservableList.clear();
        for(Map<String, String> stringStringMap : courseList){
            courseObservableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        courseTableView.setItems(courseObservableList);
    }

    private Callback<TableColumn<Map<String, String>, Void>, TableCell<Map<String, String>, Void>> createButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button button = new Button("退课");
            {
                button.setOnAction(event -> {
                    Integer choice = MessageDialog.choiceDialog("确定要退选这门课程吗？");
                    if (choice == 3) {
                        Map<String, String> rowData = getTableView().getItems().get(getIndex());
                        String courseId = CommonMethod.getString(rowData, "courseId");
                        String studentId = GlobalSession.getInstance().getStudentId();
                        DataRequest dataRequest = new DataRequest();
                        Map<String, String> map = new HashMap<>();
                        map.put("courseId", courseId);
                        map.put("studentId", studentId);
                        dataRequest.add("selection", map);
                        DataResponse dataResponse = HttpRequestUtil.request("/api/course/dropCourse", dataRequest);
                        if (dataResponse != null ) {
                            if (dataResponse.getCode() == 0){
                                MessageDialog.showDialog("退课成功!");
                            } else {
                                MessageDialog.showDialog(dataResponse.getMsg());
                            }
                        } else {
                            MessageDialog.showDialog("请检查网络连接！");
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        };
    }
}
