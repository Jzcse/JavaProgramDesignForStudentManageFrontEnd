package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.util.CommonMethod;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
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
    private TableColumn<Map,String> preCourseNameColumn;
    @FXML
    private TableColumn<Map,String> classroomColumn;
    @FXML
    private TableColumn<Map,String> dayOfWeekColumn;
    @FXML
    private TableColumn<Map,String> timeColumn;
    @FXML
    private TableColumn<Map,FlowPane> operateColumn;
    @FXML
    private Pagination pagination;
    @FXML
    private ImageView courseImageView;
    @FXML
    private Button photoButton;


    @FXML
    private TextField courseNumField;
    @FXML
    private TextField courseNameField;
    @FXML
    private TextField creditField;
    @FXML
    private TextField preCourseNameField;
    @FXML
    private TextField classroomField;
    @FXML
    private ComboBox<String> dayOfWeekComboBox;
    @FXML
    private ComboBox<String> timeComboBox;

    private String num;
    private boolean isNew = false;
    private List<Map> courseList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 50;
    private int totalPages = 1;// TableView渲染列表

    @FXML
    private void onQueryButtonClick(){
        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("page", currentPage);  // 添加分页参数
        req.add("size", pageSize); // 添加每页大小
        res = HttpRequestUtil.request("/api/course/getCourseList",req);
        if(res != null && res.getCode()== 0) {
            courseList = (ArrayList<Map>)res.getData();
            //Todo:totalPages = res.getTotalPages();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map map;
        FlowPane flowPane;
        Button newButton, editButton, deleteButton;
        for (int j = 0; j < courseList.size(); j++) {
            map = courseList.get(j);
            flowPane = new FlowPane();
            flowPane.setHgap(10);
            flowPane.setAlignment(Pos.CENTER);

            newButton = new Button("新建");
            newButton.setOnAction(e->{
                newItem();
            });

            editButton = new Button("修改");
            editButton.setId("edit"+j);
            editButton.setOnAction(e->{
                String id = ((Button)e.getSource()).getId();
                editItem(id);  // 确保传递正确的参数
            });

            deleteButton = new Button("删除");
            deleteButton.setId("delete"+j);
            deleteButton.setOnAction(e->{
                String id = ((Button)e.getSource()).getId();
                deleteItem(id);  // 确保传递正确的参数
            });

            flowPane.getChildren().addAll(newButton, editButton, deleteButton);
            map.put("operate",flowPane);
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }

    public void onAddButtonClick(ActionEvent actionEvent) {
        this.isNew = true;
        newItem();
        onClearCourseButtonClick(actionEvent);
    }
    public void newItem() {
        // 新建课程逻辑
        Map<String, Object> newCourse = new HashMap<>();
        newCourse.put("courseId", "");
        newCourse.put("num", "");
        newCourse.put("name", "");
        newCourse.put("credit", "");
        newCourse.put("preCourseName", "");
        newCourse.put("classroom", "");
        newCourse.put("dayOfWeek", "");
        newCourse.put("startTime", "");
        newCourse.put("endTime", "");
        courseList.add(newCourse);
        setTableViewData();
    }

    @FXML
    public void initialize() {
        numColumn.setCellValueFactory(new MapValueFactory("courseId"));
        numColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        numColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("courseId", event.getNewValue());
        });
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
        preCourseNameColumn.setCellValueFactory(new MapValueFactory("preCourseName"));
        preCourseNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        preCourseNameColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("preCourseName", event.getNewValue());
        });
        classroomColumn.setCellValueFactory(new MapValueFactory("classroom"));
        classroomColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        classroomColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("classroom", event.getNewValue());
        });

        dayOfWeekColumn.setCellValueFactory(new MapValueFactory("dayOfWeek"));
        dayOfWeekColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dayOfWeekColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("dayOfWeek", event.getNewValue());
        });

        timeColumn.setCellValueFactory(new MapValueFactory("time"));
        timeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        timeColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("time", event.getNewValue());
            onQueryButtonClick();
        });


        // 添加表格选择监听器
        dataTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    updateCourseDetails(newSelection);
                }
            });

        dataTableView.setEditable(true);
        onQueryButtonClick();
    }

    // 新增方法：更新右侧课程信息
    private void updateCourseDetails(Map<String, String> course) {
        courseNumField.setText(course.get("num"));
        courseNameField.setText(course.get("name"));
        creditField.setText(course.get("credit"));
        preCourseNameField.setText(course.get("preCourseName"));
        classroomField.setText(course.get("classroom"));
        dayOfWeekComboBox.setValue(course.get("dayOfWeek"));
        timeComboBox.setValue(course.get("time"));
    }

    public void editItem(String id) {  // 确保方法签名正确
        if(id == null)
            return;
        int j = Integer.parseInt(id.substring(4));  // 简化字符串处理
        Map data = courseList.get(j);
        System.out.println("Editing item: " + data);
        // 这里可以添加实际的编辑逻辑
    }

    public void deleteItem(String id) {  // 确保方法签名正确
        if(id == null)
            return;
        int j = Integer.parseInt(id.substring(6));  // 简化字符串处理
        Map data = courseList.get(j);
        System.out.println("Deleting item: " + data);
        // 这里可以添加实际的删除逻辑
    }
    private boolean isValidTimeFormat(String time) {
        return time.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]");
    }

    @FXML
    protected void onSaveCourseButtonClick(ActionEvent event) {
        // 获取输入框的值
        String courseNum = courseNumField.getText();
        String courseName = courseNameField.getText();
        String credit = creditField.getText();
        String preCourseName = preCourseNameField.getText();
        String classroom = classroomField.getText();
        String dayOfWeek = dayOfWeekComboBox.getValue();
        String time = timeComboBox.getValue();

        // 数据验证
        if (courseNum.isEmpty()) {
            MessageDialog.showDialog("课程编号不能为空");
            return;
        }
        if (courseName.isEmpty()) {
            MessageDialog.showDialog("课程名称不能为空");
            return;
        }

        // 创建课程信息Map
        Map<String, Object> courseForm = new HashMap<>();
        courseForm.put("num", courseNum);
        courseForm.put("name", courseName);
        courseForm.put("credit", credit);
        courseForm.put("preCourseName", preCourseName);
        courseForm.put("classroom", classroom);
        courseForm.put("dayOfWeek", dayOfWeek);
        courseForm.put("time", time);

        // 如果是修改操作，需要获取课程ID
        if (!isNew) {
            DataRequest courseIdRequest = new DataRequest();
            Map<String, Object> courseIdMap = new HashMap<>();
            courseIdMap.put("num", courseNum);
            courseIdMap.put("name", courseName);
            courseIdRequest.add("course", courseIdMap);
            DataResponse courseIdResponse = HttpRequestUtil.request("/api/course/getCourseId", courseIdRequest);

            if (courseIdResponse == null || courseIdResponse.getCode() != 0) {
                MessageDialog.showDialog("获取课程ID失败，请检查网络连接或服务器状态");
                return;
            }

            Map<String, Object> responseCourseId = (Map<String, Object>) courseIdResponse.getData();
            String courseId = responseCourseId != null ? responseCourseId.get("courseId").toString() : null;
            if (courseId == null) {
                MessageDialog.showDialog("无法获取课程ID");
                return;
            }
            courseForm.put("courseId", courseId);
        }

        // 创建请求对象
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("course", courseForm);

        // 发送请求
        DataResponse dataResponse;
        if (isNew) {
            dataResponse = HttpRequestUtil.request("/api/course/courseAdd", dataRequest);
        } else {
            dataResponse = HttpRequestUtil.request("/api/course/courseUpdate", dataRequest);
        }

        // 处理响应
        if (dataResponse != null && dataResponse.getCode() == 0) {
            MessageDialog.showDialog(isNew ? "课程添加成功" : "课程修改成功");
            // 刷新表格数据
            onQueryButtonClick();
            // 重置状态
            isNew = false;
        } else {
            MessageDialog.showDialog("操作失败：" + (dataResponse != null ? dataResponse.getMsg() : "请求失败"));
        }
    }
    @FXML
    public void onDeleteButtonClick(ActionEvent actionEvent) {
        Map<String, String> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("请选定要删除的课程");
        } else {
            int choice = MessageDialog.choiceDialog("确定要删除吗？");
            if (choice == MessageDialog.CHOICE_YES){
                // 创建一个新的Map，只包含需要的数据
                Map<String, String> requestData = new HashMap<>();
                requestData.put("courseId", form.get("courseId"));
                // 添加其他必要字段...
                DataRequest dataRequest = new DataRequest();
                dataRequest.add("course", requestData);  // 使用新的Map
                DataResponse dataResponse = HttpRequestUtil.request("/api/course/courseDelete", dataRequest);
                if (dataResponse != null){
                    if (dataResponse.getCode() == 0) {
                        MessageDialog.showDialog("删除成功！");
                        //刷新表格
                        DataResponse res;
                        DataRequest req = new DataRequest();
                        req.add("page", 0);  // 添加分页参数
                        req.add("size", 50); // 添加每页大小
                        res = HttpRequestUtil.request("/api/course/getCourseList", req);
                        if (res != null && res.getCode() == 0) {
                            courseList = (ArrayList<Map>) res.getData();
                        }
                        setTableViewData();
                    }
                }
            }
        }
    }
    @FXML
    protected void onClearCourseButtonClick(ActionEvent event) {
        // 清空输入框的逻辑
        courseNumField.clear();
        courseNameField.clear();
        creditField.clear();
        preCourseNameField.clear();
        classroomField.clear();
        dayOfWeekComboBox.getSelectionModel().clearSelection();
        timeComboBox.getSelectionModel().clearSelection();


        System.out.println("已清空输入框");
    }

    @FXML
    private TextField searchField;  // 搜索框

    @FXML
    private void onSearchButtonClick() {
        String keyword = searchField.getText().trim();
        DataRequest req = new DataRequest();
        Map<String, Object> courseForm = new HashMap<>();
        courseForm.put("numName", keyword);
        req.add("course", courseForm);
        req.add("page", 0);
        req.add("size", 50);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);
        if(res != null && res.getCode() == 0) {
            courseList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }
    @FXML
    private void onCourseArrangementButtonClick() {
        try {
            Stage childStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/teach/javafx/courseChild-panel.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            childStage.setTitle("课程安排");
            childStage.setScene(scene);
            childStage.initOwner(dataTableView.getScene().getWindow());  // 设置父窗口
            childStage.initModality(Modality.WINDOW_MODAL);  // 设置为模态窗口
            childStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("无法打开课程安排窗口：" + e.getMessage());
        }
    }


}


