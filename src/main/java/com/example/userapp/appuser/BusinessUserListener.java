package com.example.userapp.appuser;

import jakarta.persistence.PrePersist;

import java.util.UUID;

public class BusinessUserListener {

    @PrePersist
    public void setUUID(BusinessUser user) {
        user.setAccountIdentifier(UUID.randomUUID().toString());
    }
}
