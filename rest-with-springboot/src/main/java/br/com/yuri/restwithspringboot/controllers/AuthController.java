package br.com.yuri.restwithspringboot.controllers;

import br.com.yuri.restwithspringboot.data.vo.v1.security.AccountCredentialsVO;
import br.com.yuri.restwithspringboot.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Endpoint")
@RequestMapping("/auth")
@RestController
public class AuthController {

    @Autowired
    AuthServices service;

    @Operation(summary = "Authenticate a user by credentials and return a valid token")
    @PostMapping(value = "/signin")
    public ResponseEntity signin(@RequestBody AccountCredentialsVO data) {
        if (data == null || data.getUsername() == null || data.getUsername().isBlank() || data.getPassword() == null || data.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        var token = service.signin(data);
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(token);
    }


}
