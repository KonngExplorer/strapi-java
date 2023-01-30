package com.mystrapi.strapi.system.bo;

import com.mystrapi.strapi.persistance.entity.strapi.Channel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author tangqiang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChannelBO {
    private Channel channel;
    private List<DocumentBO> documentBOList;
}
