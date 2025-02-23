package com.teach.javafx.controller.base;

import com.teach.javafx.request.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;

import java.util.List;
import java.util.StringTokenizer;

/**
 * MenuController 登录交互控制类 对应 base/menu-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class MenuController {
    @FXML
    private TreeView<MyTreeNode> menuTreeView;
    @FXML
    private Label nodeParentLabel;
    @FXML
    private TextField nodeIdField;
    @FXML
    private TextField nodeNameField;
    @FXML
    private TextField nodeTitleField;

    private TreeItem<MyTreeNode> treeItem;
    private MyTreeNode editNode= null;

    @FXML
    private  CheckBox nodeAdminCheckBox;
    @FXML
    private  CheckBox nodeStudentCheckBox;
    @FXML
    private  CheckBox nodeTeacherCheckBox;
    private TreeItem<MyTreeNode> root;
    private Integer editType = 0;
    private TreeItem<MyTreeNode> getTreeItem(MyTreeNode node) {
        TreeItem<MyTreeNode> item = new TreeItem<>(node);
        List<MyTreeNode> sList = node.getChildren();
        if (sList == null)
            return item;
        for (int i = 0; i < sList.size(); i++) {
            item.getChildren().add(getTreeItem(sList.get(i)));
        }
        return item;
    }

    public void valueChanged(TreeItem.TreeModificationEvent<MyTreeNode> e) {
        MyTreeNode nodeValue = e.getSource().getValue();
        System.out.println(nodeValue);
    }

    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    @FXML
    public void initialize() {
        MyTreeNode  node  = new MyTreeNode(null, "", "",0);
        root = new TreeItem<>(node);
        menuTreeView.setRoot(root);
        menuTreeView.setShowRoot(false);
        updateTreeView();
        ContextMenu contextMenu = new ContextMenu();
        MenuItem add = new MenuItem("添加");
        add.setOnAction(this::onAddButtonClick);
        MenuItem edit = new MenuItem("编辑");
        edit.setOnAction(this::onEditButtonClick);
        MenuItem delete = new MenuItem("删除");
        delete.setOnAction(this::onDeleteButtonClick);
        contextMenu.getItems().addAll(add, edit, delete);
        menuTreeView.setContextMenu(contextMenu);
        menuTreeView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
            public void handle(MouseEvent event){
                treeItem = menuTreeView.getSelectionModel().getSelectedItem();
            }
        });

    }
    public void updateTreeView(){
        DataRequest req = new DataRequest();
        List<MyTreeNode> nodeList = HttpRequestUtil.requestTreeNodeList("/api/base/getMenuTreeNodeList", req);
        if (nodeList == null ||nodeList.size()== 0)
            return;
        for (int i = 0; i < nodeList.size(); i++) {
            root.getChildren().add(getTreeItem(nodeList.get(i)));
        }
    }
    public void setRoleCheckBox(){
        nodeAdminCheckBox.setSelected(false);
        nodeStudentCheckBox.setSelected(false);
        nodeTeacherCheckBox.setSelected(false);
        if(editNode == null) {
            return;
        }
        String useTypeIds = editNode.getUserTypeIds();
        if(useTypeIds == null || useTypeIds.length() == 0 )
            return;
        StringTokenizer sz = new StringTokenizer(useTypeIds,",");
        String str;
        while(sz.hasMoreTokens()) {
            str = sz.nextToken();
            if("1".equals(str)) {
                nodeAdminCheckBox.setSelected(true);
            }else if("2".equals(str)) {
                nodeStudentCheckBox.setSelected(true);
            }
            if("3".equals(str)) {
                nodeTeacherCheckBox.setSelected(true);
            }
        }
    }
    public void updateNodePanel(){
        if(editNode == null) {
            nodeIdField.setText("");
            nodeNameField.setText("");
            nodeTitleField.setText("");
        }else {
            if(editNode.getId() == null)
                nodeIdField.setText("");
            else
                nodeIdField.setText(editNode.getId().toString());
            nodeNameField.setText(editNode.getValue());
            nodeTitleField.setText(editNode.getTitle());
        }
        setRoleCheckBox();
    }
    @FXML
    protected void onAddRootButtonClick(){
        editType = 0;
        editNode = new MyTreeNode();
        editNode.setParentTitle("");
        updateNodePanel();
    }
    protected void onAddButtonClick(ActionEvent e ) {
        if (treeItem == null || treeItem.getValue()==null) {
            MessageDialog.showDialog("没有选择，无法添加");
            return;
        }
        MyTreeNode node = treeItem.getValue();
        editNode = new MyTreeNode();
        editNode.setParentTitle(node.getTitle());
        editNode.setPid(node.getId());
        editType = 1;
        updateNodePanel();
    }
    @FXML
    protected void onEditButtonClick(ActionEvent e ) {
        if (treeItem == null || treeItem.getValue()==null) {
            MessageDialog.showDialog("没有选择，无法修改");
            return;
        }
        editType = 2;
        editNode = treeItem.getValue();
        updateNodePanel();
    }
    @FXML
    protected void onDeleteButtonClick(ActionEvent e ) {
        if(treeItem == null) {
            MessageDialog.showDialog("没有选择，无法删除");
            return;
        }
        MyTreeNode node = treeItem.getValue();
        if (node == null) {
            MessageDialog.showDialog("没有选择，无法删除");
            return;
        }
        TreeItem<MyTreeNode> parent = treeItem.getParent();
        if (parent == null ) {
            MessageDialog.showDialog("不能删除根节点");
        } else {
            int ret= MessageDialog.choiceDialog("确认要删除菜单；"+node.getLabel()+"‘吗?");
            if(ret != MessageDialog.CHOICE_YES)
                return;
            DataRequest req = new DataRequest();
            req.add("id",node.getId());
            DataResponse res= HttpRequestUtil.request("/api/base/menuDelete", req);
            if(res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
            }else {
                MessageDialog.showDialog(res.getMsg());
            }
            parent.getChildren().remove(treeItem);
            treeItem = null;
        }
    }
    @FXML
    protected void onSubmitButtonClick() {
        editNode.setId(Integer.parseInt(nodeIdField.getText()));
        editNode.setValue(nodeNameField.getText());
        editNode.setTitle(nodeTitleField.getText());
        String str = null;
        if(nodeAdminCheckBox.isSelected()) {
            if(str == null)
                str ="1";
            else str +=",1";
        }
        if(nodeStudentCheckBox.isSelected()) {
            if(str == null)
                str ="2";
            else str +=",2";
        }
        if(nodeTeacherCheckBox.isSelected()) {
            if(str == null)
                str ="3";
            else str +=",3";
        }
        editNode.setUserTypeIds(str);
        editNode.setLabel(editNode.getId()+"-"+editNode.getTitle());
        DataRequest req = new DataRequest();
        req.add("editType", editType);
        req.add("node",editNode);
        DataResponse res = HttpRequestUtil.request("/api/base/menuSave", req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
            if(editType== 0) {
                root.getChildren().add(new TreeItem<>(editNode));
            }else if(editType== 1){
                treeItem.getChildren().add(new TreeItem<>(editNode));
            }
        }else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}