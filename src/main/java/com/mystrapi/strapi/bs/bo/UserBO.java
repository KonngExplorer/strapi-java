package com.mystrapi.strapi.bs.bo;

import com.mystrapi.strapi.persistance.entity.User;
import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tangqiang
 */
@Data
public class UserBO implements UserDetails {

    private User user;
    private List<AuthorityBO> authorityBOList;

    public UserBO(User user, List<AuthorityBO> authorityBOList) {
        this.user = user;
        this.authorityBOList = authorityBOList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityBOList.stream().map(authorityBO ->
                        (GrantedAuthority) () -> authorityBO.getAuthority().getAuth())
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.getEnabled();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull UserBuilder builder() {
        return new UserBuilder();
    }

    public static final class UserBuilder {
        private User user;
        private List<AuthorityBO> authorityBOList;
        private Function<String, String> passwordEncoder = (password) -> password;

        public UserBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserBuilder passwordEncoder(Function<String, String> encoder) {
            this.passwordEncoder = encoder;
            return this;
        }

        public UserBuilder authorityBOList(List<AuthorityBO> authorityBOList) {
            this.authorityBOList = authorityBOList;
            return this;
        }

        public UserBO build() {
            String encodedPassword = this.passwordEncoder.apply(this.user.getPassword());
            user.setPassword(encodedPassword);
            return new UserBO(user, this.authorityBOList);
        }
    }
}
