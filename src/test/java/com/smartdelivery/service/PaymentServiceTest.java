package com.smartdelivery.service;

import com.smartdelivery.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {
	private final PaymentService service = new PaymentService();

    @Test
    void processesCashPaymentSuccessfully() {
        Payment payment = service.processPayment(100, PaymentMethod.CASH);

        assertNotNull(payment);
        assertEquals(100, payment.getAmount());
        assertEquals(PaymentMethod.CASH, payment.getMethod());
    }

    @Test
    void recordsPaymentStatusAsCompleted() {
        Payment payment = service.processPayment(100, PaymentMethod.CASH);

        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }

    @Test
    void rejectsZeroPaymentAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> service.processPayment(0, PaymentMethod.CASH));
    }

    @Test
    void rejectsNegativePaymentAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> service.processPayment(-10, PaymentMethod.CASH));
    }

    @Test
    void rejectsNonFinitePaymentAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> service.processPayment(Double.NaN, PaymentMethod.CASH));

        assertThrows(IllegalArgumentException.class,
                () -> service.processPayment(Double.POSITIVE_INFINITY, PaymentMethod.CASH));
    }

    @Test
    void rejectsNullPaymentMethod() {
        assertThrows(IllegalArgumentException.class,
                () -> service.processPayment(100, null));
    }



}
