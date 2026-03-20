package net.breezeware.Spring_Boot_Cafeteria.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserRequestDto;
import net.breezeware.Spring_Boot_Cafeteria.user.dto.UserResponseDto;
import net.breezeware.Spring_Boot_Cafeteria.user.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



    @Slf4j
    @RestController
    @RequestMapping("/api/Admin")
    @RequiredArgsConstructor
    public class AdminUserController {

        private final AdminUserService adminUserService;

        @PostMapping("/register")
        public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto request) {
            log.info("Registering the Admin user ");
            UserResponseDto response = adminUserService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PostMapping("/login")
        public ResponseEntity<UserResponseDto> login(@RequestBody UserRequestDto request) {
            log.info("Logging in with mail and password ");
            UserResponseDto response = adminUserService.login(request);
            return ResponseEntity.ok(response);
        }

    }
