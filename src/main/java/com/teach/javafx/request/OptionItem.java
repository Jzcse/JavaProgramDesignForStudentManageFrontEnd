package com.teach.javafx.request;

import com.teach.javafx.util.CommonMethod;

import java.util.Map;

/**
 * OptionItem 选项数据类
 * Integer id  数据项id
 * String value 数据项值
 * String label 数据值标题
 */
public class OptionItem {
    private Integer id;
    private String value;
    private String title;

    public OptionItem(){

    }
    public OptionItem(Integer id, String value, String title){
        this.id = id;
        this.value = value;
        this.title = title;
    }
    public OptionItem(Map map){
        this.id = CommonMethod.getInteger(map,"id");
        this.value = CommonMethod.getString(map,"value");
        this.title = CommonMethod.getString(map,"title");
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString(){
        return title;
    }
}
