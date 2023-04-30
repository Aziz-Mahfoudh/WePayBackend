package com.example.userapp.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryResponse {

    @JsonProperty("StoreName")
    private String storeName;
    @JsonProperty("Date")
    private String date;
    @JsonProperty("Amount")
    private Double amount;
    @JsonProperty("Currency")
    private String currency;


}
