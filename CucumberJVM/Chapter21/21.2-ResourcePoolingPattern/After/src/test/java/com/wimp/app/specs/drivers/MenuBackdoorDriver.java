package com.wimp.app.specs.drivers;

import com.wimp.app.data.DataRepository;
import com.wimp.app.models.MenuItem;
import com.wimp.app.specs.support.DomainDefaults;
import com.wimp.app.specs.support.MenuItemData;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
@Transactional
public class MenuBackdoorDriver {
    private final DataRepository dataRepository;

    public MenuBackdoorDriver(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void setMenuItems(List<MenuItemData> menuItemDataList) {
        List<MenuItem> itemsToSet = menuItemDataList.stream()
            .map(MenuBackdoorDriver::toMenuItem)
            .toList();
        dataRepository.setMenuItems(itemsToSet);
    }

    public void addMenuItem(MenuItemData menuItemData) {
        dataRepository.insertMenuItem(toMenuItem(menuItemData));
    }

    private static MenuItem toMenuItem(MenuItemData item) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(item.name());
        menuItem.setPrice(item.price() != null ? item.price() : DomainDefaults.PIZZA_PRICE);
        menuItem.setCalories(item.calories() != null ? item.calories() : DomainDefaults.PIZZA_CALORIES);
        menuItem.setVegetarian(item.vegetarian());
        return menuItem;
    }

    public List<MenuItem> getMenuItems() {
        return dataRepository.getMenuItems().stream()
            .sorted(Comparator.comparing(MenuItem::getName))
            .toList();
    }
}
