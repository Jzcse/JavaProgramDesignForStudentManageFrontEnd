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

public class AchievementController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> descriptionColumn;
    @FXML
    private TableColumn<Map, String> timeColumn;
    @FXML
    private TableColumn<Map, String> personColumn;
    @FXML
    private TableColumn<Map, FlowPane> operateColumn;
    @FXML
    private Pagination pagination;

    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField timeField;
    @FXML
    private TextField personField;
    @FXML
    private TextField searchField;

    private Integer currentPage = 0;
    private final Integer pageSize = 10;
    private Integer totalPages = 1;
    private List<Map> achievementList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private boolean isNew = false;

    @FXML
    public void initialize() {
        // 初始化表格列
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new MapValueFactory<>("description"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        personColumn.setCellValueFactory(new MapValueFactory<>("personName"));

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

        DataResponse res = HttpRequestUtil.request("/api/achievement/getAchievementsByPage", req);
        if (res != null && res.getCode() == 0) {
            Map<String, Object> data = (Map<String, Object>) res.getData();
            achievementList = (List<Map>) data.get("data");

            observableList.clear();
            observableList.addAll(FXCollections.observableArrayList(achievementList));
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

        DataResponse res = HttpRequestUtil.request("/api/achievement/search", req);
        if (res != null && res.getCode() == 0) {
            Map<String, Object> data = (Map<String, Object>) res.getData();
            achievementList = (List<Map>) data.get("list");

            observableList.clear();
            observableList.addAll(FXCollections.observableArrayList(achievementList));
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
        nameField.setText(data.get("name").toString());
        descriptionField.setText(data.get("description").toString());
        timeField.setText(data.get("time").toString());
        personField.setText(data.get("personName").toString());

    }

    // 保存按钮点击事件
    @FXML
    private void onSaveButtonClick() {
        if (nameField.getText().isEmpty()) {
            MessageDialog.showDialog("成就名称不能为空");
            return;
        }

        DataRequest req = new DataRequest();
        Map<String, Object> form = new HashMap<>();
        form.put("achievementName", nameField.getText());
        form.put("achievementDescription", descriptionField.getText());
        form.put("achievementTime", timeField.getText());
        form.put("achievementPerson", personField.getText());



        req.add("achievementMap", form);

        String url = "/api/achievement/add";
        DataResponse res = HttpRequestUtil.request(url, req);

        if (res != null ) {
            MessageDialog.showDialog("添加成功");
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
        Map<String, String> achievementMap = new HashMap<>();
        achievementMap.put("achievementId", data.get("achievementId").toString());
        req.add("achievementMap", achievementMap);

        DataResponse res = HttpRequestUtil.request("/api/achievement/deleteAchievement", req);
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
        descriptionField.clear();
        timeField.clear();
        personField.clear();
        searchField.clear();

    }


    public void onAddPersonButtonClick(ActionEvent actionEvent) {
    }

    public void onRemovePersonButtonClick(ActionEvent actionEvent) {
    }
}