
package com.smartdelivery.domain;
import java.util.*;

public class Restaurant {

    private String name;
    private boolean open;
    private double minOrderValue;
    private Set<String> deliveryAreas;

    public Restaurant(String name, boolean open , double min , Set<String> deliveryAreas) {
        if(name == null) {
        	throw new IllegalArgumentException("Null Name");
        }
        if(name.isBlank()) {
        	throw new IllegalArgumentException("Blank Name");
        }
        this.name = name.strip();
        this.open = open;
        
        if(min < 0 || !Double.isFinite(min)) {
		    throw new IllegalArgumentException("The Min Order Value is not correct");
        }
        minOrderValue = min;
        
        if(deliveryAreas == null) {
		    throw new IllegalArgumentException("Null delivery Area");
        }
        if(deliveryAreas.isEmpty()) {
		    throw new IllegalArgumentException("There is no delivery area");
        }
        Set<String> cleanAreas = new HashSet<>();
        for (String area : deliveryAreas) {
        	if(area== null) {
    		    throw new IllegalArgumentException("There is a null area");
        	}
        	if(area.isBlank()) {
    		    throw new IllegalArgumentException("There is  a Blank area");
        	}
        	String cleanArea = area.strip();
        	cleanAreas.add(cleanArea);
        	}
        this.deliveryAreas = cleanAreas;
        }

    
   
    public String getName() {
        return name;
    }

    public boolean isOpen() {
        return open;
    }
    
    public double getMinOrderValue() {
    	return minOrderValue;
    }
    
    public Set<String> getDeliveryAreas(){
    	return Collections.unmodifiableSet(deliveryAreas);
    }
}
