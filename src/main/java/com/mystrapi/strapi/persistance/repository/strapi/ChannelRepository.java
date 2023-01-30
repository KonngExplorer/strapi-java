package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangqiang
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    /**
     * 根据栏目id集合查询对应的栏目列表
     * @param channelIds 栏目id集合
     * @return 栏目列表
     */
    List<Channel> findChannelsByIdIn(List<Long> channelIds);
}
