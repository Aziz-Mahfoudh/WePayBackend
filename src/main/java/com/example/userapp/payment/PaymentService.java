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

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ParticularRepository particularRepository;
    private final PaymentRepository paymentRepository;


    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);


    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        String paymentId = generatePaymentId();
        BusinessUser businessUser = businessRepository.findBusinessUserById(paymentRequest.getBeneficiaryId());

        Payment payment = Payment.builder()
                .beneficiary(businessUser)
                .paymentId(paymentId)
                .originId(paymentId)
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

   /** public PaymentExecutionResponse executeSplitPayment(SplitPaymentRequest request) {
        Payment payment = paymentRepository.findByPaymentId(request.getPaymentId());

        List<String> paymentIds = splitPayment(request, payment);
        for(String paymentId : paymentIds) {
            String username = paymentRepository.findByPaymentId(paymentId).get
            PaymentExecutionRequest paymentExecutionRequest = PaymentExecutionRequest.builder()
                            .paymentId(paymentId)
                                    .username()

            executePayment()
        }
    }**/

    public List<String> splitPayment(SplitPaymentRequest request) {
        List<Payment> payments = new ArrayList<>();
        List<String> paymentIds = new ArrayList<>();

        Payment payment = paymentRepository.findByPaymentId(request.getPaymentId());
        for(EmailAndAmount field : request.list) {
            ParticularUser payer = particularRepository.findByEmail(field.getEmail());

            Payment paymentInstance = Payment.builder()
                    .paymentId(generatePaymentId())
                    .originId(payment.getPaymentId())
                    .beneficiary(payment.getBeneficiary())
                    .total(Double.parseDouble(field.getAmount()))
                    .cancelUrl("cancel")
                    .successUrl("success")
                    .method(payment.getMethod())
                    .currency(payment.getCurrency())
                    .paymentStatus(payment.getPaymentStatus())
                    .build();
            paymentRepository.save(paymentInstance);
            paymentIds.add(paymentInstance.getPaymentId());
            logger.info("Sent Email!!!!");


        }
        return paymentIds;
    }


    public String generatePaymentId() {
        return UUID.randomUUID().toString();
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
        AppUser user = userRepository.findAppUserById(id);
         List<Payment> payments = new ArrayList<>();
        if (user.getAccountType() == AccountType.PARTICULAR) {
           payments = paymentRepository.findPaymentsByPayerId(userRepository.findId(user.getEmail()));
         }
        else if (user.getAccountType() == AccountType.BUSINESSS){
            payments = paymentRepository.findPaymentsByBeneficiaryId(userRepository.findId(user.getEmail()));
        }

        List<PaymentHistoryResponse> paymentHistoryResponseList = new ArrayList<>();
         for(Payment payment : payments) {
            var businessUser = payment.getBeneficiary();
            var particularUser = payment.getPayer();
            String name = new String();
            if (user.getAccountType() == AccountType.PARTICULAR) {
                name = businessUser.getStoreName();
             }
            else if (user.getAccountType() == AccountType.BUSINESSS){
                name =  particularUser.getFirstName() + " " + particularUser.getLastName();
            }

             PaymentHistoryResponse paymentHistoryResponse = PaymentHistoryResponse.builder()
                    .amount(payment.getTotal())
                    .storeName(name)
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
