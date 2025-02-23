package com.teach.javafx.controller.base;

import com.teach.javafx.request.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;

import java.util.List;

/**
 * DictionaryController 登录交互控制类 对应 base/dictionary-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class DictionaryController {
    @FXML
    private TreeTableView<MyTreeNode> treeTable;
    @FXML
    private TreeTableColumn<MyTreeNode, Integer> idColumn;
    @FXML
    private TreeTableColumn<MyTreeNode, String> valueColumn;
    @FXML
    private TreeTableColumn <MyTreeNode, String>titleColumn;

    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    public void editCommitValue(TableColumn.CellEditEvent<MyTreeNode,String> editEvent){
        MyTreeNode node = editEvent.getRowValue();
        node.setValue(editEvent.getNewValue());
    }
    public void editCommitLabel(TableColumn.CellEditEvent<MyTreeNode,String> editEvent){
        MyTreeNode node = editEvent.getRowValue();
        node.setLabel(editEvent.getNewValue());
    }
    @FXML
    public void initialize() {
        List<MyTreeNode> dList= HttpRequestUtil.requestTreeNodeList("/api/base/getDictionaryTreeNodeList",new DataRequest());
        if(dList == null || dList.size() == 0)
            return;

        idColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        valueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));

        idColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new IntegerStringConverter()));
        valueColumn.setCellFactory(TextFieldTreeTableCell.<MyTreeNode>forTreeTableColumn());
        titleColumn.setCellFactory(TextFieldTreeTableCell.<MyTreeNode>forTreeTableColumn());
        idColumn.setOnEditCommit(e->{
            MyTreeNode node = e.getRowValue().getValue();
            node.setId(e.getNewValue());
        });
        valueColumn.setOnEditCommit(e->{
            MyTreeNode node = e.getRowValue().getValue();
            node.setValue(e.getNewValue());
        });
        titleColumn.setOnEditCommit(e->{
            MyTreeNode node = e.getRowValue().getValue();
            node.setTitle(e.getNewValue());
        });
        MyTreeNode root = new MyTreeNode();
        root.setChildren(dList);
        TreeItem<MyTreeNode> rootNode = new TreeItem<>(root);
        MyTreeNode node;
        TreeItem<MyTreeNode> tNode, tNodes;
        List<MyTreeNode> sList;
        List<MyTreeNode> cList = root.getChildren();
        int i,j;
        for(i = 0;  i <cList.size();i++) {
            node = cList.get(i);
            tNode = new TreeItem<>(node);
            sList = node.getChildren();
            for(j = 0; j < sList.size();j++) {
                tNodes = new TreeItem<>(sList.get(j));
                tNode.getChildren().add(tNodes);
            }
            rootNode.getChildren().add(tNode);
        }
        rootNode.setExpanded(true);
        treeTable.setRoot(rootNode);
        treeTable.setPlaceholder(new Label("点击添加按钮增加一行"));
        treeTable.setEditable(true);
        treeTable.getSelectionModel().selectFirst();
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> tsm = treeTable.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener((ListChangeListener.Change<? extends Integer> change) -> {
            System.out.println("Row selection has changed");
        });
    }

    private void editItem(TreeItem<MyTreeNode> item) {
        int newRowIndex = treeTable.getRow(item);
        treeTable.scrollTo(newRowIndex);
        TreeTableColumn<MyTreeNode, ?> firstCol = treeTable.getColumns().get(0);
        treeTable.getSelectionModel().select(item);
        treeTable.getFocusModel().focus(newRowIndex, firstCol);
        treeTable.edit(newRowIndex, firstCol);
    }
    @FXML
    public void onAddButtonClick(){
        if (treeTable.getSelectionModel().isEmpty()) {
            MessageDialog.showDialog("选择一个要添加的的行");
            return;
        }
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> sm = treeTable.getSelectionModel();
        int rowIndex = sm.getSelectedIndex();
        TreeItem<MyTreeNode> selectedItem = sm.getModelItem(rowIndex);
        MyTreeNode node = selectedItem.getValue();
        MyTreeNode newNode = new MyTreeNode();
        newNode.setPid(node.getId());
        node.getChildren().add(newNode);
        TreeItem<MyTreeNode> item = new TreeItem<>(newNode);
        selectedItem.getChildren().add(item);
        selectedItem.setExpanded(true);
        this.editItem(item);
    }
    @FXML
    public void onDeleteButtonClick(){
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> sm = treeTable.getSelectionModel();
        if (sm.isEmpty()) {
            MessageDialog.showDialog("没有选择，无法删除");
            return;
        }
        int rowIndex = sm.getSelectedIndex();
        TreeItem<MyTreeNode> selectedItem = sm.getModelItem(rowIndex);
        TreeItem<MyTreeNode> parent = selectedItem.getParent();
        if (parent == null) {
            MessageDialog.showDialog("不能删除根节点");
        }
        parent.getChildren().remove(selectedItem);
        MyTreeNode node = selectedItem.getValue();
        parent.getValue().getChildren().remove(node);
        DataRequest req = new DataRequest();
        req.add("id",node.getId());
        DataResponse res= HttpRequestUtil.request("/api/base/dictionaryDelete", req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
        }else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    @FXML
    public void onSaveButtonClick() {
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> sm = treeTable.getSelectionModel();
        int rowIndex = sm.getSelectedIndex();

        TreeItem<MyTreeNode> selectedItem = sm.getModelItem(rowIndex);
        MyTreeNode node = selectedItem.getValue();
        DataRequest req = new DataRequest();
        req.add("id", node.getId());
        req.add("value",node.getValue());
        req.add("title",node.getTitle());
        req.add("pid",node.getPid());
        DataResponse res = HttpRequestUtil.request("/api/base/dictionarySave", req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
        }else {
            MessageDialog.showDialog(res.getMsg());
        }

    }
}
