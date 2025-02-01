package com.example.demo.controller;

import com.example.demo.dtos.LoginUserDto;
import com.example.demo.dtos.RegisterUserDto;
import com.example.demo.model.User;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserDto user) {
        User newUser = userService.signup(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserDto user, HttpServletResponse response) {
        User authenticatedUser = userService.authenticate(user);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        Cookie cookie = new Cookie("JWT", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)jwtService.getExpirationTime());

        response.addCookie(cookie);

        return ResponseEntity.ok("User logged in successfully, JWT token set as cookie.");
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ResponseEntity<?> unsupportedRegisterMethod() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("GET method is not supported for /register"));
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<?> unsupportedLoginMethod() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse("GET method is not supported for /login"));
    }

    // Create an ErrorResponse class to structure the error message
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        
    }

}

