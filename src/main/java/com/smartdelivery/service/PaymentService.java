package com.smartdelivery.service;
import com.smartdelivery.domain.*;

public class PaymentService {
	
	 public Payment processPayment(double amount, PaymentMethod method) {

	        Payment payment = new Payment(amount, method);

	        payment.completePayment();

	        return payment;
	    }

}
