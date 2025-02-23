package com.teach.javafx.controller.base;

import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateStringConverter 实践转换工具类，支持DatePicker的使用
 */
public class IntegerStringConverter extends StringConverter<Integer> {
    private String pattern = "yyyy-MM-dd";
    private DateTimeFormatter dtFormatter;
    public IntegerStringConverter() {
        dtFormatter = DateTimeFormatter.ofPattern(pattern);
    }
    public IntegerStringConverter(String pattern) {
        this.pattern = pattern;
        dtFormatter = DateTimeFormatter.ofPattern(pattern);
    }
    @Override
    public Integer fromString(String text) {
        Integer value = null;
        if (text != null && !text.trim().isEmpty()) {
            value = Integer.parseInt(text);
        }
        return value;
    }
    @Override
    public String toString(Integer value) {
        String text = null;
        if (value != null) {
            text = value.toString();
        }
        return text;
    }
}
