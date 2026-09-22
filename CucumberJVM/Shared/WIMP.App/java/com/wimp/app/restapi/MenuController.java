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

package com.wimp.app.restapi;

import com.wimp.app.models.MenuFilter;
import com.wimp.app.models.MenuItem;
import com.wimp.app.models.OfferedMenuItem;
import com.wimp.app.models.ServiceResult;
import com.wimp.app.services.AuthenticationService;
import com.wimp.app.services.MenuService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
@Transactional
public class MenuController {
    private final MenuService menuService;
    private final AuthenticationService authenticationService;

    public MenuController(MenuService menuService, AuthenticationService authenticationService) {
        this.menuService = menuService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public List<OfferedMenuItem> getMenu(
        @RequestParam(required = false) String promo,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) Integer maxCalories
    ) {
        MenuFilter filter = null;
        if (minPrice != null || maxPrice != null || maxCalories != null) {
            filter = new MenuFilter();
            filter.setMinPrice(minPrice);
            filter.setMaxPrice(maxPrice);
            filter.setMaxCalories(maxCalories);
        }

        return menuService.loadMenu(promo == null ? "" : promo, filter);
    }

    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(
        @RequestBody MenuItem menuItem,
        @RequestHeader("Authorization") String authorization
    ) {
        String customerName = requireOwner(authorization);
        if (!customerName.startsWith("Owner")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        ServiceResult<MenuItem> result = menuService.addMenuItem(menuItem);
        if (!result.successful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.errorMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result.value());
    }

    private String requireOwner(String authorization) {
        String token = authenticationService.extractToken(authorization);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        String customerName = authenticationService.getCustomerName(token);
        if (customerName == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The request is not authorized for this operation");
        }

        return customerName;
    }
}
