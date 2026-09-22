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

package com.wimp.app.data.entities;

import com.wimp.app.models.PizzaSize;
import com.wimp.app.models.PizzaStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class PizzaItemEmbeddable {
    @Column(name = "item_name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_size", nullable = false, length = 20)
    private PizzaSize size;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_style", nullable = false, length = 20)
    private PizzaStyle style;

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
