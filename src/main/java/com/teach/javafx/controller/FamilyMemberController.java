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

public class FamilyMemberController {
    //import components from fxml
    @FXML
    private TableView<Map<String, String>> familyTableView;
    @FXML
    private TableColumn<Map<String, String>, String> relation;
    @FXML
    private TableColumn<Map<String, String>, String> name;
    @FXML
    private TableColumn<Map<String, String>, String> age;
    @FXML
    private TableColumn<Map<String, String>, String> gender;
    @FXML
    private TableColumn<Map<String, String>, String> unit;
    @FXML
    private VBox vBoxPanel;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField ageTextField;
    @FXML
    private ComboBox<OptionItem> genderCombo;
    @FXML
    private TextField unitTextField;
    @FXML
    private TextField relationTextField;
    @FXML
    private Button saveOrAddButton;
    @FXML
    private Button editButton;

    //service
    private boolean isNew = false;
    private ArrayList<Map<String, String>> familyArrayList;
    private final ObservableList<Map<String, String>> familyObservableList = FXCollections.observableArrayList();
    private String personId;
    private String memberId;
    private List<OptionItem> genderList;

    //builder
    public FamilyMemberController(){
    }

    @FXML
    public void initialize(String personId){
        //personID
        this.personId = personId;
        //initialize the hide vbox
        setVBoxPanelVisible(false);
        //set the text field is not editable
        setTextFieldEditable(false);
        //initialize the genderComboBox
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");
        genderCombo.getItems().addAll(genderList);
        //initialize the column factory
        relation.setCellValueFactory(new MapValueFactory("relation"));
        name.setCellValueFactory(new MapValueFactory("name"));
        gender.setCellValueFactory(new MapValueFactory("gender"));
        gender.setCellValueFactory(cellData -> {
            int genderValue = Integer.parseInt(cellData.getValue().get("gender"));
            String genderName;
            if (genderValue == 1) {
                genderName = "男";
            } else if (genderValue == 2) {
                genderName = "女";
            } else {
                genderName = "";
            }
            return new SimpleStringProperty(genderName);
        });
        age.setCellValueFactory(new MapValueFactory("age"));
        unit.setCellValueFactory(new MapValueFactory("unit"));
        //initialize row select
        TableView.TableViewSelectionModel<Map<String, String>> tableViewSelectionModel = familyTableView.getSelectionModel();
        ObservableList<Integer> list = tableViewSelectionModel.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        //set table view data
        refreshTableView();
    }

    public void setTableViewData(){
        familyObservableList.clear();
        for (Map<String, String> stringStringMap : familyArrayList) {
            familyObservableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        familyTableView.setItems(familyObservableList);
    }

    public void refreshTableView(){
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("personId", personId);
        DataResponse dataResponse = HttpRequestUtil.request("/api/student/getFamilyMemberList", dataRequest);
        if (dataResponse != null){
            try {
                familyArrayList = (ArrayList<Map<String, String>>) dataResponse.getData();
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
        setVBoxPanelVisible(true);
        setTextFieldEditable(false);
        saveOrAddButton.setVisible(false);
        saveOrAddButton.setManaged(false);
        editButton.setManaged(true);
        editButton.setVisible(true);
        Map map = familyTableView.getSelectionModel().getSelectedItem();
        memberId = CommonMethod.getString(map, "memberId");
        changeVBoxInfo();
    }

    public void changeVBoxInfo(){
        Map<String, String> map = familyTableView.getSelectionModel().getSelectedItem();
        String name = CommonMethod.getString(map, "name");
        String unit = CommonMethod.getString(map, "unit");
        String age = CommonMethod.getString(map, "age");
        String relation = CommonMethod.getString(map, "relation");
        nameTextField.setText(name);
        unitTextField.setText(unit);
        ageTextField.setText(age);
        genderCombo.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(map, "gender")));
        relationTextField.setText(relation);
    }

    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
        isNew = true;
        clearPanel();
        setTextFieldEditable(true);
        setVBoxPanelVisible(true);
        editButton.setVisible(false);
        editButton.setManaged(false);
        saveOrAddButton.setManaged(true);
        saveOrAddButton.setVisible(true);
        saveOrAddButton.setText("添加");
    }

    public void clearPanel(){
        nameTextField.setText("");
        unitTextField.setText("");
        ageTextField.setText("");
        genderCombo.getSelectionModel().select(-1);
        relationTextField.setText("");
    }

    public void onDeleteButtonClick(ActionEvent actionEvent) {
        if (memberId == null || memberId.isEmpty()) {
            MessageDialog.showDialog("请选中要删除的家庭成员");
        } else {
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("memberId", memberId);
            dataRequest.add("personId", personId);
            DataResponse dataResponse = HttpRequestUtil.request("/api/familyMember/deleteFamilyMember", dataRequest);
            if (dataResponse != null){
                if (dataResponse.getCode() == 0){
                    MessageDialog.showDialog("删除成功");
                    memberId = null;
                    refreshTableView();
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            } else {
                System.err.println("error in delete family member");
            }
        }
    }

    public void onImportButtonClick(ActionEvent actionEvent) {

    }

    public void onSaveOrAddButtonClick(){
        String name = nameTextField.getText();
        String age = ageTextField.getText();
        String unit = unitTextField.getText();
        String relation = relationTextField.getText();
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("age", age);
        map.put("unit", unit);
        map.put("relation", relation);
        if (genderCombo.getSelectionModel() != null && genderCombo.getSelectionModel().getSelectedItem() != null) {
            map.put("gender", genderCombo.getSelectionModel().getSelectedItem().getValue());
        }
        map.put("personId", personId);
        if (isNew) {
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("map", map);
            DataResponse dataResponse = HttpRequestUtil.request("/api/familyMember/addFamilyMember", dataRequest);
            if (dataResponse != null){
                MessageDialog.showDialog("Success!");
                vBoxPanel.setVisible(false);
                vBoxPanel.setManaged(false);
                clearPanel();
                setVBoxPanelVisible(false);
                setTextFieldEditable(false);
                refreshTableView();
                isNew = false;
            } else {
                MessageDialog.showDialog("Failed");
            }
        } else {
            Map<String, String> oldMap = familyTableView.getSelectionModel().getSelectedItem();
            map.put("memberId", CommonMethod.getString(oldMap, "memberId"));
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("map", map);
            DataResponse dataResponse = HttpRequestUtil.request("/api/familyMember/editFamilyMemberInfo",dataRequest);
            if (dataResponse != null){
                if (dataResponse.getCode() == 0) {
                    MessageDialog.showDialog("修改成功");
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            } else {
                System.out.println("error in edit family member info");
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
        editButton.setManaged(false);
        editButton.setVisible(false);
    }

    public void setVBoxPanelVisible(boolean is){
        vBoxPanel.setVisible(is);
        vBoxPanel.setManaged(is);
    }

    public void setTextFieldEditable(boolean is){
        nameTextField.setEditable(is);
        ageTextField.setEditable(is);
        genderCombo.setDisable(!is);
        relationTextField.setEditable(is);
        unitTextField.setEditable(is);
    }
}
