package com.example.userapp.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    Integer beneficiaryId;
    Double total;
    String currency;
    String method;
    String cancelUrl;
    String successUrl;
    PaymentStatus paymentStatus = PaymentStatus.PENDING;
}
