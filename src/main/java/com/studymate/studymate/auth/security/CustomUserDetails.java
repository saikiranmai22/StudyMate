package com.studymate.studymate.auth.security;

import com.studymate.studymate.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
//Spring Security needs a standard way to understand your application's users.
//Your User entity is designed for database storage. CustomUserDetails adapts it to the interface Spring Security understands.

public class CustomUserDetails implements UserDetails {
    //UserDetails is an interface provided by Spring Security.
    //It defines methods that Spring Security expects to use when working with an authenticated user.

    private final User user; //final means we cannot reassign user to another User object after the constructor initializes it.
    public CustomUserDetails(User user){ //We store the actual User entity inside this class.
        this.user=user;
    }

    @Override //We use a collection because a user can have multiple authorities.
    public Collection<? extends GrantedAuthority> getAuthorities(){ //This method tells Spring Security which roles or permissions the user has.
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_"+user.getRole().name()
                )
        );
    }

    @Override
    public String getPassword(){ //Spring Security needs the stored password hash to authenticate a user.
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        // Spring Security uses email as the login identifier.
        return user.getEmail();
    }
    //Because our current StudyMate users table doesn't have fields for account expiry, locking, or disabling.
    //So, for now, we're treating every account as active and usable.
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { //Has the password expired?
        return true;
    }

    @Override
    public boolean isEnabled() { //Is the account enabled?
        return true;
    }

    public User getUser() { //It allows other parts of our application to retrieve the original User entity.
        return user;
    }
}
