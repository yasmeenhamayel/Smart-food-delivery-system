
package com.smartdelivery.service;

import com.smartdelivery.domain.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuService {

    public List<MenuItem> getAvailableItems(List<MenuItem> items) {
        List<MenuItem> availableItems = new ArrayList<>();

        for (MenuItem item : items) {
            if (item.isAvailable()) {
                availableItems.add(item);
            }
        }

        return availableItems;
    }
}
