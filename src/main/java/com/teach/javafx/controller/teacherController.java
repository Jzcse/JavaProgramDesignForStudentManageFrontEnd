package com.teach.javafx.controller;

import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class teacherController extends ToolController {
    //IMPORT FXML ID
    @FXML
    private TableView<Map<String, String>> dataTableView;           //教师信息表
    @FXML
    private TableColumn<Map<String, String>, String> numColumn;     //教师学工号
    @FXML
    private  TableColumn<Map<String, String>, String> nameColumn;   //教师姓名
    @FXML
    private  TableColumn<Map<String, String>, String> deptColumn;   //教师学院
    @FXML
    private  TableColumn<Map<String, String>, String> majorColumn;  //教师专业
    @FXML
    private  TableColumn<Map<String, String>, String> titleColumn;  //教师职称
    @FXML
    private  TableColumn<Map<String, String>, String> cardColumn;   //教师证件号
    @FXML
    private  TableColumn<Map<String, String>, String> genderColumn; //教师性别
    @FXML
    private  TableColumn<Map<String, String>, String> birthdayColumn;//教师生日
    @FXML
    private  TableColumn<Map<String, String>, String> emailColumn;  //教师电子邮件
    @FXML
    private  TableColumn<Map<String, String>, String> phoneColumn;  //教师电话
    @FXML
    private  TableColumn<Map<String, String>, String> addressColumn;//教师地址

    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField deptField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField cardField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox<OptionItem> genderComboBox;
    @FXML
    private DatePicker birthdayPick;
    @FXML
    private TextField numNameTextField;


    //service
    private boolean isNew = false;
    private Integer personId;
    private ArrayList<Map<String, String>> teacherList = new ArrayList<>();
    private ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList();
    private List<OptionItem> genderList;

    public void setTableViewData(){
        observableList.clear();
        for (Map<String, String> stringStringMap : teacherList) {
            observableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("page", 0);
        dataRequest.add("size", 50);
        DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/getTeacherList", dataRequest);
        if (dataResponse != null && dataResponse.getCode() == 0){
            teacherList = (ArrayList<Map<String, String>>) dataResponse.getData();
        }
        numColumn.setCellValueFactory(new MapValueFactory("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        deptColumn.setCellValueFactory(new MapValueFactory("dept"));
        majorColumn.setCellValueFactory(new MapValueFactory("major"));
        titleColumn.setCellValueFactory(new MapValueFactory("title"));
        cardColumn.setCellValueFactory(new MapValueFactory("card"));
        genderColumn.setCellValueFactory(new MapValueFactory("genderName"));
        birthdayColumn.setCellValueFactory(new MapValueFactory("birthday"));
        emailColumn.setCellValueFactory(new MapValueFactory("email"));
        phoneColumn.setCellValueFactory(new MapValueFactory("phone"));
        addressColumn.setCellValueFactory(new MapValueFactory("address"));
        //copy teacher's code
        //选中状态处理
        TableView.TableViewSelectionModel<Map<String, String>> tableViewSelectionModel = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tableViewSelectionModel.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        //处理性别下拉框
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");
        genderComboBox.getItems().addAll(genderList);
        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        isNew = false;
        changeTeacherInfo();
    }

    public void changeTeacherInfo(){
        Map<String, String> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/teacher/getTeacherInfo", dataRequest);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        deptField.setText(CommonMethod.getString(form, "dept"));
        majorField.setText(CommonMethod.getString(form, "major"));
        titleField.setText(CommonMethod.getString(form, "title"));
        cardField.setText(CommonMethod.getString(form, "card"));
        genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
        birthdayPick.getEditor().setText(CommonMethod.getString(form, "birthday"));
        emailField.setText(CommonMethod.getString(form, "email"));
        phoneField.setText(CommonMethod.getString(form, "phone"));
        addressField.setText(CommonMethod.getString(form, "address"));
//        displayPhoto();
    }

    public void onAddButtonClick(ActionEvent actionEvent) {
        this.isNew = true;
        clearPanel();
    }

    //  清空输入信息
    public void clearPanel(){
        personId = null;
        numField.setText("");
        nameField.setText("");
        deptField.setText("");
        majorField.setText("");
        titleField.setText("");
        cardField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        birthdayPick.getEditor().setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }

    public void onDeleteButtonClick(ActionEvent actionEvent) {
        Map<String, String> from = dataTableView.getSelectionModel().getSelectedItem();
        if (from == null) {
            MessageDialog.showDialog("请选定要删除的教师");
        } else {
            int choice = MessageDialog.choiceDialog("确定要删除吗？");
            if (choice == MessageDialog.CHOICE_YES){
                personId = CommonMethod.getInteger(from, "personId");
                DataRequest dataRequest = new DataRequest();
                dataRequest.add("personId", personId);
                DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/deleteTeacher", dataRequest);
                if (dataResponse != null){
                    if (dataResponse.getCode() == 0){
                        MessageDialog.showDialog("删除成功！");
                        //刷新表格
                        DataRequest dataRequest1 = new DataRequest();
                        dataRequest1.add("page", 0);
                        dataRequest1.add("size", 50);
                        DataResponse dataResponse1 = HttpRequestUtil.request("/api/teacher/getTeacherList", dataRequest1);
                        if (dataResponse1 != null && dataResponse1.getCode() == 0){
                            teacherList = (ArrayList<Map<String, String>>) dataResponse1.getData();
                            setTableViewData();
                        }
                    } else {
                        MessageDialog.showDialog(dataResponse.getMsg());
                    }
                }
            }
        }
    }

    public void onImportButtonClick(ActionEvent actionEvent) {
    }

    public void onQueryButtonClick(ActionEvent actionEvent) {
        String input = numNameTextField.getText();
        if (input.matches(".*\\d.*")){
            //num query
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("num", input);
            DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/searchTeacherByNum", dataRequest);
            if (dataResponse != null) {
                if (dataResponse.getCode() == 0){
                    teacherList = (ArrayList<Map<String, String>>) dataResponse.getData();
                    setTableViewData();
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            }

        } else {
            //name query
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("name", input);
            DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/searchTeacherByName", dataRequest);

            if (dataResponse != null){
                if (dataResponse.getCode() == 0){
                    //刷新表格
                    teacherList = (ArrayList<Map<String, String>>) dataResponse.getData();
                    setTableViewData();
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            }
        }
    }

    public void onPhotoButtonClick(ActionEvent actionEvent) {
    }

    public void onSaveButtonClick(ActionEvent actionEvent) {
        if (isNew){
            if (numField.getText().isEmpty()){
                MessageDialog.showDialog("工号为空，不能添加教师！");
                return;
            }
            if (nameField.getText().isEmpty()){
                MessageDialog.showDialog("姓名不能为空");
                return;
            }
            Map<String, String> personForm = new HashMap<>();
            Map<String, String> teacherForm = new HashMap<>();
            personForm.put("num", numField.getText());
            personForm.put("name", nameField.getText());
            personForm.put("dept", deptField.getText());
            teacherForm.put("major", majorField.getText());
            teacherForm.put("title", titleField.getText());
            personForm.put("card", cardField.getText());
            if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
                personForm.put("gender", genderComboBox.getSelectionModel().getSelectedItem().getValue());
            personForm.put("birthday", birthdayPick.getEditor().getText());
            personForm.put("email", emailField.getText());
            personForm.put("phone", phoneField.getText());
            personForm.put("address", addressField.getText());
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("teacherMap", teacherForm);
            dataRequest.add("personMap", personForm);
            DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/addTeacher", dataRequest);
            if (dataResponse.getCode() == 0){
                MessageDialog.showDialog("添加教师成功");
                //刷新表格
                DataRequest dataRequest1 = new DataRequest();
                dataRequest1.add("page", 0);
                dataRequest1.add("size", 50);
                DataResponse dataResponse1 = HttpRequestUtil.request("/api/teacher/getTeacherList", dataRequest1);
                if (dataResponse1 != null && dataResponse1.getCode() == 0){
                    teacherList = (ArrayList<Map<String, String>>) dataResponse1.getData();
                    setTableViewData();
                }
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }

        } else {
            if (numField.getText().equals("")) {
                MessageDialog.showDialog("修改后工号不能为空");
                return;
            }
            DataRequest dataRequest = new DataRequest();
            //person id request
            dataRequest.add("personId", personId);
            //info form request
            Map<String, String> form = new HashMap<>();
            form.put("num", numField.getText());
            form.put("name", nameField.getText());
            form.put("dept", deptField.getText());
            form.put("major", majorField.getText());
            form.put("title", titleField.getText());
            form.put("card", cardField.getText());
            if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
                form.put("gender", genderComboBox.getSelectionModel().getSelectedItem().getValue());
            form.put("birthday", birthdayPick.getEditor().getText());
            form.put("email", emailField.getText());
            form.put("phone", phoneField.getText());
            form.put("address", addressField.getText());
            dataRequest.add("form", form);
            //response
            DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/editTeacherInfo", dataRequest);
            if (dataResponse != null && dataResponse.getCode() == 0){
                MessageDialog.showDialog("更改成功！");
                //刷新表格
                DataRequest dataRequest1 = new DataRequest();
                dataRequest1.add("page", 0);
                dataRequest1.add("size", 50);
                DataResponse dataResponse1 = HttpRequestUtil.request("/api/teacher/getTeacherList", dataRequest1);
                if (dataResponse1 != null && dataResponse1.getCode() == 0){
                    teacherList = (ArrayList<Map<String, String>>) dataResponse1.getData();
                    setTableViewData();
                }
            }
        }
    }

    public void onFamilyButtonClick(ActionEvent actionEvent) {
    }

    public void onImportFeeButtonClick(ActionEvent actionEvent) {
    }
}
