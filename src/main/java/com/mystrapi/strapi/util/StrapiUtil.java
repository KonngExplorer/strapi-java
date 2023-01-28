package com.mystrapi.strapi.util;

import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;

/**
 * 工具类
 * @author tangqiang
 */
public class StrapiUtil {

    /**
     * 设置http响应类型为ajax类型
     * @param response HttpServletResponse
     */
    public static void setAjaxResponse(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=utf-8");
    }

}
