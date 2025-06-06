package com.teach.javafx.controller;

import com.lowagie.text.*;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;


import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class teacherController extends ToolController {
    //IMPORT FXML ID
    @FXML
    private TableView<Map<String, String>> dataTableView;           //教师信息表
    @FXML
    private TableColumn<Map<String, String>, String> numColumn;     //教师学工号
    @FXML
    private TableColumn<Map<String, String>, String> nameColumn;   //教师姓名
    @FXML
    private TableColumn<Map<String, String>, String> deptColumn;   //教师学院
    @FXML
    private TableColumn<Map<String, String>, String> majorColumn;  //教师专业
    @FXML
    private TableColumn<Map<String, String>, String> titleColumn;  //教师职称
    @FXML
    private TableColumn<Map<String, String>, String> cardColumn;   //教师证件号
    @FXML
    private TableColumn<Map<String, String>, String> genderColumn; //教师性别
    @FXML
    private TableColumn<Map<String, String>, String> birthdayColumn;//教师生日
    @FXML
    private TableColumn<Map<String, String>, String> emailColumn;  //教师电子邮件
    @FXML
    private TableColumn<Map<String, String>, String> phoneColumn;  //教师电话
    @FXML
    private TableColumn<Map<String, String>, String> addressColumn;//教师地址
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
    private Button editButton;
    @FXML
    private Button exportPdfButton; // 新增：导出PDF按钮

    //service
    private boolean isNew = false;
    private int teacherCount;
    private int pageCount;
    private Integer personId;
    private ArrayList<Map<String, String>> teacherList = new ArrayList<>();
    private ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList();
    private List<OptionItem> genderList;
    private String baseUrl;

    private void setVBoxVisible(boolean isVisible) {
        vBoxPanel.setVisible(isVisible);
        vBoxPanel.setManaged(isVisible);
    }

    private void setEditAble(boolean isEditAble) {
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

    public void refreshTeacherList() {
        int currentPage = (int) pagination.getCurrentPageIndex();
        DataRequest dataRequest = new DataRequest();
        dataRequest.add("page", currentPage);
        dataRequest.add("size", 33);
        DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/getTeacherList", dataRequest);
        if (dataResponse != null && dataResponse.getCode() == 0) {
            teacherList = (ArrayList<Map<String, String>>) dataResponse.getData();
            setTableViewData();
        }
    }

    public void setTableViewData() {
        observableList.clear();
        for (Map<String, String> stringStringMap : teacherList) {
            observableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        dataTableView.setItems(observableList);
    }

    public void refreshPageCount() {
        DataRequest dataRequestForTeacherCount = new DataRequest();
        DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/getTeacherCount", dataRequestForTeacherCount);
        if (dataResponse != null && dataResponse.getCode() == 0) {
            Map<String, String> map = (Map<String, String>) dataResponse.getData();
            teacherCount = Integer.parseInt(CommonMethod.getString(map, "count"));
            if (teacherCount % 33 == 0) {
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
        if (dataResponse != null && dataResponse.getCode() == 0) {
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

        // 新增：初始化导出PDF按钮
        exportPdfButton.setText("导出 PDF 简历");
        exportPdfButton.setOnAction(event -> exportTeacherResume());
    }

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        isNew = false;
        setVBoxVisible(true);
        setEditAble(false);
        saveButton.setVisible(false);
        saveButton.setManaged(false);
        editButton.setManaged(true);
        editButton.setVisible(true);
        exportPdfButton.setManaged(true);
        exportPdfButton.setVisible(true); // 新增：选中教师时显示导出按钮
        changeTeacherInfo();
        displayPhoto();
    }

    public void displayPhoto() {
        try {
            // 获取当前文件的路径并设置图片文件保存位置
            File saveDir;
            String classPath = System.getProperty("java.class.path");
            if (classPath.endsWith(".jar")) {
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
            if (!newDirFile.exists()) {
                if (newDirFile.mkdirs()) {
                    System.out.println("新目录创建成功" + newDirFile.getAbsolutePath());
                } else {
                    MessageDialog.showDialog("无法在当前文件夹创建新目录用以下载并保存用户的图片，请检查权限！");
                }
            }
            // 下载图片并保存到指定文件夹
            DataRequest dataRequest = new DataRequest();
            dataRequest.add("personId", personId);
            DataResponse dataResponse = HttpRequestUtil.request("/api/photo/download", dataRequest);
            if (dataResponse != null) {
                if (dataResponse.getCode() == 0) {
                    System.out.println("下载成功");
                    try {
                        Map<String, String> dataMap = (Map<String, String>) dataResponse.getData();
                        String data = CommonMethod.getString(dataMap, "file");
                        byte[] fileBytes = java.util.Base64.getDecoder().decode(data);

                        File saveFile = new File(newDirFile, personId + ".jpg");

                        Files.write(saveFile.toPath(), fileBytes);
                        System.out.println("文件保存成功");

                        String url = saveFile.toURI().toString();
                        Image image = new Image(url);
                        if (image.isError()) {
                            System.out.println("图片加载失败");
                        } else {
                            System.out.println("图片加载成功");
                            photoImage.setImage(image);
                        }
                    } catch (IOException e) {
                        MessageDialog.showDialog("照片下载失败，请检查：\n1. 文件权限；\n2. 磁盘空间；\n3. 写入文件夹是否正在被其他程序占用。");
                        throw new RuntimeException(e);
                    } catch (IllegalArgumentException e) {
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

    public void changeTeacherInfo() {
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
        editButton.setManaged(false);
        editButton.setVisible(false);
        exportPdfButton.setManaged(false);
        exportPdfButton.setVisible(false); // 新增：新增教师时隐藏导出按钮
        clearPanel();
    }

    //  清空输入信息
    public void clearPanel() {
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
        // 清空照片
        Image image = new Image("https://ts4.tc.mm.bing.net/th/id/OIP-C.rA8p_zRRqpN4GlcYol5p4AAAAA?rs=1&pid=ImgDetMain");
        photoImage.setImage(image);
    }

    public void onDeleteButtonClick(ActionEvent actionEvent) {
        Map<String, String> from = dataTableView.getSelectionModel().getSelectedItem();
        if (from == null) {
            MessageDialog.showDialog("请选定要删除的教师");
        } else {
            int choice = MessageDialog.choiceDialog("确定要删除吗？");
            if (choice == MessageDialog.CHOICE_YES) {
                personId = CommonMethod.getInteger(from, "personId");
                DataRequest dataRequest = new DataRequest();
                dataRequest.add("personId", personId);
                DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/deleteTeacher", dataRequest);
                if (dataResponse != null) {
                    if (dataResponse.getCode() == 0) {
                        MessageDialog.showDialog("删除成功！");
                        dataTableView.getSelectionModel().clearSelection();
                        //刷新教师数量及页码数
                        refreshPageCount();
                        //刷新表格
                        refreshTeacherList();
                        // 隐藏右侧面板
                        setVBoxVisible(false);
                        exportPdfButton.setManaged(false);
                        exportPdfButton.setVisible(false); // 新增：删除教师后隐藏导出按钮
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
        if (input.matches(".*\\d.*")) {
            //num query
            dataRequest.add("num", input);
            DataResponse dataResponse = HttpRequestUtil.request("/api/teacher/searchTeacherByNum", dataRequest);
            if (dataResponse != null) {
                if (dataResponse.getCode() == 0) {
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

            if (dataResponse != null) {
                if (dataResponse.getCode() == 0) {
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
        if (file != null) {
            DataResponse dataResponse = HttpRequestUtil.newUploadFile("/api/photo/upload", file, "file", personId.toString());
            if (dataResponse.getCode() == 0) {
                MessageDialog.showDialog("上传成功");
                displayPhoto();
            }
        } else {
            System.out.println("Error to choose jpg files");
            MessageDialog.showDialog("Error to choose jpg files");
        }
    }

    public void onSaveButtonClick(ActionEvent actionEvent) {
        if (isNew) {
            if (numField.getText().isEmpty()) {
                MessageDialog.showDialog("工号为空，不能添加教师！");
                return;
            }
            if (nameField.getText().isEmpty()) {
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
            if (!birthdayTemp.matches("^(?:(?:19|20)\\d{2})-(?:(?:0[1-9]|1[0-2]))-(?:(?:0[1-9]|[12]\\d|3[01]))$")) {
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
            if (dataResponse.getCode() == 0) {
                MessageDialog.showDialog("添加教师成功");
                //刷新教师数与页数
                refreshPageCount();
                //设置页码值
                pagination.setCurrentPageIndex(pageCount - 1);
                //刷新教师列表并刷新界面
                refreshTeacherList();
                //clear
                clearPanel();
                // 隐藏右侧面板
                setVBoxVisible(false);
                exportPdfButton.setManaged(false);
                exportPdfButton.setVisible(false); // 新增：添加教师后隐藏导出按钮
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
            if (!birthdayTemp.matches("^(?:(?:19|20)\\d{2})-(?:(?:0[1-9]|1[0-2]))-(?:(?:0[1-9]|[12]\\d|3[01]))$")) {
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
            if (dataResponse != null && dataResponse.getCode() == 0) {
                MessageDialog.showDialog("更改成功！");
                //刷新表格
                refreshTeacherList();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
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
        editButton.setManaged(false);
        editButton.setVisible(false);
    }

    @FXML
    private void exportTeacherResume() {
        Map<String, String> selectedTeacher = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            MessageDialog.showDialog("请先选择一位教师");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存教师简历");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF 文件", "*.pdf"));
        fileChooser.setInitialFileName(selectedTeacher.get("name") + "_简历.pdf");
        File file = fileChooser.showSaveDialog(dataTableView.getScene().getWindow());

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file);
                 Document document = new Document(PageSize.A4)) {

                PdfWriter writer = PdfWriter.getInstance(document, fos);
                document.open();
                document.setMargins(25, 25, 25, 25);

                // 颜色定义
                Color secondaryColor = new Color(26, 35, 126);
                Color textColor = new Color(33, 33, 33);
                Color borderColor = new Color(200, 200, 200);
                Color lightBackgroundColor = new Color(245, 245, 245);

                try {
                    // 字体处理（使用STSong-Light和UniGB-UCS2-H）
                    BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
                    Font regularFont = new Font(baseFont, 10, Font.NORMAL, textColor);
                    Font boldFont = new Font(baseFont, 14, Font.BOLD, secondaryColor);
                    Font headerFont = new Font(baseFont, 20, Font.BOLD, secondaryColor); // 头部姓名专用字体

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
                    infoCell.setPaddingTop(10);
                    infoCell.setPaddingLeft(15);
                    infoCell.setVerticalAlignment(Element.ALIGN_TOP);

                    // 教师姓名（放大字号，突出显示）
                    Paragraph namePara = new Paragraph(selectedTeacher.get("name"), headerFont);
                    namePara.setAlignment(Element.ALIGN_LEFT);
                    namePara.setSpacingAfter(5f);

                    // 其他个人信息
                    StringBuilder infoBuilder = new StringBuilder();
                    infoBuilder.append("编号：").append(selectedTeacher.get("num")).append("    ")
                            .append("性别：").append(getGenderDisplay(selectedTeacher.get("gender"))).append("\n");
                    infoBuilder.append("学院：").append(selectedTeacher.get("dept")).append("    ")
                            .append("职称：").append(selectedTeacher.get("title")).append("\n");

                    Paragraph infoPara = new Paragraph(infoBuilder.toString(), regularFont);
                    infoPara.setLeading(14f); // 设置行间距
                    infoPara.setAlignment(Element.ALIGN_LEFT);

                    infoCell.addElement(namePara);
                    infoCell.addElement(infoPara);
                    headerTable.addCell(infoCell);

                    containerTable.addCell(headerTable);

                    // 个人资料部分
                    containerTable.addCell(createSectionTitle("个人资料", boldFont, secondaryColor));
                    PdfPTable personalInfoTable = createInfoTable();
                    addInfoRow(personalInfoTable, "编号", selectedTeacher.get("num"), regularFont);
                    addInfoRow(personalInfoTable, "姓名", selectedTeacher.get("name"), regularFont);
                    addInfoRow(personalInfoTable, "性别", getGenderDisplay(selectedTeacher.get("gender")), regularFont);
                    addInfoRow(personalInfoTable, "学院", selectedTeacher.get("dept"), regularFont);
                    addInfoRow(personalInfoTable, "职称", selectedTeacher.get("title"), regularFont);
                    containerTable.addCell(personalInfoTable);

                    // 联系方式
                    containerTable.addCell(createSectionTitle("联系方式", boldFont, secondaryColor));
                    PdfPTable contactTable = createInfoTable();
                    addInfoRow(contactTable, "邮箱", selectedTeacher.get("email"), regularFont);
                    addInfoRow(contactTable, "电话", selectedTeacher.get("phone"), regularFont);
                    containerTable.addCell(contactTable);

                    // 个人履历区（留白）
                    containerTable.addCell(createSectionTitle("个人履历", boldFont, secondaryColor));
                    String introduction = "（请在此处填写个人履历信息）";
                    Paragraph introPara = new Paragraph(introduction, regularFont);
                    introPara.setAlignment(Element.ALIGN_JUSTIFIED);
                    introPara.setLeading(16.5f);
                    containerTable.addCell(introPara);

                    document.add(containerTable);

                    // 页脚
                    Paragraph footer = new Paragraph(
                            "简历生成时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            new Font(baseFont, 8, Font.NORMAL, new Color(150, 150, 150))
                    );
                    footer.setAlignment(Element.ALIGN_CENTER);
                    document.add(footer);

                } catch (DocumentException e) {
                    e.printStackTrace();
                    MessageDialog.showDialog("导出失败：生成PDF文档时出错");
                }
            } catch (IOException e) {
                e.printStackTrace();
                MessageDialog.showDialog("导出失败：文件操作错误");
            }
        }
    }

    // 辅助方法 - 创建信息行
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
        PdfPTable table = new PdfPTable(2); // 使用两列布局
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        return table;
    }

    private String getGenderDisplay(String genderCode) {
        return "1".equals(genderCode) ? "男" : "女";
    }
}