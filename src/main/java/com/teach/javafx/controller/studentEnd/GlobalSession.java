package com.teach.javafx.controller.studentEnd;

public class GlobalSession {

    private static GlobalSession instance;
    private String personId;
    private String num;

    private GlobalSession() {}

    public static synchronized GlobalSession getInstance() {
        if (instance == null) {
            instance = new GlobalSession();
        }
        return instance;
    }

    /**
     * getter and setter
     */

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
