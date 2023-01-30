package com.mystrapi.strapi.system.service;

import com.mystrapi.strapi.persistance.repository.strapi.ChannelRepository;
import com.mystrapi.strapi.system.bo.ChannelBO;
import com.mystrapi.strapi.system.bo.DocumentBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * @author tangqiang
 */
@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final DocumentService documentService;

    public ChannelBO findChannelById(Long channelId) {
        Optional<ChannelBO> channelBoOptional = channelRepository.findById(channelId).map(channel -> ChannelBO.builder().channel(channel).build()).map(channelBO -> {
            List<DocumentBO> documentBOList = documentService.findDocumentByChannelId(channelId);
            channelBO.setDocumentBOList(documentBOList);
            return channelBO;
        });
        return channelBoOptional.orElse(null);
    }

    public List<ChannelBO> findChannelsByIdIn(List<Long> channelIds) {
        return channelRepository.findChannelsByIdIn(channelIds).stream()
                .map(channel -> ChannelBO.builder()
                        .channel(channel)
                        .documentBOList(channel.getDocuments().stream()
                                .map(document -> DocumentBO.builder().document(document).build())
                                .toList())
                        .build()).toList();
    }

}
