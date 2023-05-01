package com.example.userapp.appuser;

import jakarta.persistence.PrePersist;

import java.util.UUID;

public class ParticularUserListener {

    @PrePersist
    public void setUUID(ParticularUser user) {
        user.setAccountIdentifier(UUID.randomUUID().toString());
    }
}
