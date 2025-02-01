package com.example.demo.dtos;

import lombok.*;

@Getter
@Setter
public class RegisterUserDto {
    private String email;

    private String password;

    private String username;
}
