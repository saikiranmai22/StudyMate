package com.studymate.studymate.auth.security;

import com.studymate.studymate.user.entity.User;
import com.studymate.studymate.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
//This class is responsible for finding a user in our MySQL database when Spring Security needs to authenticate them.
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository; //We need UserRepository because we want to search for users in MySQL.

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Override
    //Spring Security calls this method when it needs to retrieve a user for authentication
    //We implement this interface to tell Spring Security how to find users in our database.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user= userRepository.findByEmailIgnoreCase(email).orElseThrow(()->
                new UsernameNotFoundException("User not found"));
        //If the Optional contains a User, return that User.
        //If it is empty, throw UsernameNotFoundException.
        return new CustomUserDetails(user);
        //We pass the retrieved User entity into that class.
        //It wraps the entity and provides Spring Security with:
        //- Email through getUsername().
        //- Password hash through getPassword().
        //- Role through getAuthorities().
        //That's why our method returns UserDetails rather than User.
    }


}
