package com.example.userapp.payment;

import com.example.userapp.appuser.BusinessUser;
import com.example.userapp.appuser.ParticularUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.text.SimpleDateFormat;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_payment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "beneficiary", "payer"})
public class Payment {

    @Id
    @GeneratedValue
    private Integer id;
    private String paymentId;
    private String originId;
    @ManyToOne
    @JoinColumn(name = "beneficiary_id")
    private BusinessUser beneficiary;
    @ManyToOne
    @JoinColumn(name = "payer_id")
    private ParticularUser payer;
    private String timeStamp;
    private Double total;
    private String currency;
    private String method;
    private String cancelUrl;
    private String successUrl;
    private PaymentStatus paymentStatus;


    public Payment(BusinessUser beneficiary,
                   ParticularUser payer,
                   String paymentId,
                   String originId,
                   Double total,
                   String currency,
                   String method,
                   String cancelUrl,
                   String successUrl,
                   PaymentStatus paymentStatus) {
        this.beneficiary = beneficiary;
        this.payer = payer;
        this.paymentId = paymentId;
        this.originId = originId;
        this.total = total;
        this.currency = currency;
        this.method = method;
        this.cancelUrl = cancelUrl;
        this.successUrl = successUrl;
        this.paymentStatus = paymentStatus;

    }

    @PrePersist
    public void setTimestamp() {
        this.timeStamp = new SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date());

    }


}
