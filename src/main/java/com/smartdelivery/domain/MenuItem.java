
package com.smartdelivery.domain;

public class MenuItem {

    private String name;
    private double price;
    private boolean available;

    public MenuItem(String name, double price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
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
}
