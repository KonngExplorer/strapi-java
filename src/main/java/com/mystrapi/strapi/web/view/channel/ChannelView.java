package com.mystrapi.strapi.web.view.channel;

import lombok.Builder;
import lombok.Data;

/**
 * @author tangqiang
 */
@Data
@Builder
public class ChannelView {
    private Long id;
    private String siteName;
    private String zhName;
    private String description;
}
