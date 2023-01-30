package com.mystrapi.strapi.persistance.repository.strapi;

import com.mystrapi.strapi.persistance.entity.strapi.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tangqiang
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {

    /**
     * 查找栏目下的文档
     * @param channelId 栏目id
     * @return 对应栏目id下的文档列表（最多取1000条）
     */
    List<Document> findTop1000ByChannelIdOrderByIdDesc(Long channelId);

}
