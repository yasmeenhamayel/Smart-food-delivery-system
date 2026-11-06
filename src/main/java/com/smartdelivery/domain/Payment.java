package com.smartdelivery.domain;

public class Payment {
	private double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    
    public Payment(double amount, PaymentMethod method) {

        if (amount <= 0 || !Double.isFinite(amount)) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        if (method == null) {
            throw new IllegalArgumentException("Null payment method");
        }

        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }
    
    
    public double getAmount() {
        return amount;
    }
    
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    
    public void completePayment() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment already processed");
        }
        status = PaymentStatus.COMPLETED;
    }
    
    
    public void failPayment() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment already processed");
        }

        status = PaymentStatus.FAILED;
    }
    
    



}
