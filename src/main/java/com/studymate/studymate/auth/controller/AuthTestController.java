package com.studymate.studymate.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {
    @GetMapping("/api/test/protected")
    public String protectedEndpoint(){
        return "You are authenticated! ";
    }
}
