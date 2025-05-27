package com.teach.javafx.controller.inner;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.util.CommonMethod;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.Flow;

public class StudentServiceAdminInnerController {

    // components
    @FXML
    private Label nameLabel, startDateLabel, endDateLabel, contactLabel, numLabel;
    @FXML
    private TextArea reasonTextArea;
    @FXML
    private Button approveButton;
    @FXML
    private Button rejectButton;

    // global variables
    private String leaveRequestId;
    private Integer currentStatus;

    public void initialize(String leaveRequestId) {
        this.leaveRequestId = leaveRequestId;
        // get leave request data
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("leaveRequestId", leaveRequestId);
        DataResponse dataResponse = HttpRequestUtil.request("/api/leaveRequest/getLeaveRequestDetailInfo", dataRequest);
        if (dataResponse == null) {
            MessageDialog.showDialog("连接失败，请检查网络!");
            System.err.println("Failed to get leave request detail info");
        } else {
            if (dataResponse.getCode() == 0) {
                Map<String, String> data = (Map<String, String>) dataResponse.getData();
                nameLabel.setText(CommonMethod.getString(data, "name"));
                numLabel.setText(CommonMethod.getString(data, "num"));
                startDateLabel.setText(CommonMethod.getString(data, "startDate"));
                endDateLabel.setText(CommonMethod.getString(data, "endDate"));
                reasonTextArea.setText(CommonMethod.getString(data, "reason"));
                contactLabel.setText(CommonMethod.getString(data, "contact"));
                currentStatus = Integer.parseInt(CommonMethod.getString(data, "status"));
            } else {
                MessageDialog.showDialog("获取数据失败!");
                System.err.println("Failed to get leave request detail info");
            }
        }

        // set button visibility
        if (currentStatus == 0) {
            approveButton.setVisible(true);
            rejectButton.setVisible(true);
        } else {
            approveButton.setVisible(false);
            rejectButton.setVisible(false);
        }
    }

    public void onApproveButtonClick(ActionEvent actionEvent) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("leaveRequestId", leaveRequestId);
        dataRequest.add("result", "1");
        DataResponse dataResponse = HttpRequestUtil.request("/api/leaveRequest/changeLeaveRequestStatus", dataRequest);
        if (dataResponse == null) {
            MessageDialog.showDialog("连接失败，请检查网络!");
            System.err.println("Failed to change leave request status");
        } else {
            if (dataResponse.getCode() == 0) {
                MessageDialog.showDialog("批准成功!");
                System.out.println("Successfully to change leave request status");
                closeCurrentWindow(actionEvent);
            } else {
                MessageDialog.showDialog("处理失败!");
                System.err.println("Failed to change leave request status");
            }
        }
    }

    public void onRejectButtonClick(ActionEvent actionEvent) {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("leaveRequestId", leaveRequestId);
        dataRequest.add("result", "2");
        DataResponse dataResponse = HttpRequestUtil.request("/api/leaveRequest/changeLeaveRequestStatus", dataRequest);
        if (dataResponse == null) {
            MessageDialog.showDialog("连接失败，请检查网络!");
            System.err.println("Failed to change leave request status");
        } else {
            if (dataResponse.getCode() == 0) {
                MessageDialog.showDialog("拒绝成功!");
                System.out.println("Successfully to change leave request status");
                closeCurrentWindow(actionEvent);
            } else {
                MessageDialog.showDialog("处理失败!");
                System.err.println("Failed to change leave request status");
            }
        }
    }

    private void closeCurrentWindow(ActionEvent actionEvent) {
        Button sourceButton = (Button) actionEvent.getSource();
        Stage stage = (Stage) sourceButton.getScene().getWindow();
        stage.close();
    }
}
