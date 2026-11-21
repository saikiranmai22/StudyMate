package com.studymate.studymate.auth.service;

import com.studymate.studymate.auth.dto.AuthResponse;
import com.studymate.studymate.auth.dto.LoginRequest;
import com.studymate.studymate.auth.dto.RegisterRequest;
import com.studymate.studymate.auth.security.JwtService;
import com.studymate.studymate.user.entity.User;
import com.studymate.studymate.user.entity.UserRole;
import com.studymate.studymate.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //Hashes and verifies passwords
    private final AuthenticationManager authenticationManager; //Authenticates login credentials
    private final JwtService jwtService; //Generates JWT tokens

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.jwtService=jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request){ //RegisterRequest contains the registration details sent by the frontend.
        if(userRepository.existsByEmailIgnoreCase(request.email())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }
        if(userRepository.existsByUsernameIgnoreCase(request.username())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username is already taken"
            );
        }
        String hashedPassword= passwordEncoder.encode(request.password()); //Hash the password
        User user= new User( //Creates a new Java User object.
                request.username(),
                request.email(),
                hashedPassword,
                request.phone(),
                UserRole.STUDENT
        );
        userRepository.save(user); //save the user
        String token=jwtService.generateToken(user.getEmail()); //Generate JWT
        return new AuthResponse(token, "Bearer"); //Returns an AuthResponse containing: The generated JWT and the token type (Bearer).
        //The frontend can now use the token to access protected endpoints.
    }

    public AuthResponse login(LoginRequest request){
        try{
            //AuthenticationManager receives the email and password.
            //It delegates to DaoAuthenticationProvider.
            //DaoAuthenticationProvider calls CustomUserDetailsService.
            //CustomUserDetailsService retrieves the user from MySQL.
            //PasswordEncoder compares the entered password with the stored hash.
            //If they match, authentication succeeds.
            Authentication authentication =authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            String email = authentication.getName();
            String token = jwtService.generateToken(email);//If authentication succeeds, generate a JWT for the user.

            return new AuthResponse(token, "Bearer");

        }catch (AuthenticationException ex){ //We use the same error message for incorrect email and incorrect password. This avoids revealing whether a particular email is registered.
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

    }
}
