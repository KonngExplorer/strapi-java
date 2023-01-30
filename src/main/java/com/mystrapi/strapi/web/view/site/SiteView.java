package com.mystrapi.strapi.web.view.site;

import lombok.Builder;
import lombok.Data;

/**
 * @author tangqiang
 */
@Data
@Builder
public class SiteView {
    private Long id;
    private String siteName;
    private String zhName;
    private String description;
}
