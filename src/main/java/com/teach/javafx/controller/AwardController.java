package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class AwardController extends ToolController {
    // TableView components
    @FXML
    private TableView<Map<String, String>> dataTableView;
    @FXML
    private TableColumn<Map<String, String>, String> awardNameColumn;
    @FXML
    private TableColumn<Map<String, String>, String> awardTypeColumn;
    @FXML
    private TableColumn<Map<String, String>, String> awardLevelColumn;
    @FXML
    private TableColumn<Map<String, String>, String> awardTimeColumn;
    @FXML
    private TableColumn<Map<String, String>, String> awardSizeColumn;

    // Detail panel components
    @FXML
    private TextField awardNameField;
    @FXML
    private ComboBox<String> awardTypeField;
    @FXML
    private ComboBox<String> awardLevelField;
    @FXML
    private TextField awardSizeField;
    @FXML
    private TextField awardTimeField;
    @FXML
    private TextField awardSearchNameTextField;

    // Other variables
    private boolean isNew = false;
    private Integer currentAwardId;
    private ArrayList<Map<String, String>> awardList = new ArrayList<>();
    private final ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList();

    @FXML
    protected void onQueryButtonClick() {
        String awardName = awardSearchNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("awardName", awardName);
        DataResponse res = HttpRequestUtil.request("/api/award/getSelectedList", req);
        if (res != null && res.getCode() == 0) {
            awardList = (ArrayList<Map<String, String>>) res.getData();
            setTableViewData();
        }
    }

    public void refreshAwardList() {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("size", 10);
        DataResponse dataResponse = HttpRequestUtil.request("/api/award/getAwardList", dataRequest);
        if (dataResponse != null && dataResponse.getCode() == 0) {
            Map<Integer, Map<String, String>> awardMap = (Map<Integer, Map<String, String>>) dataResponse.getData();
            awardList = new ArrayList<>(awardMap.values());
            setTableViewData();
        }
    }

    public void setTableViewData() {
        observableList.clear();
        for (Map<String, String> award : awardList) {
            observableList.addAll(FXCollections.observableArrayList(award));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataRequest dataRequest = new DataRequest();
        DataResponse dataResponse = HttpRequestUtil.request("/api/award/getAwardList", dataRequest);
        if (dataResponse != null && dataResponse.getCode() == 0) {
            Map<Integer, Map<String, String>> awardMap = (Map<Integer, Map<String, String>>) dataResponse.getData();
            awardList = new ArrayList<>(awardMap.values());
        }

        // Set up table columns
        awardNameColumn.setCellValueFactory(new MapValueFactory("awardName"));
        awardTimeColumn.setCellValueFactory(new MapValueFactory("awardTime"));
        awardSizeColumn.setCellValueFactory(new MapValueFactory("awardSize"));
        awardTypeColumn.setCellValueFactory(new MapValueFactory("awardType"));
        awardLevelColumn.setCellValueFactory(new MapValueFactory("awardLevel"));

        // Set up selection listener
        TableView.TableViewSelectionModel<Map<String, String>> selectionModel = dataTableView.getSelectionModel();
        ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
        selectedIndices.addListener(this::onTableRowSelect);

        // Set initial table data
        setTableViewData();
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        isNew = false;
        changeAwardInfo();
    }

    public void changeAwardInfo() {
        Map<String, String> selectedRow = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            clearPanel();
            return;
        }

        // 直接从选中的行数据中获取信息并设置到对应的组件中
        awardSearchNameTextField.setText(selectedRow.get("awardName"));
        awardNameField.setText(selectedRow.get("awardName"));
        awardTypeField.setValue(selectedRow.get("awardType"));
        awardLevelField.setValue(selectedRow.get("awardLevel"));
        awardSizeField.setText(selectedRow.get("awardSize"));
        awardTimeField.setText(selectedRow.get("awardTime"));

        currentAwardId = CommonMethod.getInteger(selectedRow, "awardId");
    }

    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
        this.isNew = true;
        clearPanel();
    }

    public void clearPanel() {
        currentAwardId = null;
        awardNameField.setText("");
        awardTypeField.setValue(null);
        awardLevelField.setValue(null);
        awardSizeField.setText("");
        awardTimeField.setText("");
    }

    @FXML
    public void onDeleteButtonClick(ActionEvent actionEvent) {
        Map<String, String> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请选定要删除的奖项");
        } else {
            int choice = MessageDialog.choiceDialog("确定要删除吗？");
            if (choice == MessageDialog.CHOICE_YES) {
                currentAwardId = CommonMethod.getInteger(selected, "awardId");
                DataRequest dataRequest = new DataRequest();
                dataRequest.add("awardId", currentAwardId);
                DataResponse dataResponse = HttpRequestUtil.request("/api/award/deleteAward", dataRequest);

                if (dataResponse != null) {
                    if (dataResponse.getCode() == 0) {
                        MessageDialog.showDialog("删除成功！");
                        refreshAwardList();
                    } else {
                        MessageDialog.showDialog(dataResponse.getMsg());
                    }
                }
            }
        }
    }

    @FXML
    public void onSaveButtonClick(ActionEvent actionEvent) {
        if (isNew) {
            if (awardNameField.getText().isEmpty()) {
                MessageDialog.showDialog("奖项名称不能为空");
                return;
            }

            Map<String, String> awardForm = new HashMap<>();
            awardForm.put("awardName", awardNameField.getText());
            awardForm.put("awardType", awardTypeField.getValue());
            awardForm.put("awardLevel", awardLevelField.getValue());
            awardForm.put("awardSize", awardSizeField.getText());
            awardForm.put("awardTime", awardTimeField.getText());

            DataRequest dataRequest = new DataRequest();
            dataRequest.add("awardMap", awardForm);
            DataResponse dataResponse = HttpRequestUtil.request("/api/award/addAward", dataRequest);

            if (dataResponse != null) {
                if (dataResponse.getCode() == 0) {
                    MessageDialog.showDialog("添加奖项成功");
                    refreshAwardList();
                    clearPanel();
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            } else {
                MessageDialog.showDialog("请求失败，未获取到响应数据");
            }
        } else {
            Map selected = dataTableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                MessageDialog.showDialog("请先选择一个奖项");
                return;
            }

            DataRequest dataRequest = new DataRequest();

            Map<String, String> form = new HashMap<>();
            form.put("awardId", CommonMethod.getString(selected, "awardId"));
            form.put("awardName", awardNameField.getText());
            form.put("awardType", awardTypeField.getValue());
            form.put("awardLevel", awardLevelField.getValue());
            form.put("awardSize", awardSizeField.getText());
            form.put("awardTime", awardTimeField.getText());

            dataRequest.add("form", form);
            DataResponse dataResponse = HttpRequestUtil.request("/api/award/updateAward", dataRequest);

            if (dataResponse != null) {
                if (dataResponse.getCode() == 0) {
                    MessageDialog.showDialog("修改成功！");
                    refreshAwardList();
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            } else {
                MessageDialog.showDialog("请求失败，未获取到响应数据");
            }
        }
    }

    @FXML
    public void onSetCandidatesButtonClick(ActionEvent actionEvent) throws Exception {
        Map<String, String> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请先选择一个奖项");
            return;
        }
        Map<String, String> studentMap = new HashMap<>();

        currentAwardId = CommonMethod.getInteger(selected, "awardId");
        openChildWindow(currentAwardId);
    }

    @FXML
    public void onGetCandidatesSizeButtonClick(ActionEvent actionEvent) {
        Map<String, String> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageDialog.showDialog("请先选择一个奖项");
            return;
        }

        currentAwardId = CommonMethod.getInteger(selected, "awardId");
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("awardId", currentAwardId);

        DataResponse dataResponse = HttpRequestUtil.request("/api/award/getCandidatesListSize", dataRequest);
        if (dataResponse != null && dataResponse.getCode() == 0) {
            MessageDialog.showDialog("获奖人数: " + dataResponse.getData());
        } else {
            MessageDialog.showDialog(dataResponse != null ? dataResponse.getMsg() : "获取失败");
        }
    }

    @FXML
    private void openChildWindow(Integer currentAwardId) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/child-award-panel.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setResizable(false);
        stage.setTitle("Candidates");
        Scene scene = new Scene(root, 500, 400);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        AwardPersonController controller = loader.getController();
        controller.initialize(currentAwardId);
        stage.showAndWait();
    }
}
