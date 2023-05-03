package com.example.userapp.appuser;

import com.example.userapp.payment.Payment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@EntityListeners(ParticularUserListener.class)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "tokens", "paymentList"})
public class ParticularUser extends AppUser{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String phone;
    private String accountIdentifier;
    @OneToMany(mappedBy = "payer")
    private List<Payment> paymentList;
    public ParticularUser(String email,
                          String password,
                          AppUserRole appUserRole,
                          AccountType accountType,
                          Boolean locked,
                          Boolean enabled,
                          String firstName,
                          String lastName,
                          Double balance,
                          String phone) {
        super(email, password, appUserRole, accountType, locked, enabled, balance);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }


}
