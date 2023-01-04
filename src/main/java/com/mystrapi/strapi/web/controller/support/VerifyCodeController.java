package com.mystrapi.strapi.web.controller.support;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 验证码
 *
 * @author tangqiang
 */
@Controller
@RequestMapping("/code")
public class VerifyCodeController {

    @RequestMapping("/img")
    @ResponseBody
    public String img(HttpSession session) {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(50, 100);
        String imgBase64Data = captcha.getImageBase64Data();
        session.setAttribute("verifyCode", captcha.getCode());
        return imgBase64Data;
    }

}
