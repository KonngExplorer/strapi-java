package com.mystrapi.strapi.system.bo;

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

    public String toString() {
        return "GroupBO(group=" + this.getGroup() + ")";
    }

}
