package com.example.userapp.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticularRegisterRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
}
