package com.teach.javafx.controller.base;

import com.teach.javafx.request.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.stage.FileChooser;
import com.teach.javafx.request.DataRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * DictionaryController 登录交互控制类 对应 base/dictionary-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class MaterialController{
    @FXML
    private TreeTableView<MyTreeNode> treeTable;
    @FXML
    private TreeTableColumn<MyTreeNode, String> fileNameColumn;
    @FXML
    private TreeTableColumn<MyTreeNode, String> titleColumn;

    @FXML
    public void initialize() {
        MyTreeNode root = HttpRequestUtil.requestTreeNode("/api/base/getMaterialTreeNode",new DataRequest());
        if(root == null)
            return;
        fileNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        fileNameColumn.setCellFactory(TextFieldTreeTableCell.<MyTreeNode>forTreeTableColumn());
        titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("label"));
        titleColumn.setCellFactory(TextFieldTreeTableCell.<MyTreeNode>forTreeTableColumn());
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
        treeTable.getSelectionModel().selectFirst();
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> tsm = treeTable.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
    }
    @FXML
    public void onDownloadButtonClick(){
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> sm = treeTable.getSelectionModel();
        if (sm.isEmpty()) {
            MessageDialog.showDialog("没有选择，无法下载");
            return;
        }
        int rowIndex = sm.getSelectedIndex();
        TreeItem<MyTreeNode> selectedItem = sm.getModelItem(rowIndex);
        MyTreeNode node = selectedItem.getValue();
        if(!node.getIsLeaf().equals(1)) {
            MessageDialog.showDialog("选择为目录，不是文件，无法下载");
            return;
        }
        String fileName =node.getValue();
        int index = fileName.lastIndexOf(".");
        String suffix = fileName.substring(index+1,fileName.length());
        DataRequest req = new DataRequest();
        String dir = selectedItem.getParent().getValue().getValue();
        String path= null;
        if(dir== null || dir.length()== 0) {
            path = "material/" + fileName;
        }else {
            path = "material/" +dir+"/"+ fileName;
        }
        req.add("fileName",path);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getFileByteData", req);
        if (bytes != null) {
            FileChooser fileDialog = new FileChooser();
            fileDialog.setTitle("请选择保存的文件名");
            fileDialog.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(suffix+" 文件", "*."+suffix));
            fileDialog.setInitialFileName(fileName);
            File file = fileDialog.showSaveDialog(null);
            if(file != null) {
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                    MessageDialog.showDialog("下载成功！");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
