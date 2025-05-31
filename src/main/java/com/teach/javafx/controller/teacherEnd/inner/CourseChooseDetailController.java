package com.teach.javafx.controller.teacherEnd.inner;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.studentEnd.GlobalSession;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class CourseChooseDetailController {
    @FXML
    private Label num;
    @FXML
    private Label name;
    @FXML
    private Label credit;
    @FXML
    private Label preCourse;
    @FXML
    private Label classname;
    @FXML
    private Label day;
    @FXML
    private Label time;

    private String courseId;

    public void initialize(String courseId) {
        this.courseId = courseId;

        getData();
    }

    private void getData() {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        dataRequest.add("course", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/course/getSingleCourse", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                Map<String, String> form = (Map<String, String>) dataResponse.getData();
                num.setText(CommonMethod.getString(form, "num"));
                name.setText(CommonMethod.getString(form, "name"));
                credit.setText(CommonMethod.getString(form, "credit"));
                preCourse.setText(CommonMethod.getString(form, "preCourseName"));
                day.setText(CommonMethod.getString(form, "dayOfWeek"));
                time.setText(CommonMethod.getString(form, "time"));
                classname.setText(CommonMethod.getString(form, "classroom"));
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接！");
        }
    }

    public void onSelectButtonClick(ActionEvent actionEvent) {
        DataRequest dataRequest = new DataRequest();
        Map<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("teacherId", GlobalSession.getInstance().getTeacherId());
        dataRequest.add("selection", map);
        DataResponse dataResponse = HttpRequestUtil.request("/api/course/addCourseTeacher", dataRequest);
        if (dataResponse != null) {
            if (dataResponse.getCode() == 0) {
                MessageDialog.showDialog("任课成功!");
                Stage stage = (Stage) num.getScene().getWindow();
                stage.close();
            } else {
                MessageDialog.showDialog(dataResponse.getMsg());
            }
        } else {
            MessageDialog.showDialog("请检查网络连接!");
        }
    }
}
