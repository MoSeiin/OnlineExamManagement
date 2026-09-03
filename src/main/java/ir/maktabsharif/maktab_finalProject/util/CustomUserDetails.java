package ir.maktabsharif.maktab_finalProject.util;

import ir.maktabsharif.maktab_finalProject.domain.Person;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String UserCode;

    public CustomUserDetails(Long id,
                             String username,
                             String password,
                             String UserCode,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.UserCode = UserCode;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public String getUserCode() {
        return UserCode;
    }

    public static CustomUserDetails fromPerson(Person person,
                                               Collection<? extends GrantedAuthority> authorities) {
        return new CustomUserDetails(
                person.getId(),
                person.getUserName(),
                person.getPassword(),
                person.getUserCode(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
