
package com.smartdelivery.domain;

public class Restaurant {

    private String name;
    private boolean open;

    public Restaurant(String name, boolean open) {
        this.name = name;
        this.open = open;
    }

    public String getName() {
        return name;
    }

    public boolean isOpen() {
        return open;
    }
}
