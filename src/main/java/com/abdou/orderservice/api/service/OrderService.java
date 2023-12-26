package com.abdou.orderservice.api.service;

import com.abdou.orderservice.api.common.Payment;
import com.abdou.orderservice.api.common.TransactionRequest;
import com.abdou.orderservice.api.common.TransactionResponse;
import com.abdou.orderservice.api.entity.Order;
import com.abdou.orderservice.api.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private RestTemplate template;


    public TransactionResponse saveOrder(TransactionRequest request) {
        String response = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());


        Payment paymentResponse = template.postForObject("http://localhost:9191/payment/doPayment", payment, Payment.class );

        response = paymentResponse.getPaymentStatus().equals("success")?"payment processing successful and order placed":"there is a failure in payment api, order added to cart";
        repository.save(order);
        return new TransactionResponse(order, paymentResponse.getAmount(),paymentResponse.getTransationId(), response);
    }
}
