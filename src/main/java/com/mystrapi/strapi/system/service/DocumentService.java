package com.mystrapi.strapi.system.service;


import com.mystrapi.strapi.persistance.repository.strapi.DocumentRepository;
import com.mystrapi.strapi.system.bo.DocumentBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tangqiang
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    public List<DocumentBO> findDocumentByChannelId(Long channelId) {
        return documentRepository.findTop1000ByChannelIdOrderByIdDesc(channelId)
                .stream()
                .map(document -> DocumentBO.builder().document(document).build())
                .toList();
    }

}
