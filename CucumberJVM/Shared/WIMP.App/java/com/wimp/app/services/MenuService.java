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

import com.wimp.app.data.DataRepository;
import com.wimp.app.models.MenuFilter;
import com.wimp.app.models.MenuItem;
import com.wimp.app.models.OfferedMenuItem;
import com.wimp.app.models.ServiceResult;
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

    public List<OfferedMenuItem> loadMenu() {
        return loadMenu("", null);
    }

    public List<OfferedMenuItem> loadMenu(String promotionName, MenuFilter filter) {
        List<OfferedMenuItem> offeredItems = repository.getMenuItems().stream()
            .sorted(Comparator.comparing(MenuItem::getName))
            .map(this::toOfferedMenuItem)
            .toList();

        if ("Friday $1 off".equals(promotionName)) {
            offeredItems.forEach(item -> {
                if (item.getOriginalPrice().compareTo(BigDecimal.valueOf(8.00)) > 0) {
                    item.setPrice(item.getOriginalPrice().subtract(BigDecimal.ONE));
                }
            });
        } else if ("Veggie week".equals(promotionName)) {
            offeredItems.forEach(item -> {
                if (item.isVegetarian()) {
                    item.setPrice(BigDecimal.valueOf(6.00));
                }
            });
        }

        if (filter == null) {
            return offeredItems;
        }

        List<OfferedMenuItem> filtered = new ArrayList<>(offeredItems);
        if (filter.getMinPrice() != null) {
            filtered.removeIf(item -> item.getPrice().compareTo(filter.getMinPrice()) < 0);
        }
        if (filter.getMaxPrice() != null) {
            filtered.removeIf(item -> item.getPrice().compareTo(filter.getMaxPrice()) > 0);
        }
        if (filter.getMaxCalories() != null) {
            filtered.removeIf(item -> item.getCalories() > filter.getMaxCalories());
        }
        return filtered;
    }

    public ServiceResult<MenuItem> addMenuItem(MenuItem menuItem) {
        repository.insertMenuItem(menuItem);
        return ServiceResult.success(menuItem);
    }

    private OfferedMenuItem toOfferedMenuItem(MenuItem menuItem) {
        OfferedMenuItem offered = new OfferedMenuItem();
        offered.setName(menuItem.getName());
        offered.setPrice(menuItem.getPrice());
        offered.setCalories(menuItem.getCalories());
        offered.setVegetarian(menuItem.isVegetarian());
        offered.setOriginalPrice(menuItem.getPrice());
        return offered;
    }
}
