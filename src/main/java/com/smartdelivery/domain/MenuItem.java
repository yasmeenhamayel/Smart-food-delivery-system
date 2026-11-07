package com.smartdelivery.domain;

public class MenuItem {

    private String name;
    private double price;
    private boolean available;
    private double weight;

    // Backward compatibility with previous sprints
    public MenuItem(String name, double price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
        this.weight = 0.0;
    }

    // Constructor used for items with a specified weight
    public MenuItem(String name, double price, boolean available, double weight) {

        if (weight <= 0 || !Double.isFinite(weight)) {
            throw new IllegalArgumentException("Weight must be positive and finite");
        }

        this.name = name;
        this.price = price;
        this.available = available;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public double getWeight() {
        return weight;
    }
}