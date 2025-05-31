package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> typeColumn;
    @FXML
    private TableColumn<Map, String> timeColumn;
    @FXML
    private TableColumn<Map, String> locationColumn;
    @FXML
    private TableColumn<Map, FlowPane> operateColumn;
    @FXML
    private Pagination pagination;

    @FXML
    private TextField nameField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField timeField;
    @FXML
    private TextField locationField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField searchField;

    private Integer currentPage = 0;
    private final Integer pageSize = 10;
    private Integer totalPages = 1;
    private List<Map> activityList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private boolean isNew = false;

    @FXML
    public void initialize() {
        // 初始化表格列
        nameColumn.setCellValueFactory(new MapValueFactory<>("activityName"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("activityType"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("activityTime"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));

        // 初始化操作列
        operateColumn.setCellFactory(createActionCellFactory());

        // 初始化分页组件
        pagination.setPageCount(totalPages);
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = newVal.intValue();
            onQueryButtonClick();
        });

        // 加载初始数据
        onQueryButtonClick();
        onSearchButtonClick();
    }

    // 创建操作列按钮
    private Callback<TableColumn<Map, FlowPane>, TableCell<Map, FlowPane>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editBtn = new Button("编辑");
            private final Button deleteBtn = new Button("删除");
            private final FlowPane flowPane = new FlowPane(editBtn, deleteBtn);

            {
                flowPane.setHgap(10);
                flowPane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(event -> {
                    Map data = getTableView().getItems().get(getIndex());
                    onEditButtonClick(data);
                });

                deleteBtn.setOnAction(event -> {
                    Map data = getTableView().getItems().get(getIndex());
                    onDeleteButtonClick(data);
                });
            }

            @Override
            protected void updateItem(FlowPane item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(flowPane);
                }
            }
        };
    }

    // 查询按钮点击事件
    @FXML
    private void onQueryButtonClick() {
        DataRequest req = new DataRequest();
        req.add("page", currentPage);
        req.add("size", pageSize);

        DataResponse res = HttpRequestUtil.request("/api/activity/page", req);
        if (res != null && res.getCode() == 0) {
            Map<String, Object> data = (Map<String, Object>) res.getData();
            activityList = (List<Map>) data.get("data");
            totalPages = (Integer) data.get("totalPages");

            observableList.clear();
            observableList.addAll(FXCollections.observableArrayList(activityList));
            dataTableView.setItems(observableList);

            pagination.setPageCount(totalPages);
        }
    }

    // 搜索按钮点击事件
    @FXML
    private void onSearchButtonClick() {
        String keyword = searchField.getText().trim();
        DataRequest req = new DataRequest();
        req.add("name", keyword);
        req.add("page", 0);
        req.add("size", pageSize);

        DataResponse res = HttpRequestUtil.request("/api/activity/search", req);
        if (res != null && res.getCode() == 0) {
            Map<String, Object> data = (Map<String, Object>) res.getData();
            activityList = (List<Map>) data.get("list");

            observableList.clear();
            observableList.addAll(FXCollections.observableArrayList(activityList));
            dataTableView.setItems(observableList);

            currentPage = 0;
            pagination.setCurrentPageIndex(0);
        }
        clearForm();
    }

    // 新增按钮点击事件
    @FXML
    private void onAddButtonClick() {
        isNew = true;
        clearForm();
    }

    // 编辑按钮点击事件
    private void onEditButtonClick(Map data) {
        isNew = false;
        nameField.setText(data.get("activityName").toString());
        typeField.setText(data.get("activityType").toString());
        timeField.setText(data.get("activityTime").toString());
        locationField.setText(data.get("location").toString());
        descriptionArea.setText(data.get("description").toString());
    }

    // 保存按钮点击事件
    @FXML
    private void onSaveButtonClick() {
        if (nameField.getText().isEmpty()) {
            MessageDialog.showDialog("活动名称不能为空");
            return;
        }

        DataRequest req = new DataRequest();
        Map<String, Object> form = new HashMap<>();
        form.put("activityName", nameField.getText());
        form.put("activityType", typeField.getText());
        form.put("activityTime", timeField.getText());
        form.put("location", locationField.getText());
        form.put("description", descriptionArea.getText());

        req.add("activityMap", form);

        String url = "/api/activity/add";
        DataResponse res = HttpRequestUtil.request(url, req);

        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("操作成功");
            onQueryButtonClick();
            clearForm();
        } else {
            MessageDialog.showDialog("操作失败: " + (res != null ? res.getMsg() : "请求失败"));
        }
        onSearchButtonClick();
    }

    // 删除按钮点击事件
    private void onDeleteButtonClick(Map data) {
        int choice = MessageDialog.choiceDialog("确认要删除这条记录吗？");
        if (choice != MessageDialog.CHOICE_YES) {
            return;
        }

        DataRequest req = new DataRequest();
        Map<String, String> activityMap = new HashMap<>();
        activityMap.put("activityId", data.get("activityId").toString());
        req.add("activityMap", activityMap);

        DataResponse res = HttpRequestUtil.request("/api/activity/delete", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog("删除失败: " + (res != null ? res.getMsg() : "请求失败"));
        }
        onSearchButtonClick();
    }

    // 清空表单
    private void clearForm() {
        nameField.clear();
        typeField.clear();
        timeField.clear();
        locationField.clear();
        descriptionArea.clear();
        searchField.clear();
    }
}