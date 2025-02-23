package com.teach.javafx;


import com.teach.javafx.controller.base.MainFrameController;
import com.teach.javafx.request.JwtResponse;

/**
 * 前端应用全程数据类
 * JwtResponse jwt 客户登录信息
 */
public class AppStore {
    private static JwtResponse jwt;
    private static MainFrameController mainFrameController;
    private AppStore(){
    }

    public static JwtResponse getJwt() {
        return jwt;
    }

    public static void setJwt(JwtResponse jwt) {
        AppStore.jwt = jwt;
    }

    public static MainFrameController getMainFrameController() {
        return mainFrameController;
    }

    public static void setMainFrameController(MainFrameController mainFrameController) {
        AppStore.mainFrameController = mainFrameController;
    }
}
