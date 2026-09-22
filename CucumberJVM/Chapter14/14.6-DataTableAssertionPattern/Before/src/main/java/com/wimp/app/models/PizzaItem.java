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

package com.wimp.app.models;

public final class PizzaItem {
    private String name;
    private PizzaSize size;
    private PizzaStyle style;

    public PizzaItem(String name, PizzaSize size, PizzaStyle style) {
        this.name = name;
        this.size = size;
        this.style = style;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PizzaSize getSize() {
        return size;
    }

    public void setSize(PizzaSize size) {
        this.size = size;
    }

    public PizzaStyle getStyle() {
        return style;
    }

    public void setStyle(PizzaStyle style) {
        this.style = style;
    }
}
