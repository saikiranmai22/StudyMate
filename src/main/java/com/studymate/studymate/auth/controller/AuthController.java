package com.studymate.studymate.auth.controller;

import com.studymate.studymate.auth.dto.AuthResponse;
import com.studymate.studymate.auth.dto.LoginRequest;
import com.studymate.studymate.auth.dto.RegisterRequest;
import com.studymate.studymate.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") //Sets the common URL prefix for all endpoints in this controller.
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){
        this.authService=authService;
    }

    @PostMapping("/register")
    //@RequestBody converts the incoming JSON into a RegisterRequest object.
    //@Valid triggers the validation annotations in RegisterRequest, such as @NotBlank, @Email and @Size.
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        AuthResponse response= authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        //ResponseEntity lets us control the HTTP response.
        //HttpStatus.CREATED returns 201 Created, indicating successful resource creation.
        //.body(response) sends the AuthResponse containing the JWT.
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        AuthResponse response= authService.login(request); //The service authenticates the user.
        return ResponseEntity.ok(response);
    }
}
