package com.teach.javafx.controller;

import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
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
    //components of right box for detail info of teacher
    @FXML
    private ImageView photoImage;
    @FXML
    private VBox vBoxPanel;
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
    @FXML
    private Button photoButton;
    //pagination component
    @FXML
    private Pagination pagination;
    //
    @FXML
    private Button saveButton;
    @FXML
    private Button familyButton;
    @FXML
    private Button editButton;
    @FXML
    private Button importFeeButton;



    //service
    private boolean isNew = false;
    private int teacherCount;
    private int pageCount;
    private Integer personId;
    private ArrayList<Map<String, String>> teacherList = new ArrayList<>();
    private ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList();
    private List<OptionItem> genderList;
    private String baseUrl;

    private void setVBoxVisible(boolean isVisible){
        vBoxPanel.setVisible(isVisible);
        vBoxPanel.setManaged(isVisible);
    }

    private void setEditAble(boolean isEditAble){
        numField.setEditable(isEditAble);
        nameField.setEditable(isEditAble);
        deptField.setEditable(isEditAble);
        majorField.setEditable(isEditAble);
        titleField.setEditable(isEditAble);
        cardField.setEditable(isEditAble);
        emailField.setEditable(isEditAble);
        phoneField.setEditable(isEditAble);
        addressField.setEditable(isEditAble);
        genderComboBox.setDisable(!isEditAble);
        birthdayPick.setEditable(isEditAble);
        birthdayPick.setDisable(!isEditAble);
    }

    public void refreshTeacherList(){
        int currentPage = (int) pagination.getCurrentPageIndex();
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("page", currentPage);
        dataRequest.add("size", 33);
        DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/getTeacherList", dataRequest);
        if (dataResponse != null && dataResponse.getCode() == 0){
            teacherList = (ArrayList<Map<String, String>>) dataResponse.getData();
            setTableViewData();
        }
    }

    public void setTableViewData(){
        observableList.clear();
        for (Map<String, String> stringStringMap : teacherList) {
            observableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        dataTableView.setItems(observableList);
    }

    public void refreshPageCount(){
        DataRequest dataRequestForTeacherCount = new DataRequest();
        DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/getTeacherCount", dataRequestForTeacherCount);
        if (dataResponse != null && dataResponse.getCode() == 0){
            Map<String, String> map = (Map<String, String>) dataResponse.getData();
            teacherCount = Integer.parseInt(CommonMethod.getString(map, "count"));
            if (teacherCount % 33 == 0){
                pageCount = teacherCount / 33;
            } else {
                pageCount = teacherCount / 33 + 1;
            }
            pagination.setPageCount(pageCount);
        } else {
            MessageDialog.showDialog("Error! Failed to get teacher count from the server.");
        }
    }

    @FXML
    public void initialize() {
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("page", 0);
        dataRequest.add("size", 33);
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
        genderColumn.setCellValueFactory(new MapValueFactory("gender"));
        genderColumn.setCellValueFactory(cellData -> {
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
        birthdayColumn.setCellValueFactory(new MapValueFactory("birthday"));
        emailColumn.setCellValueFactory(new MapValueFactory("email"));
        phoneColumn.setCellValueFactory(new MapValueFactory("phone"));
        addressColumn.setCellValueFactory(new MapValueFactory("address"));
        //get the sum count of teacher and init the page count
        refreshPageCount();
        //init the pagination
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            //refresh teacher list and set table view
            refreshTeacherList();
        });
        //copy teacher's code
        //选中状态处理
        TableView.TableViewSelectionModel<Map<String, String>> tableViewSelectionModel = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tableViewSelectionModel.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        //
        setTableViewData();
        //处理性别下拉框
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");
        genderComboBox.getItems().addAll(genderList);
        //设置日期格式
        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        //demo

        // get base url
        DataRequest dataRequest1 = new DataRequest();
        DataResponse dataResponse1 = HttpRequestUtil.request("/api/photo/getBaseUrl", dataRequest1);
        if (dataResponse1 != null) {
            baseUrl = (String) dataResponse1.getData();
        } else {
            MessageDialog.showDialog("获取url失败，请重新启动后端!");
        }
        //设置右侧的面板初始为隐藏状态
        setVBoxVisible(false);
        setEditAble(false);
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        isNew = false;
        setVBoxVisible(true);
        setEditAble(false);
        saveButton.setVisible(false);
        saveButton.setManaged(false);
        familyButton.setManaged(true);
        familyButton.setVisible(true);
        importFeeButton.setVisible(true);
        importFeeButton.setManaged(true);
        editButton.setManaged(true);
        editButton.setVisible(true);
        changeTeacherInfo();
        displayPhoto();
    }

    public void displayPhoto() {
        if (personId == null){
            MessageDialog.showDialog("未选择教师");
            return;
        }
        try {
            // 获取当前文件的路径并设置图片文件保存位置
            File saveDir;
            String classPath = System.getProperty("java.class.path");
            if(classPath.endsWith(".jar")){
                // 生产环境
                java.net.URI uri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
                java.io.File jarFile = new java.io.File(uri);
                saveDir = jarFile.getParentFile();
            } else {
                // 开发环境
                saveDir = new File(System.getProperty("user.dir"));
            }
            // 新建保存图片的文件夹
            String newDir = "photos";
            File newDirFile = new File(saveDir, newDir);
            if(!newDirFile.exists()){
                if(newDirFile.mkdirs()){
                    System.out.println("新目录创建成功" + newDirFile.getAbsolutePath());
                } else {
                    MessageDialog.showDialog("无法在当前文件夹创建新目录用以下载并保存用户的图片，请检查权限！");
                }
            }
            // 下载图片并保存到指定文件夹
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("personId", personId);
            DataResponse dataResponse = HttpRequestUtil.request("/api/photo/download", dataRequest);
            if (dataResponse != null){
                if (dataResponse.getCode() == 0){
                    System.out.println("下载成功");
                    try{
                        Map<String, String> dataMap = (Map<String, String>) dataResponse.getData();
                        String data = CommonMethod.getString(dataMap, "file");
                        byte[] fileBytes = java.util.Base64.getDecoder().decode(data);

                        File saveFile = new File(newDirFile, personId + ".jpg");

                        Files.write(saveFile.toPath(), fileBytes);
                        System.out.println("文件保存成功");

                        String url = saveFile.toURI().toString();
                        Image image = new Image(url);
                        if (image.isError()){
                            System.out.println("图片加载失败");
                        } else {
                            System.out.println("图片加载成功");
                            photoImage.setImage(image);
                        }
                    } catch (IOException e) {
                        MessageDialog.showDialog("照片下载失败，请检查：\n1. 文件权限；\n2. 磁盘空间；\n3. 写入文件夹是否正在被其他程序占用。");
                        throw new RuntimeException(e);
                    } catch (IllegalArgumentException e){
                        System.out.println("无效的本地路径");
                        Image image = new Image("https://ts4.tc.mm.bing.net/th/id/OIP-C.rA8p_zRRqpN4GlcYol5p4AAAAA?rs=1&pid=ImgDetMain");
                        photoImage.setImage(image);
                    }
                } else {
                    Image image = new Image("https://ts4.tc.mm.bing.net/th/id/OIP-C.rA8p_zRRqpN4GlcYol5p4AAAAA?rs=1&pid=ImgDetMain");
                    photoImage.setImage(image);
                    System.out.println("下载失败");
                    System.out.println(dataResponse.getMsg());
                }
            } else {
                MessageDialog.showDialog("获取照片失败，请重新启动后端!");
            }
        } catch (URISyntaxException e) {
            MessageDialog.showDialog("无法解析当前文件路径！");
            throw new RuntimeException(e);
        }

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
    }

    public void onAddButtonClick(ActionEvent actionEvent) {
        this.isNew = true;
        setEditAble(true);
        setVBoxVisible(true);
        saveButton.setVisible(true);
        saveButton.setManaged(true);
        saveButton.setText("确定添加");
        familyButton.setManaged(false);
        familyButton.setVisible(false);
        importFeeButton.setVisible(false);
        importFeeButton.setManaged(false);
        editButton.setManaged(false);
        editButton.setVisible(false);
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
                        //刷新教师数量及页码数
                        refreshPageCount();
                        //刷新表格
                        refreshTeacherList();
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
        DataRequest dataRequest = new DataRequest();
        if (input.matches(".*\\d.*")){
            //num query
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

    public void onPhotoButtonClick(ActionEvent actionEvent) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择教师图片");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(null);
        if(file != null) {
            DataResponse dataResponse = HttpRequestUtil.newUploadFile("/api/photo/upload", file, "file", personId.toString());
            if (dataResponse.getCode() == 0){
                MessageDialog.showDialog("上传成功");
                displayPhoto();
            }
        } else {
            System.out.println("Error to choose jpg files");
            MessageDialog.showDialog("Error to choose jpg files");
        }
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
            String birthdayTemp = birthdayPick.getEditor().getText();
            if (!birthdayTemp.matches("^(?:(?:19|20)\\d{2})-(?:(?:0[1-9]|1[0-2]))-(?:(?:0[1-9]|[12]\\d|3[01]))$")){
                MessageDialog.showDialog("日期不符合规范,请检查。");
                return;
            } else {
                personForm.put("birthday", birthdayPick.getEditor().getText());
            }
            personForm.put("email", emailField.getText());
            personForm.put("phone", phoneField.getText());
            personForm.put("address", addressField.getText());
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("teacherMap", teacherForm);
            dataRequest.add("personMap", personForm);
            DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/addTeacher", dataRequest);
            if (dataResponse.getCode() == 0){
                MessageDialog.showDialog("添加教师成功");
                //刷新教师数与页数
                refreshPageCount();
                //设置页码值
                pagination.setCurrentPageIndex(pageCount - 1);
                //刷新教师列表并刷新界面
                refreshTeacherList();
                //clear
                clearPanel();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            if (numField.getText().isEmpty()) {
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
            String birthdayTemp = birthdayPick.getEditor().getText();
            if (!birthdayTemp.matches("^(?:(?:19|20)\\d{2})-(?:(?:0[1-9]|1[0-2]))-(?:(?:0[1-9]|[12]\\d|3[01]))$")){
                MessageDialog.showDialog("日期不符合规范,请检查。");
                return;
            } else {
                form.put("birthday", birthdayPick.getEditor().getText());
            }
            form.put("email", emailField.getText());
            form.put("phone", phoneField.getText());
            form.put("address", addressField.getText());
            dataRequest.add("form", form);
            //response
            DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/editTeacherInfo", dataRequest);
            if (dataResponse != null && dataResponse.getCode() == 0){
                MessageDialog.showDialog("更改成功！");
                //刷新表格
                refreshTeacherList();
            }
        }
    }

    public void onFamilyButtonClick(ActionEvent actionEvent) {
    }

    public void onImportFeeButtonClick(ActionEvent actionEvent) {
    }

    public void onEditButtonClick(ActionEvent actionEvent) {
        setEditAble(true);
        setVBoxVisible(true);
        saveButton.setVisible(true);
        saveButton.setManaged(true);
        saveButton.setText("保存");
        familyButton.setManaged(false);
        familyButton.setVisible(false);
        importFeeButton.setVisible(false);
        importFeeButton.setManaged(false);
        editButton.setManaged(false);
        editButton.setVisible(false);
    }
}
