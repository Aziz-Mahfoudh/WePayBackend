package com.example.userapp.appuser;

import com.example.userapp.payment.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@EntityListeners(BusinessUserListener.class)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BusinessUser extends AppUser{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String storeName;
    private String industry;
    private String phone;
    private String accountIdentifier;
    @OneToMany(mappedBy = "beneficiary")
    private List<Payment> paymentList;
    public BusinessUser(String email,
                        String password,
                        AppUserRole appUserRole,
                        AccountType accountType,
                        Boolean locked,
                        Boolean enabled,
                        String storeName,
                        String industry,
                        Double balance,
                        String phone) {
        super(email, password, appUserRole, accountType, locked, enabled, balance);
        this.storeName = storeName;
        this.industry = industry;
        this.phone = phone;
        this.accountIdentifier = UUID.randomUUID().toString();
    }

}
