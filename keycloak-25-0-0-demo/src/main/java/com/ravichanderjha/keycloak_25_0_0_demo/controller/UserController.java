package com.ravichanderjha.keycloak_25_0_0_demo.controller;

import com.ravichanderjha.keycloak_25_0_0_demo.record.UserRecord;
import com.ravichanderjha.keycloak_25_0_0_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody UserRecord userRecord){
        userService.createUser(userRecord);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
