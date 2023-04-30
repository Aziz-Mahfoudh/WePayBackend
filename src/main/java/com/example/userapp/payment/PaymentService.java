package com.example.userapp.payment;

import com.example.userapp.appuser.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BusinessRepository businessRepository;
    private final ParticularRepository particularRepository;
    private final PaymentRepository paymentRepository;


    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);


    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        String paymentId = UUID.randomUUID().toString();
        BusinessUser businessUser = businessRepository.findBusinessUserById(paymentRequest.getBeneficiaryId());

        if (businessUser != null) {
            logger.info("Found payment");
        }

        Payment payment = Payment.builder()
                .beneficiary(businessUser)
                .paymentId(paymentId)
                .total(paymentRequest.getTotal())
                .currency(paymentRequest.getCurrency())
                .method(paymentRequest.getMethod())
                .cancelUrl(paymentRequest.getCancelUrl())
                .successUrl(paymentRequest.getSuccessUrl())
                .paymentStatus(paymentRequest.getPaymentStatus())
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(paymentId)
                .build();
    }

    public PaymentExecutionResponse executePayment(PaymentExecutionRequest request) {
        Payment payment = paymentRepository.findByPaymentId(request.getPaymentId());
        if (payment != null) {
            logger.info("Found payment");
        }
        ParticularUser payer = extractUserByUsername(request.getUsername());
        if (payer != null) {
            logger.info("Found payer");
        }

        if (!isAbleToPay(payer, payment)) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            return PaymentExecutionResponse.builder()
                    .paymentId(request.getPaymentId())
                    .status("FAILED")
                    .build();
        }

        payment.setPayer(payer);
        payment.setPaymentStatus(PaymentStatus.SUCCEED);
        paymentRepository.save(payment);
        correctBalances(payment);

        return PaymentExecutionResponse.builder()
                .paymentId(request.getPaymentId())
                .status("SUCCEED")
                .build();
    }

    public void correctBalances(Payment payment) {
        ParticularUser particularUser = payment.getPayer();
        BusinessUser businessUser = payment.getBeneficiary();
        particularUser.setBalance(particularUser.getBalance() - payment.getTotal());
        businessUser.setBalance(businessUser.getBalance() + payment.getTotal());

        particularRepository.save(particularUser);
        businessRepository.save(businessUser);
    }

    public boolean isAbleToPay(ParticularUser payer, Payment payment) {
        return !(payer.getBalance() < payment.getTotal());
    }

    public ParticularUser extractUserByUsername(String username) {
        ParticularUser user = particularRepository.findByEmail(username);
        if (user != null) {
            logger.info("Found user");
        }

        return user;
    }
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId);
    }

    public ListPaymentsResponse getAllPaymentsByPayerId(Integer id) {
        return ListPaymentsResponse.builder()
                .paymentList(paymentRepository.findPaymentsByPayerId(id))
                .build();
    }

     public List<PaymentHistoryResponse> getAllPaymentsByPayerIdForHistory(Integer id) {
        List<Payment> payments = paymentRepository.findPaymentsByPayerId(id);
        List<PaymentHistoryResponse> paymentHistoryResponseList = new ArrayList<>();
        for(Payment payment : payments) {
            var businessUser = payment.getBeneficiary();
            if (businessUser == null) {
                logger.info("Business user is null");
            }
            PaymentHistoryResponse paymentHistoryResponse = PaymentHistoryResponse.builder()
                    .amount(payment.getTotal())
                    .storeName(businessUser.getStoreName())
                    .date(payment.getTimeStamp())
                    .currency(payment.getCurrency())
                    .build();
            paymentHistoryResponseList.add(paymentHistoryResponse);
        }
        return paymentHistoryResponseList;
    }



    public List<Payment> getAllPaymentsByPayerEmail(String email) {
        return paymentRepository.findPaymentsByPayer(email);
    }


    public String deleteAllPayments() {
        paymentRepository.deleteAll();
        return "Done";
    }
}
