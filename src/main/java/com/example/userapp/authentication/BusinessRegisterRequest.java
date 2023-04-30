package com.example.userapp.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessRegisterRequest {

    private String storeName;
    private String industry;
    private String phone;
    private String email;
    private String password;

}
