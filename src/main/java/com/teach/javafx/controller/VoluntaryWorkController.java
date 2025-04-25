package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoluntaryWorkController {
    @FXML
    private TableView<Map<String,String>> dataTableView;
    @FXML
    private TableColumn<Map<String,String>,String> workNameColumn;
    @FXML
    private TableColumn<Map<String,String>,String> workTimeColumn;
    @FXML
    private TableColumn<Map<String,String>,String> workSizeColumn;
    @FXML
    private TableColumn<Map<String,String>,String> workLevelColumn;
    @FXML
    private TableColumn<Map<String,String>,String> workTypeColumn;

    @FXML
    private TextField workNameTextField;
    @FXML
    private TextField workTimeTextField;
    @FXML
    private TextField workSizeTextField;
    @FXML
    private TextField workSearchNameField;
    @FXML
    private ComboBox<String> workTypeComboBox;
    @FXML
    private ComboBox<String> workLevelComboBox;

    private boolean isNew = false;
    private Integer currentWorkId;
    private ArrayList<Map<String,String>> workList = new ArrayList<>();
    private final ObservableList<Map<String,String>> observableList = FXCollections.observableArrayList();

    @FXML
    private void onQueryButtonClick(){
        String awardName = workNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("workName",awardName);
        DataResponse res = HttpRequestUtil.request("/api/voluntaryWork/getSelectedList",req);
        if(res != null && res.getCode() == 0){
            workList = (ArrayList<Map<String,String>>) res.getData();
            setTableView();
        }
    }

    public void refreshWorkList(){
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("size",10);
        DataResponse dataResponse = HttpRequestUtil.request("/api/voluntaryWork/getVoluntaryWorkList",dataRequest);
        if(dataResponse != null && dataResponse.getCode() == 0){
            Map<Integer,Map<String,String>> workMap = (Map<Integer,Map<String,String>>) dataResponse.getData();
            workList = (ArrayList<Map<String,String>>) dataResponse.getData();
            setTableView();
        }
    }

    public void setTableView(){
        observableList.clear();
        for(Map<String,String> work : workList){
            observableList.addAll(FXCollections.observableArrayList(work));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize(){
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("size",10);
        DataResponse dataResponse = HttpRequestUtil.request("/api/voluntaryWork/getVoluntaryWorkList",dataRequest);
        if (dataResponse != null && dataResponse.getCode() == 0) {
            Map<Integer, Map<String, String>> workMap = (Map<Integer, Map<String, String>>) dataResponse.getData();
            workList = new ArrayList<>(workMap.values());
        }

        workNameColumn.setCellValueFactory(new MapValueFactory("workName"));
        workTimeColumn.setCellValueFactory(new MapValueFactory("workTime"));
        workSizeColumn.setCellValueFactory(new MapValueFactory("workSize"));
        workLevelColumn.setCellValueFactory(new MapValueFactory("workLevel"));
        workTypeColumn.setCellValueFactory(new MapValueFactory("workType"));

        TableView.TableViewSelectionModel<Map<String,String>> selectionModel = dataTableView.getSelectionModel();
        ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
        selectedIndices.addListener(this::onTableRowSelect);

        setTableView();
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        isNew = false;
        changeWorkInfo();
    }

    public void changeWorkInfo(){
        Map<String,String> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }

        currentWorkId = CommonMethod.getInteger(form,"workId");
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("workId",currentWorkId);
        DataResponse res = HttpRequestUtil.request("/api/voluntaryWork/getVoluntaryWorkList",dataRequest);

        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }

        form = (Map) res.getData();
        workNameTextField.setText(CommonMethod.getString(form,"workName"));
        workTimeTextField.setText(CommonMethod.getString(form,"workTime"));
        workSizeTextField.setText(CommonMethod.getString(form,"workSize"));
        workTypeComboBox.setValue(CommonMethod.getString(form,"workType"));
        workLevelComboBox.setValue(CommonMethod.getString(form,"workLevel"));
        workSearchNameField.setText(CommonMethod.getString(form,"workName"));
    }

    @FXML
    public void onAddButtonClick(){
        isNew = true;
        clearPanel();
    }

    public void clearPanel(){
        currentWorkId = null;
        workNameTextField.setText("");
        workTimeTextField.setText("");
        workSizeTextField.setText("");
        workTypeComboBox.setValue(null);
        workLevelComboBox.setValue(null);
        workSearchNameField.setText("");
    }

    @FXML
    public void onDeleteButtonClick(){
        Map<String,String> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请选择要删除的项目");
            return;
        }else {
            int choice = MessageDialog.choiceDialog("确定要删除选中的项目吗？");
            if (choice == MessageDialog.CHOICE_YES) {
                currentWorkId = CommonMethod.getInteger(selected,"workId");
                DataRequest dataRequest = new DataRequest();
                dataRequest.add("workId",currentWorkId);
                DataResponse dataResponse = HttpRequestUtil.request("/api/voluntaryWork/deleteVoluntaryWork",dataRequest);

                if (dataResponse != null) {
                    if (dataResponse.getCode() == 0) {
                        MessageDialog.showDialog("删除成功！");
                        refreshWorkList();
                    } else {
                        MessageDialog.showDialog(dataResponse.getMsg());
                    }
                }
            }
        }
    }

    @FXML
    public void onSaveButtonClick(){
        if (isNew) {
            if (workNameTextField.getText().isEmpty()) {
                MessageDialog.showDialog("项目名称不能为空");
                return;
            }

            Map<String,String> workForm = new HashMap<>();
            workForm.put("workName",workNameTextField.getText());
            workForm.put("workTime",workTimeTextField.getText());
            workForm.put("workSize",workSizeTextField.getText());
            workForm.put("workType",workTypeComboBox.getValue());
            workForm.put("workLevel",workLevelComboBox.getValue());

            DataRequest dataRequest = new DataRequest();
            dataRequest.add("voluntaryWorkMap",workForm);
            DataResponse dataResponse = HttpRequestUtil.request("/api/voluntaryWork/addVoluntaryWork",dataRequest);

            if (dataResponse!= null) {
                if (dataResponse.getCode() == 0) {
                    MessageDialog.showDialog("新增成功！");
                    refreshWorkList();
                    clearPanel();
                }else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            }else {
                MessageDialog.showDialog("新增失败！");
            }
        }else {
            Map selected = dataTableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                MessageDialog.showDialog("请选择要修改的项目");
                return;
            }

            DataRequest dataRequest = new DataRequest();

            Map<String,String> form = new HashMap<>();
            form.put("workId",CommonMethod.getString(selected,"workId"));
            form.put("workName",workNameTextField.getText());
            form.put("workTime",workTimeTextField.getText());
            form.put("workSize",workSizeTextField.getText());
            form.put("workType",workTypeComboBox.getValue());
            form.put("workLevel",workLevelComboBox.getValue());

            dataRequest.add("form",form);
            DataResponse dataResponse = HttpRequestUtil.request("/api/voluntaryWork/updateVoluntaryWork",dataRequest);

            if (dataResponse!= null) {
                if (dataResponse.getCode() == 0) {
                    MessageDialog.showDialog("修改成功！");
                    refreshWorkList();
                }else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            }else {
                MessageDialog.showDialog("修改失败！");
            }
        }
    }
}
