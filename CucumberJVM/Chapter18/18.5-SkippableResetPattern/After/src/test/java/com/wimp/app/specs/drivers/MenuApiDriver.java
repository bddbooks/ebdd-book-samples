package com.wimp.app.specs.drivers;

import com.wimp.app.models.MenuFilter;
import com.wimp.app.models.MenuItem;
import com.wimp.app.models.OfferedMenuItem;
import com.wimp.app.specs.support.LambdaAction;
import com.wimp.app.specs.support.TestAction;
import com.wimp.app.specs.support.VoidReturn;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MenuApiDriver {
    private final MenuApiClient menuApiClient;

    @HttpExchange("/api/menu")
    public interface MenuApiClient {
        @GetExchange
        OfferedMenuItem[] loadMenu(
            @RequestParam(required = false) String promo,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer maxCalories);

        @PostExchange
        void addMenuItem(@RequestBody MenuItem menuItem);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MenuApiDriver(MenuApiClient menuApiClient) {
        this.menuApiClient = menuApiClient;
    }

    public TestAction<List<OfferedMenuItem>> loadMenu() {
        return loadMenu(null, null);
    }

    public TestAction<List<OfferedMenuItem>> loadMenu(String promotionName, MenuFilter filter) {
        return new LambdaAction<>("Load menu", new Object[]{promotionName, filter}, () -> List.of(
            menuApiClient.loadMenu(
                promotionName,
                filter == null ? null : filter.getMinPrice(),
                filter == null ? null : filter.getMaxPrice(),
                filter == null ? null : filter.getMaxCalories())));
    }

    public TestAction<VoidReturn> addMenuItem(MenuItem menuItem) {
        return new LambdaAction.Void("Add menu item", menuItem, () -> menuApiClient.addMenuItem(menuItem));
    }
}
