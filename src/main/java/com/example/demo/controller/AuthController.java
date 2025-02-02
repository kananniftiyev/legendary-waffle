package com.example.demo.controller;

import com.example.demo.dtos.LoginUserDto;
import com.example.demo.dtos.RegisterUserDto;
import com.example.demo.model.User;
import com.example.demo.service.JwtService;
import com.example.demo.service.TokenBlacklistService;
import com.example.demo.service.UserService;
import com.example.demo.configs.JwtAuthenticationFilter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    private final TokenBlacklistService tokenBlacklistService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(TokenBlacklistService tokenBlacklistService, UserService userService, JwtService jwtService) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto user) {
        return userService.signup(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto user, HttpServletResponse response) {
        User authenticatedUser = userService.authenticate(user);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        Cookie cookie = new Cookie("JWT", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)jwtService.getExpirationTime());

        response.addCookie(cookie);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .body(Map.of("token", jwtToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user, HttpServletRequest request, HttpServletResponse response) {
        String jwt = getJwtFromCookies(request);
        if (jwt != null) {

            tokenBlacklistService.blacklistToken(jwt);

            Cookie cookie = new Cookie("JWT", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));

        }

        return ResponseEntity.ok(Map.of("message", "user not logged in."));

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

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }


    }

    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> "JWT".equals(cookie.getName()))
                    .findFirst();
            return jwtCookie.map(Cookie::getValue).orElse(null);
        }
        return null;
    }

}

