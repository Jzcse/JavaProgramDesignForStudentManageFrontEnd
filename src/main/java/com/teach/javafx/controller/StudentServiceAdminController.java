package com.teach.javafx.controller;


import com.teach.javafx.controller.inner.StudentServiceAdminInnerController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 处理来自学生端的服务请求的控制器
 * 对应于fxml文件中的 student-service-leave-request-panel.fxml
 */
public class StudentServiceAdminController {

    // components
    @FXML
    private TableView<Map<String, String>> dataTableView;
    @FXML
    private TextField queryTextField;
    @FXML
    private TableColumn<Map<String, String>, String> nameColumn, numColumn, deptColumn, majorColumn, classNameColumn,
            startDateColumn, endDateColumn, reasonColumn, contactColumn, statusColumn, requestDateColumn;
    @FXML
    private Pagination pagination;

    // global variables
    private ArrayList<Map<String, String>> leaveRequestList = new ArrayList();
    private ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList();

    // return a callback function to set cell value factory for table column
    private javafx.util.Callback<TableColumn.CellDataFeatures<Map<String, String>, String>, ObservableValue<String>> createCellValueFactory(String key) {
        return callBack -> {
            Map<String, String> map = callBack.getValue();
            return new SimpleStringProperty(CommonMethod.getString(map, key));
        };
    }

    private void setTableViewData() {
        observableList.clear();
        for (Map<String, String> map : leaveRequestList) {
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.getSelectionModel().clearSelection();
        dataTableView.setItems(observableList);
    }

    public void initialize() {
        // count total pages
        DataResponse dataResponseForPageCount = HttpRequestUtil.request("/api/leaveRequest/getLeaveRequestCount", new DataRequest());
        if (dataResponseForPageCount != null) {
            if (dataResponseForPageCount.getCode() == 0) {
                int requestCount = Integer.parseInt(dataResponseForPageCount.getData().toString());
                int pageCount = requestCount / 33 + 1;
                if(requestCount % 33 == 0) {
                    pageCount -= 1;
                }
                pagination.setPageCount(pageCount);
            } else {
                System.err.println("error to get leave request count");
            }
        }
        // init data
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("page", "0");
        dataRequest.add("size", "33");
        DataResponse dataResponse = HttpRequestUtil.request("/api/leaveRequest/getLeaveRequestListForAdmin", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                try {
                    leaveRequestList = (ArrayList<Map<String, String>>) dataResponse.getData();
                } catch (ClassCastException e) {
                    System.err.println("error to cast data to ArrayList<Map<String, String>>");
                }
            }
        } else {
            System.err.println("error to get leave request list");
        }
        // set pagination
        pagination.setPageFactory(pageIndex -> {
            System.out.println("set pagination");
            // get data
            DataRequest dataRequestForNewList = new DataRequest();
            dataRequestForNewList.add("page", String.valueOf(pageIndex));
            dataRequestForNewList.add("size", "33");
            DataResponse dataResponseForNewList = HttpRequestUtil.request("/api/leaveRequest/getLeaveRequestListForAdmin", dataRequestForNewList);
            if (dataResponseForNewList != null) {
                if (dataResponseForNewList.getCode() == 0) {
                    try {
                        leaveRequestList = (ArrayList<Map<String, String>>) dataResponseForNewList.getData();
                    } catch (ClassCastException e) {
                        System.err.println("error to cast data to ArrayList<Map<String, String>>");
                    }
                }
            } else {
                System.err.println("error to get leave request list");
            }
            // set table view data
            setTableViewData();
            return null;
        });
        nameColumn.setCellValueFactory(createCellValueFactory("name"));
        numColumn.setCellValueFactory(createCellValueFactory("num"));
        deptColumn.setCellValueFactory(createCellValueFactory("dept"));
        majorColumn.setCellValueFactory(createCellValueFactory("major"));
        classNameColumn.setCellValueFactory(createCellValueFactory("className"));
        startDateColumn.setCellValueFactory(createCellValueFactory("startDate"));
        endDateColumn.setCellValueFactory(createCellValueFactory("endDate"));
        reasonColumn.setCellValueFactory(createCellValueFactory("reason"));
        contactColumn.setCellValueFactory(createCellValueFactory("contact"));
        statusColumn.setCellValueFactory(callBack -> {
            Map<String, String> map = callBack.getValue();
            String status = CommonMethod.getString(map, "status");
            if(status.equals("0")) {
                return new SimpleStringProperty("未处理");
            } else if (status.equals("1")) {
                return new SimpleStringProperty("已同意");
            } else {
                return new SimpleStringProperty("已拒绝");
            }
        });
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "未处理" -> setStyle("-fx-text-fill: #BBBBBB;");
                        case "已拒绝" -> setStyle("-fx-text-fill: #DC3545;");
                        case "已同意" -> setStyle("-fx-text-fill: #28A745;");
                    }
                }
            }
        });
        requestDateColumn.setCellValueFactory(createCellValueFactory("requestDate"));
        // set table view data
        setTableViewData();

        TableView.TableViewSelectionModel<Map<String, String>> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
    }

    private void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        // 检查是否有选中项
        Map<String, String> map = dataTableView.getSelectionModel().getSelectedItem();
        if (map == null) {
            return;
        }
        try {
            String leaveRequestId = CommonMethod.getString(map, "leaveRequestId");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/student-service-leave-request-panel-admin-inner.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setWidth(600);
            stage.setHeight(400);
            stage.setResizable(false);
            stage.setTitle("请假处理");
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            //
            StudentServiceAdminInnerController studentServiceAdminInnerController = loader.getController();
            studentServiceAdminInnerController.initialize(leaveRequestId);
            stage.showAndWait();
            // refresh table view data
            refreshLeaveRequestList();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadPageData(int pageIndex) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("page", String.valueOf(pageIndex));
        dataRequest.add("size", "33");
        DataResponse dataResponse = HttpRequestUtil.request("/api/leaveRequest/getLeaveRequestListForAdmin", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                try {
                    leaveRequestList = (ArrayList<Map<String, String>>) dataResponse.getData();
                    setTableViewData();
                } catch (ClassCastException e) {
                    System.err.println("error to cast data to ArrayList<Map<String, String>>");
                }
            }
        } else {
            System.err.println("error to get leave request list");
        }
    }

    private void refreshLeaveRequestList(){
        int currentPage = pagination.getCurrentPageIndex();
        loadPageData(currentPage);
    }

    public void onQueryButtonClick(ActionEvent actionEvent) {
        String query = queryTextField.getText();

    }
}
