package com.mystrapi.strapi.web.controller.support;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import com.mystrapi.strapi.web.view.ViewResult;
import com.mystrapi.strapi.web.view.support.VerifyCodeView;
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
    public ViewResult<VerifyCodeView> img(HttpSession session) {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40);
        String imgBase64Data = captcha.getImageBase64Data();
        String verifyCodeToken = IdUtil.fastSimpleUUID();
        session.setAttribute(verifyCodeToken, captcha.getCode());
        return ViewResult.success(VerifyCodeView.builder()
                .img(imgBase64Data)
                .verifyCodeToken(verifyCodeToken)
                .build());
    }

}
