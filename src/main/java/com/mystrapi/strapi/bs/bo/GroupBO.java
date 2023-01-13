package com.mystrapi.strapi.bs.bo;

import com.mystrapi.strapi.persistance.entity.strapi.Group;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author tangqiang
 */
@Data
@Builder
public class GroupBO {
    private Group group;
    private List<UserBO> userBOList;
    private List<AuthorityBO> authorityBOList;
}
