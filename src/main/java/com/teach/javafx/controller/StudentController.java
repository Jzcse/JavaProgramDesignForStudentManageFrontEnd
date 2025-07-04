package com.teach.javafx.controller;

//import com.itextpdf.kernel.colors.Color;
//import com.itextpdf.kernel.colors.DeviceRgb;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.borders.Border;
//import com.itextpdf.layout.borders.SolidBorder;
//import com.itextpdf.layout.element.*;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.properties.*;
import java.awt.*;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.models.Award;
import com.teach.javafx.models.AwardPerson;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.geom.PageSize;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.properties.TextAlignment;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StudentController 登录交互控制类 对应 student_panel.fxml  对应于学生管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *
 * @FXML 属性 对应fxml文件中的
 * @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class StudentController extends ToolController {
    private ImageView photoImageView;
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map, String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map, String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map, String> deptColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map, String> majorColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map, String> classNameColumn; //学生信息表 班级列
    @FXML
    private TableColumn<Map, String> cardColumn; //学生信息表 证件号码列
    @FXML
    private TableColumn<Map, String> genderColumn; //学生信息表 性别列
    @FXML
    private TableColumn<Map, String> birthdayColumn; //学生信息表 出生日期列
    @FXML
    private TableColumn<Map, String> emailColumn; //学生信息表 邮箱列
    @FXML
    private TableColumn<Map, String> phoneColumn; //学生信息表 电话列
    @FXML
    private TableColumn<Map, String> addressColumn;//学生信息表 地址列
    @FXML
    private Button photoButton;  //照片显示和上传按钮

    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField deptField; //学生信息  院系输入域
    @FXML
    private TextField majorField; //学生信息  专业输入域
    @FXML
    private TextField classNameField; //学生信息  班级输入域
    @FXML
    private TextField cardField; //学生信息  证件号码输入域
    @FXML
    private ComboBox<OptionItem> genderComboBox;  //学生信息  性别输入域
    @FXML
    private DatePicker birthdayPick;  //学生信息  出生日期选择域
    @FXML
    private TextField emailField;  //学生信息  邮箱输入域
    @FXML
    private TextField phoneField;   //学生信息  电话输入域
    @FXML
    private TextField addressField;  //学生信息  地址输入域
    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域
    @FXML
    private VBox VBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button familyButton;
    @FXML
    private Button editButton;

    private boolean isNew = false;
    private Integer personId = null;  //当前编辑修改的学生的主键
    private ArrayList<Map> studentList = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> genderList;   //性别选择列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表

    private void setVBoxVisible(boolean isVisible){
        VBox.setVisible(isVisible);
        VBox.setManaged(isVisible);
    }

    private void setEditable(boolean isEditable){
        numField.setEditable(isEditable);
        nameField.setEditable(isEditable);
        deptField.setEditable(isEditable);
        majorField.setEditable(isEditable);
        classNameField.setEditable(isEditable);
        cardField.setEditable(isEditable);
        emailField.setEditable(isEditable);
        phoneField.setEditable(isEditable);
        addressField.setEditable(isEditable);
        genderComboBox.setDisable(!isEditable);
        birthdayPick.setEditable(isEditable);
        birthdayPick.setDisable(!isEditable);
    }

    private void refreshStudentList(){
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("numName", "");
        DataResponse dataResponse = HttpRequestUtil.request("/api/student/getStudentList", dataRequest);
        if (dataResponse != null){
            if (dataResponse.getCode() == 0){
                studentList = (ArrayList<Map>) dataResponse.getData();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            System.out.println("failed to get student list from the back end");
        }
        setTableViewData();
    }

    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (Map map : studentList) {
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        //初始化图片窗口
        photoImageView = new ImageView();
        photoImageView.setFitHeight(100);
        photoImageView.setFitWidth(100);
        photoButton.setGraphic(photoImageView);
        //初始化学生信息集合
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("numName", "");
        res = HttpRequestUtil.request("/api/student/getStudentList", req);
        if (res != null && res.getCode() == 0) {
            studentList = (ArrayList<Map>) res.getData();
        }
        //设置列值工程属性
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        deptColumn.setCellValueFactory(new MapValueFactory<>("dept"));
        majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        cardColumn.setCellValueFactory(new MapValueFactory<>("card"));
        genderColumn.setCellValueFactory(new MapValueFactory<>("genderName"));
        birthdayColumn.setCellValueFactory(new MapValueFactory<>("birthday"));
        emailColumn.setCellValueFactory(new MapValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new MapValueFactory<>("address"));
        //初始化行选中状态处理
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        //初始化表格内容
        setTableViewData();
        //初始化性别下拉框与生日选择框
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");
        genderComboBox.getItems().addAll(genderList);
        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        //初始化右侧栏与文字域可编辑状态
        setEditable(false);
        setVBoxVisible(false);
    }

    /**
     * 清除学生表单中输入信息
     */
    public void clearPanel() {
        personId = null;
        numField.setText("");
        nameField.setText("");
        deptField.setText("");
        majorField.setText("");
        classNameField.setText("");
        cardField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        birthdayPick.getEditor().setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }

    /**
     * 当选中某一行后更新右侧信息
     */
    protected void changeStudentInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("personId", personId);
        DataResponse dataResponse = HttpRequestUtil.request("/api/student/getStudentInfo", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() != 0) {
                MessageDialog.showDialog(dataResponse.getMsg());
            } else {
                form = (Map) dataResponse.getData();
                numField.setText(CommonMethod.getString(form, "num"));
                nameField.setText(CommonMethod.getString(form, "name"));
                deptField.setText(CommonMethod.getString(form, "dept"));
                majorField.setText(CommonMethod.getString(form, "major"));
                classNameField.setText(CommonMethod.getString(form, "className"));
                cardField.setText(CommonMethod.getString(form, "card"));
                genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
                birthdayPick.getEditor().setText(CommonMethod.getString(form, "birthday"));
                emailField.setText(CommonMethod.getString(form, "email"));
                phoneField.setText(CommonMethod.getString(form, "phone"));
                addressField.setText(CommonMethod.getString(form, "address"));
                displayPhoto();
            }
        } else {
            MessageDialog.showDialog("获取详细信息出错");
        }
    }

    /**
     * 点击学生列表的某一行，根据personId ,从后台查询学生的基本信息，切换学生的编辑信息
     */
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        isNew = false;
        setVBoxVisible(true);
        setEditable(false);
        saveButton.setManaged(false);
        saveButton.setVisible(false);
        editButton.setManaged(true);
        editButton.setVisible(true);
        familyButton.setManaged(true);
        familyButton.setVisible(true);
        changeStudentInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);
        if (res != null && res.getCode() == 0) {
            studentList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }


    /**
     * 添加新学生， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
        setVBoxVisible(true);
        setEditable(true);
        saveButton.setManaged(true);
        saveButton.setVisible(true);
        saveButton.setText("确定添加");
        editButton.setVisible(false);
        editButton.setManaged(false);
        familyButton.setManaged(false);
        familyButton.setVisible(false);
        isNew = true;
    }

    /**
     * 点击删除按钮 删除当前编辑的学生的数据
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/student/studentDelete", req);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    /**
     * 点击保存或添加按钮，判断是新增还是修改，提交当前的学生信息
     */
    @FXML
    protected void onSaveButtonClick(){
        String num = numField.getText();
        String name = nameField.getText();
        String dept = deptField.getText();
        String major = majorField.getText();
        String className = classNameField.getText();
        String card = cardField.getText();
        String birthday = birthdayPick.getEditor().getText();
        String email = emailField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        String gender;
        if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null){
            gender = genderComboBox.getSelectionModel().getSelectedItem().getValue();
        } else {
            gender = null;
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("num", num);
        map.put("dept", dept);
        map.put("major", major);
        map.put("className", className);
        map.put("card", card);
        map.put("birthday", birthday);
        map.put("email", email);
        map.put("address", address);
        map.put("phone", phone);
        map.put("gender", gender);
        if (isNew) {
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("map", map);
            DataResponse dataResponse = HttpRequestUtil.request("/api/student/addStudent", dataRequest);
            if (dataResponse != null){
                if (dataResponse.getCode() == 0) {
                    MessageDialog.showDialog("添加成功");
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            } else {
                System.out.println("on save button click for add part Http errors");
            }
        } else {
            map.put("personId", personId.toString());
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("map", map);
            DataResponse dataResponse = HttpRequestUtil.request("/api/student/editStudentInfo", dataRequest);
            if (dataResponse != null) {
                if (dataResponse.getCode() == 0){
                    MessageDialog.showDialog("修改成功");
                } else {
                    MessageDialog.showDialog(dataResponse.getMsg());
                }
            } else {
                System.out.println("on save button click for edit part Http errors");
            }
        }
        refreshStudentList();
        setTableViewData();
    }

    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */
    @FXML
    protected void onSaveButtonClick1() {
        if (numField.getText().equals("")) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map form = new HashMap();
        form.put("num", numField.getText());
        form.put("name", nameField.getText());
        form.put("dept", deptField.getText());
        form.put("major", majorField.getText());
        form.put("className", classNameField.getText());
        form.put("card", cardField.getText());
        if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("gender", genderComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("birthday", birthdayPick.getEditor().getText());
        form.put("email", emailField.getText());
        form.put("phone", phoneField.getText());
        form.put("address", addressField.getText());
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/student/studentEditSave", req);
        if (res.getCode() == 0) {
            personId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onEditButtonClick(){
        setEditable(true);
        editButton.setVisible(false);
        editButton.setManaged(false);
        saveButton.setVisible(true);
        saveButton.setManaged(true);
        familyButton.setManaged(false);
        familyButton.setVisible(false);
        saveButton.setText("保存");
    }

    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对学生的增，删，改操作
     */
    public void doNew() {
        clearPanel();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }

    /**
     * 导出学生信息表的示例 重写ToolController 中的doExport 这里给出了一个导出学生基本信息到Excl表的示例， 后台生成Excl文件数据，传回前台，前台将文件保存到本地
     */
    public void doExport() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/student/getStudentListExcl", req);
        if (bytes != null) {
            try {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("前选择保存的文件");
                fileDialog.setInitialDirectory(new File("C:/"));
                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
                File file = fileDialog.showSaveDialog(null);
                if (file != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    protected void onImportButtonClick() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("请选择学生数据表");
        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        String paras = "";
        DataResponse res = HttpRequestUtil.importData("/api/student/importStudentData", file.getPath(), paras);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    protected void onFamilyButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/familyMember-panel.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setWidth(600);
        stage.setHeight(400);
        stage.setResizable(false);
        stage.setTitle("familyMember");
        Scene scene = new Scene(root, 500, 400);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        //
        FamilyMemberController familyMemberController = loader.getController();
        familyMemberController.initialize(personId.toString());
        stage.showAndWait();
    }

    @FXML
    protected void onFamilyButtonClick1() {
        //define some lambda function
        //add family member scene
        Runnable addFamilyStage = () -> {
            Scene scene = null;
            Stage stage = new Stage();
            BorderPane borderPane = new BorderPane();
            scene = new Scene(borderPane,400, 300);
            stage.showAndWait();
        };

        //get family member by person ID
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/student/getFamilyMemberList", dataRequest);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        //return the list of map which contains the info of member
        List<Map<String, String>> list = (List<Map<String, String>>) res.getData();
        ObservableList<Map<String, String>> familyList = FXCollections.observableArrayList(list); //a new list class supported by JavaFx
        //initialize the root scene
        Scene scene = null, pScene = null;
        Stage stage = new Stage();
        //initialize the table
        TableView<Map<String, String>> table = new TableView<>(familyList);
        table.setEditable(true);
        //initialize the column of the table
        //column of relation
        TableColumn<Map<String, String>, String> relationColumn = new TableColumn<>("关系");
        relationColumn.setCellValueFactory(new MapValueFactory("relation"));
        relationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        //set editable to relation column
//        relationColumn.setOnEditCommit(event -> {
//            TableView tempTable = event.getTableView();
//            Map tempEntity = (Map) tempTable.getItems().get(event.getTablePosition().getRow());
//            tempEntity.put("relation",event.getNewValue());
//        });
        table.getColumns().add(relationColumn);
        //column of name
        TableColumn<Map<String, String>, String> nameColumn = new TableColumn<>("姓名");
        nameColumn.setCellValueFactory(new MapValueFactory("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        table.getColumns().add(nameColumn);
        //column of gender
        TableColumn<Map<String, String>, String> genderColumn = new TableColumn<>("性别");
        genderColumn.setCellValueFactory(new MapValueFactory("gender"));
        genderColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        table.getColumns().add(genderColumn);
        //column of age
        TableColumn<Map<String, String>, String> ageColumn = new TableColumn<>("年龄");
        ageColumn.setCellValueFactory(new MapValueFactory("age"));
        ageColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        table.getColumns().add(ageColumn);
        //column of unit
        TableColumn<Map<String, String>, String> unitColumn = new TableColumn<>("单位");
        unitColumn.setCellValueFactory(new MapValueFactory("unit"));
        unitColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        table.getColumns().add(unitColumn);
        //initialize the content of the table (set table view data)
        //...
        //initialize root
        BorderPane root = new BorderPane();
        FlowPane flowPane = new FlowPane();
        VBox vBox = new VBox();

        Button confirmButton = new Button("确定");
        confirmButton.setOnAction(event -> {
            for(Map map: table.getItems()) {
                System.out.println("map:"+map);
            }
            stage.close();
        });
        Button addButton = new Button("添加家庭成员");
        addButton.setOnAction(event -> {
            addFamilyStage.run();
        });
        //Hierarchy part
        flowPane.getChildren().add(confirmButton);
        flowPane.getChildren().add(addButton);
        root.setCenter(table);
        root.setBottom(flowPane);
        scene = new Scene(root, 400, 300);
        stage.initOwner(MainApplication.getMainStage());
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setTitle("家庭成员信息！");
        stage.setOnCloseRequest(event -> {
            MainApplication.setCanClose(true);
        });
        stage.showAndWait();
    }

    public void displayPhoto(){
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
                            photoImageView.setImage(image);
                        }
                    } catch (IOException e) {
                        MessageDialog.showDialog("照片下载失败，请检查：\n1. 文件权限；\n2. 磁盘空间；\n3. 写入文件夹是否正在被其他程序占用。");
                        throw new RuntimeException(e);
                    } catch (IllegalArgumentException e){
                        System.out.println("无效的本地路径");
                        Image image = new Image("https://ts4.tc.mm.bing.net/th/id/OIP-C.rA8p_zRRqpN4GlcYol5p4AAAAA?rs=1&pid=ImgDetMain");
                        photoImageView.setImage(image);
                    }
                } else {
                    Image image = new Image("https://ts4.tc.mm.bing.net/th/id/OIP-C.rA8p_zRRqpN4GlcYol5p4AAAAA?rs=1&pid=ImgDetMain");
                    photoImageView.setImage(image);
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

    @FXML
    public void onPhotoButtonClick() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("请选择学生图片");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(null);
        if(file != null) {
            DataResponse dataResponse = HttpRequestUtil.newUploadFile("/api/photo/upload", file, "file", personId.toString());
            if (dataResponse!= null){
                if (dataResponse.getCode() == 0){
                    MessageDialog.showDialog("上传成功！");
                    displayPhoto();
                }
            }
        } else {
            System.out.println("Error to choose jpg files");
            MessageDialog.showDialog("Error to choose jpg files");
        }
    }
    @FXML
    public void onImportFeeButtonClick(){
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("前选择消费数据表");
        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        String paras = "personId="+personId;
        DataResponse res =HttpRequestUtil.importData("/api/student/importFeeData",file.getPath(),paras);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }




    public static void generateStudentPdf(List<Student> students, List<Award> awards, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (FileOutputStream fos = new FileOutputStream(file);
             Document document = new Document(PageSize.A4)) {

            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();
            document.setMargins(25, 25, 25, 25);

            Color secondaryColor = new Color(26, 35, 126);
            Color textColor = new Color(33, 33, 33);
            Color borderColor = new Color(200, 200, 200);

            try {
                BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
                Font regularFont = new Font(baseFont, 10, Font.NORMAL, textColor);
                Font boldFont = new Font(baseFont, 14, Font.BOLD, secondaryColor);
                Font headerFont = new Font(baseFont, 20, Font.BOLD, secondaryColor); // 头部姓名专用字体，放大为20pt

                for (Student student : students) {
                    // 创建简历容器表格
                    PdfPTable containerTable = new PdfPTable(1);
                    containerTable.setWidthPercentage(100);
                    containerTable.setSpacingAfter(20f);
                    containerTable.getDefaultCell().setBorder(Rectangle.BOX);
                    containerTable.getDefaultCell().setBorderColor(borderColor);
                    containerTable.getDefaultCell().setPadding(20);

                    // 头部表格（左侧照片，右侧个人信息）
                    PdfPTable headerTable = new PdfPTable(new float[]{1, 3}); // 照片:信息 = 1:3 比例
                    headerTable.setWidthPercentage(100);
                    headerTable.setSpacingAfter(15f);

                    // 左侧照片占位框
                    PdfPCell photoCell = new PdfPCell();
                    photoCell.setBorder(Rectangle.NO_BORDER);
                    photoCell.setPadding(5);
                    photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                    PdfPTable photoInnerTable = new PdfPTable(1);
                    photoInnerTable.setTotalWidth(80);
                    photoInnerTable.setLockedWidth(true);
                    PdfPCell photoInnerCell = new PdfPCell();
                    photoInnerCell.setFixedHeight(90);
                    photoInnerCell.setBorder(Rectangle.BOX);
                    photoInnerCell.setBorderColor(borderColor);
                    photoInnerTable.addCell(photoInnerCell);

                    photoCell.addElement(photoInnerTable);
                    headerTable.addCell(photoCell);

                    // 右侧个人信息
                    PdfPCell infoCell = new PdfPCell();
                    infoCell.setBorder(Rectangle.NO_BORDER);
                    infoCell.setPaddingTop(10);  // 顶部留出适当空间
                    infoCell.setPaddingLeft(15); // 左侧留出适当空间
                    infoCell.setPaddingRight(5); // 右侧减少空间
                    infoCell.setPaddingBottom(5); // 底部减少空间
                    infoCell.setVerticalAlignment(Element.ALIGN_TOP);

                    // 学生姓名（放大字号，突出显示）
                    Paragraph namePara = new Paragraph(student.getName(), headerFont);
                    namePara.setAlignment(Element.ALIGN_LEFT);
                    namePara.setSpacingAfter(3f); // 减少姓名下方间距

                    // 其他个人信息：使用更紧凑的布局
                    StringBuilder infoBuilder = new StringBuilder();

                    // 添加关键信息，使用竖线分隔（更紧凑）
                    infoBuilder.append("学号：").append(student.getNum()).append("    ")
                            .append("性别：").append(student.getGenderName()).append("\n");
                    infoBuilder.append("专业：").append(student.getMajor()).append("    ")
                            .append("班级：").append(student.getClassName()).append("\n");
                    infoBuilder.append("生日：").append(student.getBirthday()).append("    ")
                            .append("学生类型：").append(student.getType() != null ? student.getType() : "暂无信息");

                    Paragraph infoPara = new Paragraph(infoBuilder.toString(), regularFont);
                    infoPara.setLeading(14f); // 设置行间距（数值越小越紧凑）
                    infoPara.setAlignment(Element.ALIGN_LEFT);

                    infoCell.addElement(namePara);
                    infoCell.addElement(infoPara);
                    headerTable.addCell(infoCell);

                    containerTable.addCell(headerTable);

                    // 个人资料部分（保留原内容，但移除已在头部显示的信息）
                    containerTable.addCell(createSectionTitle("个人资料", boldFont, secondaryColor));
                    PdfPTable personalInfoTable = createInfoTable();

                    addInfoRow(personalInfoTable, "身份证号", student.getCard(), regularFont);
                    // 其他个人资料信息...
                    containerTable.addCell(personalInfoTable);

                    // 联系方式
                    containerTable.addCell(createSectionTitle("联系方式", boldFont, secondaryColor));
                    PdfPTable contactTable = createInfoTable();
                    addInfoRow(contactTable, "电话", student.getPhone(), regularFont);
                    addInfoRow(contactTable, "邮箱", student.getEmail(), regularFont);
                    addInfoRow(contactTable, "地址", student.getAddress(), regularFont);
                    containerTable.addCell(contactTable);

                    // 教育背景
                    containerTable.addCell(createSectionTitle("教育背景", boldFont, secondaryColor));
                    PdfPTable educationTable = createInfoTable();
                    addInfoRow(educationTable, "学院/部门", student.getDept(), regularFont);
                    containerTable.addCell(educationTable);

                    // 获奖情况
                    containerTable.addCell(createSectionTitle("获奖情况", boldFont, secondaryColor));
                    if (awards != null && !awards.isEmpty()) {
                        PdfPTable awardsTable = new PdfPTable(3);
                        awardsTable.setWidthPercentage(100);
                        awardsTable.setSpacingBefore(5f);

                        awardsTable.addCell(createTableHeaderCell("奖项名称", boldFont));
                        awardsTable.addCell(createTableHeaderCell("奖项等级", boldFont));
                        awardsTable.addCell(createTableHeaderCell("获奖时间", boldFont));

                        for (Award award : awards) {
                            awardsTable.addCell(createTableCell(
                                    award.getAwardName() != null ? award.getAwardName() : "未填写",
                                    regularFont
                            ));
                            awardsTable.addCell(createTableCell(
                                    award.getAwardLevel() != null ? award.getAwardLevel() : "未填写",
                                    regularFont
                            ));
                            awardsTable.addCell(createTableCell(
                                    award.getAwardTime() != null ? award.getAwardTime() : "未填写",
                                    regularFont
                            ));
                        }
                        containerTable.addCell(awardsTable);
                    } else {
                        PdfPCell noAwardCell = new PdfPCell(new Phrase("暂无获奖信息", regularFont));
                        noAwardCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        noAwardCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        noAwardCell.setFixedHeight(30);
                        noAwardCell.setBorder(Rectangle.NO_BORDER);
                        containerTable.addCell(noAwardCell);
                    }

                    // 个人简介
                    containerTable.addCell(createSectionTitle("个人简介", boldFont, secondaryColor));
                    String introduction = student.getIntroduce() != null ? student.getIntroduce() : "暂无简介";
                    Paragraph introPara = new Paragraph(introduction, regularFont);
                    introPara.setAlignment(Element.ALIGN_JUSTIFIED);
                    introPara.setLeading(16.5f); // 1.5 * 11
                    containerTable.addCell(introPara);

                    document.add(containerTable);

                    // 添加分页
                    if (students.indexOf(student) < students.size() - 1) {
                        document.newPage();
                    }
                }

                // 页脚
                Paragraph footer = new Paragraph(
                        "生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        new Font(baseFont, 8, Font.NORMAL, new Color(150, 150, 150))
                );
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);

            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    // 辅助方法 - 创建信息行（修改为两列布局）
    private static void addInfoRow(PdfPTable table, String label, String value, Font font) {
        if (value == null || value.isEmpty()) {
            value = "暂无信息";
        }

        Font labelFont = new Font(font.getBaseFont(), font.getSize(), Font.NORMAL, new Color(97, 97, 97));
        Font valueFont = new Font(font.getBaseFont(), font.getSize(), Font.NORMAL, font.getColor());

        PdfPCell labelCell = new PdfPCell(new Phrase(label + ":", labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private static PdfPCell createSectionTitle(String title, Font font, Color color) {
        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(224, 224, 224));
        cell.setBorderWidth(0.7f);
        cell.setPaddingBottom(5);
        cell.setPaddingTop(5);
        return cell;
    }

    private static PdfPTable createInfoTable() {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        return table;
    }


    private static PdfPCell createTableHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(font.getBaseFont(), font.getSize(), Font.BOLD)));
        cell.setBackgroundColor(new Color(240, 240, 240));
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(new Color(200, 200, 200));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private static PdfPCell createTableCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(new Color(240, 240, 240));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    @FXML
    public void onExportPdfButtonClick() {
        showExportHelpDialog();
        try {
            // 保持原有逻辑，获取学生和获奖信息
            Map<String, Object> form = dataTableView.getSelectionModel().getSelectedItem();
            if (form == null) {
                MessageDialog.showDialog("没有选择，不能导出");
                return;
            }

            String targetStudentNum = CommonMethod.getString(form, "num");
            DataRequest req = new DataRequest();
            req.add("numName", targetStudentNum);

            DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);
            DataResponse response = HttpRequestUtil.request("/api/award/getRelatedStudentForPdf", req);

            List<Map<String, Object>> students = res != null && res.getCode() == 0 ? (List<Map<String, Object>>) res.getData() : null;
            List<Map<String, Object>> relatedStudentList = response != null && response.getCode() == 0 ? (List<Map<String, Object>>) response.getData() : null;

            if (students != null && !students.isEmpty()) {
                Map<String, Object> studentMap = students.get(0);
                Student targetStudent = createStudentFromMap(studentMap);

                List<Award> relatedAwards = new ArrayList<>();
                if (relatedStudentList != null) {
                    for (Map<String, Object> awardMap : relatedStudentList) {
                        relatedAwards.add(createAwardFromMap(awardMap));
                    }
                }

                // 打开文件选择对话框
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("请选择导出文件路径");
                fileDialog.setInitialDirectory(new File("D:/"));
                fileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF文件", "*.pdf"));

                File selectedFile = fileDialog.showSaveDialog(null);
                if (selectedFile != null) {
                    String filePath = selectedFile.getAbsolutePath();
                    if (!filePath.toLowerCase().endsWith(".pdf")) {
                        filePath += ".pdf";
                    }

                    // 生成 PDF（注意：这里传入的是单学生列表）
                    generateStudentPdf(Collections.singletonList(targetStudent), relatedAwards, filePath);
                    MessageDialog.showDialog("导出成功！");
                }
            } else {
                MessageDialog.showDialog(res != null ? res.getMsg() : "未找到对应的学生信息");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("导出失败：" + e.getMessage());
        }
    }

    // 新增帮助对话框方法
    private void showExportHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("导出PDF帮助");
        alert.setHeaderText("如何导出学生信息PDF");
        alert.setContentText(
                "1. 在弹出的文件保存对话框中，选择要保存PDF的文件夹\n" +
                        "2. 在\"文件名\"字段中输入PDF文件的名称（例如：学生信息.pdf）\n" +
                        "3. 确认文件路径和名称无误后，点击\"保存\"按钮\n\n" +

                        "注意事项：\n" +
                        "- 请确保有写入该文件夹的权限\n" +
                        "- 文件名不要包含特殊字符（如 / \\ : * ? \" < > | ）\n" +
                        "- 如果文件已存在，将会被覆盖"
        );

        // 自定义对话框样式（可选）
        alert.getDialogPane().setStyle("-fx-font-size: 13px;");
        alert.showAndWait();
    }

    // 封装学生对象创建逻辑
    private Student createStudentFromMap(Map<String, Object> studentMap) {
        Student student = new Student();
        student.setPersonId(CommonMethod.getInteger(studentMap, "personId"));
        student.setNum(CommonMethod.getString(studentMap, "num"));
        student.setName(CommonMethod.getString(studentMap, "name"));
        student.setType(CommonMethod.getString(studentMap, "type"));
        student.setDept(CommonMethod.getString(studentMap, "dept"));
        student.setCard(CommonMethod.getString(studentMap, "card"));
        student.setGender(CommonMethod.getString(studentMap, "gender"));
        student.setGenderName(CommonMethod.getString(studentMap, "genderName"));
        student.setBirthday(CommonMethod.getString(studentMap, "birthday"));
        student.setEmail(CommonMethod.getString(studentMap, "email"));
        student.setPhone(CommonMethod.getString(studentMap, "phone"));
        student.setAddress(CommonMethod.getString(studentMap, "address"));
        student.setIntroduce(CommonMethod.getString(studentMap, "introduce"));
        student.setMajor(CommonMethod.getString(studentMap, "major"));
        student.setClassName(CommonMethod.getString(studentMap, "className"));
        return student;
    }

    // 封装奖项对象创建逻辑
    private Award createAwardFromMap(Map<String, Object> awardMap) {
        Award award = new Award();
        award.setAwardName(CommonMethod.getString(awardMap, "awardName"));
        award.setAwardLevel(CommonMethod.getString(awardMap, "awardLevel"));
        award.setAwardTime(CommonMethod.getString(awardMap, "awardTime"));
        return award;
    }
}
