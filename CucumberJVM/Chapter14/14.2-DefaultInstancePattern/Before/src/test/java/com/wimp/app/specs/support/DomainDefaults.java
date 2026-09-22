package com.wimp.app.specs.support;

import com.wimp.app.models.PizzaStyle;

public final class DomainDefaults {
    public static final String CUSTOMER_NAME = "Rebecca";
    public static final String CUSTOMER_EMAIL = "rebecca@example.com";
    public static final String CUSTOMER_ADDRESS = "2850 Piedmont Ave NE, Apt 3C, Atlanta, GA 30308";
    public static final String ALT_DELIVERY_ADDRESS = "191 Peachtree St NE, Suite 6200, Atlanta, GA 30303";

    public static final String PIZZA_NAME = "Margherita";
    public static final String PIZZA_SIZE = "Medium";
    public static final PizzaStyle PIZZA_STYLE = PizzaStyle.REGULAR;

    private DomainDefaults() {
    }
}
