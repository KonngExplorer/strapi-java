package com.mystrapi.strapi.web.view.support;

import lombok.Builder;
import lombok.Data;

/**
 * @author tangqiang
 */
@Data
@Builder
public class VerifyCodeView {
    private String img;
    private String verifyCodeToken;
}
