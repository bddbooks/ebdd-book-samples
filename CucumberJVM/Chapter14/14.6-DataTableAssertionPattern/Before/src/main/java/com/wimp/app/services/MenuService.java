/*
 * NOTE: This application ("WIMP - Where Is My Pizza") is provided solely to
 * demonstrate the Behavior-Driven Development scenario automation patterns
 * described in the book "Effective Behavior-Driven Development" by
 * Gaspar Nagy and Seb Rose.
 *
 * It is NOT a complete or production-ready implementation. It deliberately
 * uses shortcuts and simplifications (e.g. authentication, data storage,
 * error handling, security) that are NOT suitable for a real application.
 * Do not use this code as a basis for production software.
 */

package com.wimp.app.services;

import com.wimp.app.infrastructure.DataRepository;
import com.wimp.app.models.MenuItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MenuService {
    private final DataRepository repository;

    public MenuService(DataRepository repository) {
        this.repository = repository;
    }

    public void addMenuItem(String name, BigDecimal price, int calories, String ingredients, boolean vegetarian) {
        var menuItem = new MenuItem(name, price, calories, ingredients, vegetarian);
        repository.saveMenuItem(menuItem);
    }

    public List<MenuItem> getItems() {
        return repository.getMenuItems().stream()
            .sorted(Comparator.comparing(MenuItem::name))
            .toList();
    }

    public List<MenuItem> getFilteredItems(BigDecimal minPrice, BigDecimal maxPrice, Integer maxCalories) {

        var filtered = new ArrayList<>(getItems());
        if (minPrice != null) {
            filtered.removeIf(item -> item.price().compareTo(minPrice) < 0);
        }
        if (maxPrice != null) {
            filtered.removeIf(item -> item.price().compareTo(maxPrice) > 0);
        }
        if (maxCalories != null) {
            filtered.removeIf(item -> item.calories() > maxCalories);
        }
        return filtered;
    }
}
