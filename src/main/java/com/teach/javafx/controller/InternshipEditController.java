package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternshipEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private TextField InternshipTimeField;
    @FXML
    private TextField InternshipSpaceField;
    private InternshipTableController InternshipTableController=null;
    private Integer InternshipId=null;
    private void initialize() {
    }
    public void okButtonClick() {
        if (!InternshipTableController.canEdit) {
            Map data = new HashMap();
            OptionItem op;
            op = studentComboBox.getSelectionModel().getSelectedItem();
            if (op != null) {
                data.put("personId", Integer.valueOf(op.getValue()));
            }
            data.put("InternshipTime", InternshipTimeField.getText());
            data.put("InternshipSpace", InternshipSpaceField.getText());
            data.put("InternshipId", InternshipId);
            InternshipTableController.doClose("ok", data);
        }
        else {
            Map data = new HashMap();
            OptionItem op;
            op = studentComboBox.getSelectionModel().getSelectedItem();
            if (op != null) {
                data.put("personId", Integer.valueOf(op.getValue()));
            }
            data.put("InternshipTime", InternshipTimeField.getText());
            data.put("InternshipSpace", InternshipSpaceField.getText());
            data.put("InternshipId", InternshipId);
            InternshipTableController.doEdit("ok", data);

        }
    }
    public void cancelButtonClick() {
        InternshipTableController.doClose("cancel",null);
    }
    public void setInternshipTableController(InternshipTableController InternshipTableController) {
        this.InternshipTableController=InternshipTableController;
    }
    public void init(){
        studentList=InternshipTableController.getStudentList();
        studentComboBox.getItems().addAll(studentList);
    }
    public void showDialog(Map data) {
        if(data == null) {
            InternshipId=null;
            studentComboBox.getSelectionModel().select(-1);
            studentComboBox.setDisable(false);
            InternshipTimeField.setText("");
            InternshipSpaceField.setText("");
        }
        else {
             InternshipId = CommonMethod.getInteger(data, "InternshipId");
            Integer studentId = CommonMethod.getInteger(data, "studentId");
            String InternshipTime = CommonMethod.getString(data, "InternshipTime");
            String InternshipSpace = CommonMethod.getString(data, "InternshipSpace");
            if (studentId != null) {
                studentComboBox.getSelectionModel().select(studentList.size()-studentId);
                studentComboBox.setDisable(false); // 禁用下拉框（可能是编辑状态）
            } else {
                studentComboBox.setDisable(false); // 启用下拉框（可能是新增状态）
            }
            InternshipTimeField.setText(InternshipTime);
            InternshipSpaceField.setText(InternshipSpace);

        }
    }
}
