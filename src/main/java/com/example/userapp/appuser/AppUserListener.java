package com.example.userapp.appuser;

import jakarta.persistence.PrePersist;

public class AppUserListener {


    @PrePersist
    public void setInitialBalance(AppUser user) {
        user.setBalance(0.0);

    }
}
