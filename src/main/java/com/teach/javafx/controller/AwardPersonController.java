package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwardPersonController {
    @FXML
    private TableView<Map<String,String>> awardPersonTableView;
    @FXML
    private TableColumn<Map<String,String>,String> StudentNameColumn;
    @FXML
    private TableColumn<Map<String,String>,String> StudentEmailColumn;
    @FXML
    private TableColumn<Map<String,String>,String> StudentPhoneColumn;
    @FXML
    private TextField nameTextField;
    @FXML
    private Button saveOrAddButton;

    private boolean isNew = false;
    private ArrayList<Map<String, String>> studentArrayList = new ArrayList<>();
    private final ObservableList<Map<String, String>> studentObservableList = FXCollections.observableArrayList();
    private String personId;
    private Integer awardId;
    private ArrayList<Map<String, String>> awardPersonList;

    public void AwardPersonController() {
    }

    @FXML
    public void initialize(Integer awardId){
        //还是有问题
        this.awardId = awardId;
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("awardId",awardId);
        DataResponse dataResponse = HttpRequestUtil.request("/api/award/getCandidatesList",dataRequest);
        if (dataResponse != null && dataResponse.getCode() == 0) {
            studentArrayList = (ArrayList<Map<String, String>>) dataResponse.getData();
            setTableViewData();
        }
        //set the text field is not editable
        setTextFieldEditable(false);
        //initialize the column factory
        StudentNameColumn.setCellValueFactory(new MapValueFactory("name"));
        StudentPhoneColumn.setCellValueFactory(new MapValueFactory("phone"));
        StudentEmailColumn.setCellValueFactory(new MapValueFactory("email"));
        //initialize row select
        TableView.TableViewSelectionModel<Map<String, String>> tableViewSelectionModel = awardPersonTableView.getSelectionModel();
        ObservableList<Integer> list = tableViewSelectionModel.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        //set table view data
        refreshTableView();
    }

    public void setTableViewData(){
        studentObservableList.clear();
        for (Map<String, String> stringStringMap : studentArrayList) {
            studentObservableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        awardPersonTableView.setItems(studentObservableList);
    }

    public void refreshTableView(){
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("awardId",awardId);
        DataResponse dataResponse = HttpRequestUtil.request("/api/award/getCandidatesList",dataRequest);
        if (dataResponse != null){
            try {
                studentArrayList = (ArrayList<Map<String, String>>) dataResponse.getData();
            } catch (ClassCastException exception){
                System.out.println("请后端发送正确类型的数据!");
            }
        } else {
            System.out.println("failed to get data from the backEnd!");
        }
        setTableViewData();
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        isNew = false;
        setTextFieldEditable(false);
        saveOrAddButton.setVisible(false);
        saveOrAddButton.setManaged(false);
        Map map = awardPersonTableView.getSelectionModel().getSelectedItem();
        changeVBoxInfo();
    }

    public void changeVBoxInfo(){
        Map<String, String> map = awardPersonTableView.getSelectionModel().getSelectedItem();
        String name = CommonMethod.getString(map, "name");
        nameTextField.setText(name);
    }

    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
        isNew = true;
        clearPanel();
        setTextFieldEditable(true);
        saveOrAddButton.setManaged(true);
        saveOrAddButton.setVisible(true);
        saveOrAddButton.setText("添加");
    }

    public void clearPanel(){
        nameTextField.setText("");
    }

    public void onDeleteButtonClick(ActionEvent actionEvent) {
        Map<String, String> awardPersonMap = awardPersonTableView.getSelectionModel().getSelectedItem();
        personId = CommonMethod.getString(awardPersonMap, "awardPersonId");
        if (personId == null || personId.isEmpty()) {
            MessageDialog.showDialog("请选中要删除的获奖者");
        } else {
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("personId", personId);
            dataRequest.add("awardPersonMap", awardPersonMap);
            DataResponse dataResponse = HttpRequestUtil.request("/api/award/CandidatesDelete", dataRequest);
            if (dataResponse != null){
                if (dataResponse.getCode() == 0){
                    MessageDialog.showDialog("删除成功");
                    personId = null;
                    refreshTableView();
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            } else {
                System.out.println("error in delete candidate");
            }
        }
    }

    public void onImportButtonClick(ActionEvent actionEvent) {

    }

    public void onSaveOrAddButtonClick(){
        String num = nameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("num", num);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);
        if (res != null && res.getCode() == 0) {
            studentArrayList = (ArrayList<Map<String, String>>) res.getData();
            setTableViewData();
        }
        Map<String, String> studentMap = new HashMap<>();
        studentMap.put("num", num);
        studentMap.put("personId", personId);
        if (isNew) {
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("studentMap", studentMap);
            dataRequest.add("awardId", awardId.toString());
            DataResponse dataResponse = HttpRequestUtil.request("/api/award/addCandidate", dataRequest);
            if (dataResponse != null) {
                MessageDialog.showDialog("Success!");
                clearPanel();
                setTextFieldEditable(false);
                refreshTableView();
                isNew = false;
            } else {
                MessageDialog.showDialog("Failed");
            }
        }
        refreshTableView();
    }

    @FXML
    protected void onEditButtonClick(){
        setTextFieldEditable(true);
        saveOrAddButton.setVisible(true);
        saveOrAddButton.setManaged(true);
        saveOrAddButton.setText("保存");
    }

    public void setTextFieldEditable(boolean is){
        nameTextField.setEditable(is);
    }
}


