package com.teach.javafx.controller.studentEnd;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StudentServiceLeaveRequestController extends ToolController {

    /**
     * fxml components
     */
    @FXML
    private TextField nameField, numField, contactField, reasonField;
    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private TableColumn<Map<String, String>, String> nameColumn, numColumn, contactColumn, reasonColumn, startDateColumn, endDateColumn, statusColumn, requestTimeColumn;
    @FXML
    private TableView<Map<String, String>> dataTableView;
    @FXML
    private Button summitButton;

    /**
     * global variables
     */
    private String personId;
    private String num = GlobalSession.getInstance().getNum();
    private ArrayList<Map<String, String>> leaveRequestList = new ArrayList<>();
    private ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList();

    public void initialize() {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("num", num);
        DataResponse dataResponse = HttpRequestUtil.request("/api/leaveRequest/getLeaveRequestListForStudent", dataRequest);
        if (dataResponse != null){
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

        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        numColumn.setCellValueFactory(new MapValueFactory("num"));
        contactColumn.setCellValueFactory(new MapValueFactory("contact"));
        reasonColumn.setCellValueFactory(new MapValueFactory("reason"));
        startDateColumn.setCellValueFactory(new MapValueFactory("startTime"));
        endDateColumn.setCellValueFactory(new MapValueFactory("endTime"));
        statusColumn.setCellValueFactory(cellData -> {
            int status = Integer.parseInt(cellData.getValue().get("status"));
            String result;
            if (status == 0) {
                result = "未处理";
            } else if (status == 1) {
                result = "已同意";
            } else {
                result = "被拒绝";
            }
            return new SimpleStringProperty(result);
        });
        requestTimeColumn.setCellValueFactory(cellData -> {
            String requestTime = cellData.getValue().get("requestTime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(requestTime, formatter);
            LocalDateTime localDateTime = zonedDateTime.withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime();
            return new SimpleStringProperty(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        });
        setTableViewData();
        System.err.println("studentServiceLeaveRequestController.initialize");
    }

    private void refreshTableViewData() {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("num", num);
        DataResponse dataResponse = HttpRequestUtil.request("/api/leaveRequest/getLeaveRequestListForStudent", dataRequest);
        if (dataResponse == null) {
            System.err.println("Failed to get new leave request list in refreshTableViewData");
        } else {
            if (dataResponse.getCode() == 0) {
                System.out.println("Successfully to get new leave request list in refreshTableViewData");
                try {
                    leaveRequestList = (ArrayList<Map<String, String>>) dataResponse.getData();
                    setTableViewData();
                } catch (ClassCastException e) {
                    System.err.println("error to cast data to ArrayList<Map<String, String>>");
                }
            } else {
                System.err.println("Failed to get new leave request list in refreshTableViewData");
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        }
    }

    private void setTableViewData() {
        observableList.clear();
        for (Map<String, String> leaveRequestMap : leaveRequestList) {
            observableList.addAll(FXCollections.observableArrayList(leaveRequestMap));
        }
        dataTableView.setItems(observableList);
    }

    public void onSummitButtonClick(ActionEvent actionEvent) {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> data = new HashMap<>();
        data.put("num", num);
        data.put("contact", contactField.getText());
        data.put("reason", reasonField.getText());
        data.put("requestDate", new Date().toString());
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            System.err.println("startDate or endDate is null");
            MessageDialog.showDialog("please select startDate and endDate");
            return;
        }
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            System.err.println("startDate is after endDate");
            MessageDialog.showDialog("startDate is after endDate");
            return;
        }
        data.put("startDate", startDatePicker.getValue().toString());
        data.put("endDate", endDatePicker.getValue().toString());
        dataRequest.add("data", data);
        DataResponse dataResponse = HttpRequestUtil.request("/api/leaveRequest/addLeaveRequest", dataRequest);

        if (dataResponse == null) {
            System.err.println("Failed to add leave request");
        } else {
            if (dataResponse.getCode() == 0) {
                MessageDialog.showDialog("add leave request successfully");
                leaveRequestList.add(data);
                refreshTableViewData();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        }
        System.err.println("studentServiceLeaveRequestController.onSubmitButtonClick");
    }
}
