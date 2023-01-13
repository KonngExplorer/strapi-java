package com.mystrapi.strapi.bs.bo;

import com.mystrapi.strapi.persistance.entity.strapi.Authority;
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
public class AuthorityBO{
    private Authority authority;
}
