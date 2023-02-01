package com.mystrapi.strapi.system.service;

import com.mystrapi.strapi.persistance.entity.strapi.Channel;
import com.mystrapi.strapi.persistance.repository.strapi.SiteRepository;
import com.mystrapi.strapi.system.bo.SiteBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tangqiang
 */
@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final ChannelService channelService;

    /**
     * 获取站点列表
     *
     * @return List<SiteView>
     */
    public List<SiteBO> findSiteList() {
        return siteRepository.findAll().stream().map(site -> SiteBO.builder()
                .site(site)
                .channelBOList(channelService.findChannelsByIdIn(site.getChannels().stream().map(Channel::getId).toList()))
                .build()
        ).toList();
    }

}
