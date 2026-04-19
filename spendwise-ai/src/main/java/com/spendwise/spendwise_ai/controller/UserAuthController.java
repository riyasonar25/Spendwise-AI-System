package com.spendwise.spendwise_ai.controller;

import com.spendwise.spendwise_ai.dto.AuthRequest;
import com.spendwise.spendwise_ai.service.UserAuthService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody AuthRequest request) {

        userAuthService.register(
                request.name,
                request.email,
                request.password
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");

        return response;
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest request) {

        String token = userAuthService.login(
                request.email,
                request.password
        );

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return response;
    }
}