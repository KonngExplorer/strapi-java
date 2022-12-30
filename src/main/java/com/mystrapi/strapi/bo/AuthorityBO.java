package com.mystrapi.strapi.bo;

import com.mystrapi.strapi.jpa.entity.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author tangqiang
 */
@Data
@AllArgsConstructor
public class AuthorityBO{
    private Authority authority;
}
