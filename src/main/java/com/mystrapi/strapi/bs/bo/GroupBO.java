package com.mystrapi.strapi.bs.bo;

import com.mystrapi.strapi.persistance.entity.strapi.Group;
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
public class GroupBO {
    private Group group;
    private List<UserBO> userBOList;
    private List<AuthorityBO> authorityBOList;
}
