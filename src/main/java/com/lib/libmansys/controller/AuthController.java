package com.lib.libmansys.controller;

import com.lib.libmansys.config.AuthenticationService;
import com.lib.libmansys.dto.Authentication.AuthenticationRequest;
import com.lib.libmansys.dto.Authentication.AuthenticationResponse;
import com.lib.libmansys.dto.Authentication.RegisterRequest;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/register")
    @PermitAll
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    @PermitAll
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
