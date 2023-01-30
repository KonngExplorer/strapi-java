package com.mystrapi.strapi.system.bo;

import com.mystrapi.strapi.persistance.entity.strapi.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tangqiang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentBO {

    private Document document;

}
