package com.lib.libmansys.controller;

import com.lib.libmansys.config.AuthenticationService;
import com.lib.libmansys.dto.Authentication.AuthenticationRequest;
import com.lib.libmansys.dto.Authentication.AuthenticationResponse;
import com.lib.libmansys.dto.Authentication.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(tags = "Authentication", description = "Sign-up as a new user", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})
    @PostMapping("/register")
    @PermitAll
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authService.register(request));
    }
    @Operation(tags = "Authentication", description = "Login using user credentials", responses = {
            @ApiResponse(description = "Success", responseCode = "200"

            ), @ApiResponse(description = "Data Not Found", responseCode = "404"

    )})

    @PostMapping("/authenticate")
    @PermitAll
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
