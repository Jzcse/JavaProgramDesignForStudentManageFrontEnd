package com.teach.javafx.controller.teacherEnd;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.controller.studentEnd.GlobalSession;
import com.teach.javafx.controller.teacherEnd.inner.ScoreManagementInnerController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.spi.ServiceRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreManagementController extends ToolController {
    // FX组件
    @FXML
    private TableView<Map<String, String>> tableView;
    @FXML
    private TableColumn<Map<String, String>, String> num;
    @FXML
    private TableColumn<Map<String, String>, String> name;
    @FXML
    private TableColumn<Map<String, String>, String> time;
    @FXML
    private TableColumn<Map<String, String>, String> dayOfWeek;
    @FXML
    private TableColumn<Map<String, String>, String> classroom;
    @FXML
    private TableColumn<Map<String, String>, String> stuSum;
    // 全局变量
    private String teacherId, courseId;
    private ArrayList<Map<String, String>> courseList = new ArrayList<>();
    private ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList();

    public void initialize() {
        this.teacherId = GlobalSession.getInstance().getTeacherId();

        // 列值
        num.setCellValueFactory(new MapValueFactory("num"));
        name.setCellValueFactory(new MapValueFactory("name"));
        time.setCellValueFactory(new MapValueFactory("time"));
        dayOfWeek.setCellValueFactory(new MapValueFactory("dayOfWeek"));
        classroom.setCellValueFactory(new MapValueFactory("classroom"));
        stuSum.setCellValueFactory(new MapValueFactory("studentTotal"));

        // 行选中
        TableView.TableViewSelectionModel<Map<String, String>> tableViewSelectionModel = tableView.getSelectionModel();
        ObservableList<Integer> list = tableViewSelectionModel.getSelectedIndices();
        list.addListener(this::onRowSelect);

        getData();
        setTableView();
    }

    private void onRowSelect(ListChangeListener.Change<? extends Integer> change) {
        courseId = tableView.getSelectionModel().getSelectedItem().get("courseId");
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/teacher-end/inner/score-management-inner-panel.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.setTitle("成绩登记");
            Scene scene = new Scene(root, 1000,600);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            //
            ScoreManagementInnerController scoreManagementInnerController = loader.getController();
            scoreManagementInnerController.initialize(courseId);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }

    private void getData() {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("teacherId", teacherId);
        dataRequest.add("selection", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/course/getTeacherCourseListResult", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                courseList = (ArrayList<Map<String, String>>) dataResponse.getData();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接!");
        }
    }

    private void setTableView() {
        observableList.clear();
        for(Map<String, String> stringStringMap : courseList) {
            observableList.addAll(FXCollections.observableArrayList(stringStringMap));
        }
        tableView.setItems(observableList);
    }
}
