package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseCentreController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> textbookColumn;
    @FXML
    private TableColumn<Map,String> coursewareColumn;
    @FXML
    private TableColumn<Map,String> referenceColumn;

    @FXML
    private TextField courseCentreNameField;
    @FXML
    private TextField courseCentreTextbookField;
    @FXML
    private TextField courseCentreCoursewareField;
    @FXML
    private TextField courseCentreReferenceField;

    private boolean isNew = false;
    private List<Map> courseCentreList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();
    private int currentPage = 0;
    private int pageSize = 50;

    @FXML
    public void initialize() {

        nameColumn.setCellValueFactory(new MapValueFactory("courseCentreName"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("name", event.getNewValue());
        });
        textbookColumn.setCellValueFactory(new MapValueFactory("courseCentreTextbook"));
        textbookColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        textbookColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("credit", event.getNewValue());
        });
        coursewareColumn.setCellValueFactory(new MapValueFactory("courseCentreCourseware"));
        coursewareColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        coursewareColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("preCourseName", event.getNewValue());
        });
        referenceColumn.setCellValueFactory(new MapValueFactory("courseCentreReference"));
        referenceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        referenceColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("classroom", event.getNewValue());
            onQueryButtonClick();
        });



        // 添加表格选择监听器
        dataTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        updateCourseCentreDetails(newSelection);
                    }
                });

        dataTableView.setEditable(true);
        onQueryButtonClick();
    }

    @FXML
    private void onQueryButtonClick(){
        DataResponse res;
        DataRequest req =new DataRequest();
        Map<String, Object> courseCentre = new HashMap<>();
        courseCentre.put("page", currentPage);
        courseCentre.put("pageSize", pageSize);
        req.add("courseCentre", courseCentre);
        res = HttpRequestUtil.request("/api/courseCentre/getCourseCentreList",req);
        if(res != null && res.getCode()== 0) {
            courseCentreList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map map;
        FlowPane flowPane;
        Button newButton, editButton, deleteButton;
        for (int j = 0; j < courseCentreList.size(); j++) {
            map = courseCentreList.get(j);
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
    private void updateCourseCentreDetails(Map<String, String> courseCentre) {
        courseCentreNameField.setText(courseCentre.get("courseCentreName"));
        courseCentreTextbookField.setText(courseCentre.get("courseCentreTextbook"));
        courseCentreCoursewareField.setText(courseCentre.get("courseCentreCourseware"));
        courseCentreReferenceField.setText(courseCentre.get("courseCentreReference"));
    }
    public void newItem() {
        // 新建课程逻辑
        Map<String, Object> newCourseCentre = new HashMap<>();
        newCourseCentre.put("courseCentreName", "");
        newCourseCentre.put("courseCentreTextbook", "");
        newCourseCentre.put("courseCentreCourseware", "");
        newCourseCentre.put("courseCentreReference", "");
        courseCentreList.add(newCourseCentre);
        setTableViewData();
    }
    public void editItem(String id) {  // 确保方法签名正确
        if(id == null)
            return;
        int j = Integer.parseInt(id.substring(4));  // 简化字符串处理
        Map data = courseCentreList.get(j);
        System.out.println("Editing item: " + data);
        // 这里可以添加实际的编辑逻辑
    }

    public void deleteItem(String id) {  // 确保方法签名正确
        if(id == null)
            return;
        int j = Integer.parseInt(id.substring(6));  // 简化字符串处理
        Map data = courseCentreList.get(j);
        System.out.println("Deleting item: " + data);
        // 这里可以添加实际的删除逻辑
    }
    public void onAddButtonClick(ActionEvent actionEvent) {
        this.isNew = true;
        newItem();
        onClearCourseButtonClick(actionEvent);
    }
    @FXML
    protected void onClearCourseButtonClick(ActionEvent event) {
        // 清空输入框的逻辑
        courseCentreNameField.clear();
        courseCentreTextbookField.clear();
        courseCentreList.clear();
        courseCentreCoursewareField.clear();
        courseCentreReferenceField.clear();
        System.out.println("已清空输入框");
    }
    @FXML
    protected void onSaveCourseButtonClick(ActionEvent event) {
        // 获取输入框的值
        String courseName = courseCentreNameField.getText();
        String textbook = courseCentreTextbookField.getText();
        String courseware = courseCentreCoursewareField.getText();
        String reference = courseCentreReferenceField.getText();
        if (courseName.isEmpty()) {
            MessageDialog.showDialog("课程名称不能为空");
            return;
        }

        // 创建课程信息Map
        Map<String, Object> courseCentreForm = new HashMap<>();
        courseCentreForm.put("courseCentreName", courseName);
        courseCentreForm.put("courseCentreTextbook", textbook);
        courseCentreForm.put("courseCentreCourseware", courseware);
        courseCentreForm.put("courseCentreReference", reference);

        // 如果是修改操作，需要获取课程ID
        if (!isNew) {
            DataRequest courseIdRequest = new DataRequest();
            Map<String, Object> courseCentre = new HashMap<>();
            courseCentre.put("page", currentPage);
            courseCentre.put("pageSize", pageSize);
            courseCentre.put("courseCentreName", courseName);
            courseIdRequest.add("courseCentre", courseCentre);
            DataResponse courseIdResponse = HttpRequestUtil.request("/api/courseCentre/getCourseCentreList", courseIdRequest);

            if (courseIdResponse == null || courseIdResponse.getCode() != 0) {
                MessageDialog.showDialog("获取课程ID失败，请检查网络连接或服务器状态");
                return;
            }

            List<Map<String, Object>> responseCourseCentreIdList = (List<Map<String, Object>>) courseIdResponse.getData();
            Map<String, Object> responseCourseCentreId = responseCourseCentreIdList != null && responseCourseCentreIdList.size() > 0 ? responseCourseCentreIdList.get(0) : null;
            String courseCentreId = responseCourseCentreId != null ? responseCourseCentreId.get("courseCentreId").toString() : null;
            if (courseCentreId == null) {
                MessageDialog.showDialog("无法获取课程ID");
                return;
            }
            courseCentreForm.put("courseCentreId", courseCentreId);
        }

        // 创建请求对象
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("courseCentre", courseCentreForm);

        // 发送请求
        DataResponse dataResponse;
        if (isNew) {
            dataResponse = HttpRequestUtil.request("/api/courseCentre/addCourseCentre", dataRequest);
        } else {
            dataResponse = HttpRequestUtil.request("/api/courseCentre/updateCourseCentre", dataRequest);
        }

        // 处理响应
        if (dataResponse != null && dataResponse.getCode() == 0) {
            MessageDialog.showDialog(isNew ? "课程信息添加成功" : "课程信息修改成功");
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
                requestData.put("courseCentreId", form.get("courseCentreId"));
                // 添加其他必要字段...
                DataRequest dataRequest = new DataRequest();
                dataRequest.add("courseCentre", requestData);  // 使用新的Map
                DataResponse dataResponse = HttpRequestUtil.request("/api/courseCentre/deleteCourseCentre", dataRequest);
                if (dataResponse != null){
                    if (dataResponse.getCode() == 0) {
                        MessageDialog.showDialog("删除成功！");
                        //刷新表格
                        onQueryButtonClick();
                    }
                }
            }
        }
    }

    public void onSearchButtonClick(ActionEvent actionEvent) {
        String keyword = searchField.getText().trim();
        DataRequest req = new DataRequest();
        Map<String, Object> courseCentreForm = new HashMap<>();
        courseCentreForm.put("courseCentreName", keyword);
        courseCentreForm.put("page", currentPage);
        courseCentreForm.put("pageSize", pageSize);
        req.add("courseCentre", courseCentreForm);
        DataResponse res = HttpRequestUtil.request("/api/courseCentre/getCourseCentreList", req);
        if(res != null && res.getCode() == 0) {
            courseCentreList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }
}
