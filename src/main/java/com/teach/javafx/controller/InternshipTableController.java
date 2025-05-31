package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InternshipTableController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> classNameColumn;
    @FXML
    private TableColumn<Map, String> InternshipSpaceColumn;
    @FXML
    private TableColumn<Map, String> InternshipTimeColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;//Map表示每一列下的每一格都是一个Map对象，后面的就是表格里面显示的东西的类型，这里是按钮类型

    private ArrayList<Map> InternshipList = new ArrayList();//用于存储从服务器获取的Internship类型数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();//没它不能显示表格数据


    @FXML
    private ComboBox<OptionItem> studentComboBox;//下拉框控件，里面的OptionItem指的是这个控件下拉框里面的元素都是这个类型


    private List<OptionItem> studentList;
                                     //下拉框拉下来之后显示的数据控件，
                                         // 就是把下拉框的数据存储在这个List中，每一个也是OptionItem类型，可能是便于转换
                                         //OptionItem里面有三个性质，一个是id，用数字来标识数据，一个是value，就是它的主键的值，一个是title，一个是实际显示的东西


    private InternshipEditController InternshipEditController = null;
    private Stage stage = null;

    public boolean canEdit = true;

    public List<OptionItem> getStudentList() {
        return studentList;
    }

    public void  onQueryButtonClick() {//这个方法就是从数据库获取一次数据，然后展示出来，调用它相当于刷新一次
        Integer personId = 0;
        OptionItem op;//这些对象如果是null，相当于会把所有数据都获取
        op = studentComboBox.getSelectionModel().getSelectedItem();//用这个方法就能获取到OptionItem类型的对象,
                                                                   // 这个方法返回值的类型可能就是获取到的是什么类型就转换成什么类型
        if(op.getValue()==null||op==null)
        { Refresh(); return;}
        else if (op != null)//如果没有获取到，这里的null就是没有获取到对象，并不是对象里面没有值
        {personId = Integer.parseInt(op.getValue());}//获取这个OptionItem里面的一个叫Value的变量的值并且转变成int类型，也可能就是主键的数字
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        res = HttpRequestUtil.request("/api/Internship/Query_getInternshipList", req);//通过这三个值来查找相应的Internship表，然后展示出来
        if (res != null && res.getCode() == 0) {//前面的res的判断是用来看有没有获取到东西，getCode那个是看
            InternshipList = (ArrayList<Map>) res.getData();//这里看似直接往Internship里面加数据，
                                                           // 没有清空原数据，但是这里是一个覆盖的方法，
                                                           //直接把对原数据的指向给指到了新数据
        }
        setTableViewData();
    }

    public void  Refresh() {//这个方法就是从数据库获取一次数据，然后展示出来，调用它相当于刷新一次
        DataResponse res;
        DataRequest req = new DataRequest();
        res = HttpRequestUtil.request("/api/Internship/getInternshipList", req);
        if (res != null && res.getCode() == 0) {//前面的res的判断是用来看有没有获取到东西，getCode那个是看
            InternshipList = (ArrayList<Map>) res.getData();//这里看似直接往Internship里面加数据，
            // 没有清空原数据，但是这里是一个覆盖的方法，
            //直接把对原数据的指向给指到了新数据
        }
        setTableViewData();
    }

    private void setTableViewData() {
        observableList.clear();
        Map map;
        Button editButton;
        for (int i = 0; i < InternshipList.size(); i++) {//获取现在最新的InternshipList表里面的数据
            map = InternshipList.get(i);                 //存到map里面，然后再加上一个叫“编辑”的按钮
            editButton = new Button("编辑");          //然后再给每个按钮设置一个Id
            editButton.setId("edit" + i);               //并且再设置一个按下之后的反应
            editButton.setOnAction((ActionEvent event) -> {
                editItem(((Button) event.getSource()).getId());
            });
            map.put("edit", editButton);//一直到这里都相当于是封装了一个数据
            observableList.addAll(FXCollections.observableArrayList(map));//每次遍历都存进去一行数据，直到存完为止
        }
        //数据都导入进去之后就可以展示了
        dataTableView.setItems(observableList);//生成新的表格
    }

    public void editItem(String name) {
        canEdit=true;
        if (name == null)
            return;
        int j = Integer.parseInt(name.substring(4, name.length()));//获取edit后面的数字，这个数字能表示这是第几行的索引值，
                                                                   // 一般就是第n行的索引是n-1，这种写法在setTableView里面有
        Map data = InternshipList.get(j);//相当于通过索引来获取数据
        initDialog();//初始化这个对话框
        InternshipEditController.showDialog(data);//展示对话框
        MainApplication.setCanClose(false);
        stage.showAndWait();//让对话框一直存在
    }
    public void initialize() {
        //这里是设置一下键的名称
        studentNumColumn.setCellValueFactory(new MapValueFactory("studentNum"));//学号
        studentNameColumn.setCellValueFactory(new MapValueFactory("studentName"));//姓名
        classNameColumn.setCellValueFactory(new MapValueFactory("className"));//班级
        InternshipTimeColumn.setCellValueFactory(new MapValueFactory("InternshipTime"));//实习时间
        InternshipSpaceColumn.setCellValueFactory(new MapValueFactory("InternshipSpace"));//实习地点
        editColumn.setCellValueFactory(new MapValueFactory("edit"));//编辑栏
        studentList =null;
        DataRequest req=new DataRequest();//创建一个请求对象，便于从服务器获取数据
        studentList =HttpRequestUtil.requestOptionItemList("/api/Internship/getStudentItemOptionList",req);//从服务器获取学生下拉框数据


        OptionItem item=new OptionItem(null,null,"请选择");//先设置一个初始显示的选项
        studentComboBox.getItems().addAll(item);//把item加进去
        studentComboBox.getItems().addAll(studentList);//把从后端获取的数据加进去
        //设置首次查询为多选
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //刷新一下数据
        Refresh();
    }
    private void initDialog() {
        //如果已经产生了对话框，就直接返回
        FXMLLoader fxmlLoader;
        Scene scene ;
        if (canEdit) {
            try {//加载对话框的fxml布局
                fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Internship-edit-dialog.fxml"));
                scene = new Scene(fxmlLoader.load(), 360, 240);
                stage = new Stage();
                stage.initOwner(MainApplication.getMainStage());//设置主窗口为所有者
                stage.initModality(Modality.NONE);//非模态窗口
                stage.setAlwaysOnTop(true);//窗口置顶
                stage.setScene(scene);//设置场景
                stage.setTitle("实习记录修改对话框！");//标题
                stage.setOnCloseRequest(event -> {
                    MainApplication.setCanClose(true);
                });//允许主窗口关闭
                InternshipEditController = fxmlLoader.getController();//获取这个fxml文件的控制器类示例
                InternshipEditController.setInternshipTableController(this);//设置回调，让对话框能控制当前实例
                InternshipEditController.init();//初始对话框控制器
            } catch (IOException e) {//加载失败时抛出运行时错误
                throw new RuntimeException(e);
            }
        }
        else {
            try {//加载对话框的fxml布局
                fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Internship-edit-dialog.fxml"));
                scene = new Scene(fxmlLoader.load(), 360, 200);
                stage = new Stage();
                stage.initOwner(MainApplication.getMainStage());//设置主窗口为所有者
                stage.initModality(Modality.NONE);//非模态窗口
                stage.setAlwaysOnTop(true);//窗口置顶
                stage.setScene(scene);//设置场景
                stage.setTitle("实习记录添加对话框！");//标题
                stage.setOnCloseRequest(event -> {
                    MainApplication.setCanClose(true);
                });//允许主窗口关闭
                InternshipEditController = fxmlLoader.getController();//获取这个fxml文件的控制器类示例
                InternshipEditController.setInternshipTableController(this);//设置回调，让对话框能控制当前实例
                InternshipEditController.init();//初始对话框控制器
            } catch (IOException e) {//加载失败时抛出运行时错误
                throw new RuntimeException(e);
            }
        }
    }
    public void doClose(String cmd,Map data) {
        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer personId = CommonMethod.getInteger(data, "personId");
        if(personId == null){
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }
        String InternshipSpace = (String) data.get("InternshipSpace");
        if(InternshipSpace .isEmpty()){
            MessageDialog.showDialog("没有填写实习地点不能添加保存！");
            return;
        }
        String InternshipTime = (String) data.get("InternshipTime");
        if(InternshipTime == null){
            MessageDialog.showDialog("没有填写实习时间不能添加保存！");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("InternshipSpace", InternshipSpace);
        req.add("InternshipTime", InternshipTime);
        res=HttpRequestUtil.request("/api/Internship/addInternship", req);
        if(res != null && res.getCode() == 0){
            Refresh();
        }
    }

    @FXML
    private void onAddButtonClick() {
        canEdit = false;
        initDialog();
        InternshipEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    private void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null){
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗？");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        Integer InternshipId=CommonMethod.getInteger(form, "InternshipId");
        DataRequest req = new DataRequest();
        req.add("InternshipId", InternshipId);
        DataResponse res=HttpRequestUtil.request("/api/Internship/deleteInternship", req);
        if(res.getCode() == 0){
            Refresh();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    private void onEditButtonClick() {
        canEdit=true;
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null){
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        InternshipEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    public void doEdit(String cmd, Map data) {
        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer InternshipId=CommonMethod.getInteger(data, "InternshipId");
        Integer personId = CommonMethod.getInteger(data, "personId");
//        if(personId == null){
//            MessageDialog.showDialog("没有选中学生不能添加保存！");
//            return;
//        }
//        String InternshipSpace = (String) data.get("InternshipSpace");
//        if(InternshipSpace .isEmpty()){
//            MessageDialog.showDialog("没有填写实习地点不能添加保存！");
//            return;
//        }
//        String InternshipTime = (String) data.get("InternshipTime");
//        if(InternshipTime == null){
//            MessageDialog.showDialog("没有填写实习时间不能添加保存！");
//            return;
//        }
        String InternshipSpace = (String) data.get("InternshipSpace");
        String InternshipTime = (String) data.get("InternshipTime");
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("InternshipSpace", InternshipSpace);
        req.add("InternshipTime", InternshipTime);
        req.add("InternshipId", InternshipId);
        res=HttpRequestUtil.request("/api/Internship/editInternship", req);
        if(res != null && res.getCode() == 0){
            Refresh();
        }
    }
}
