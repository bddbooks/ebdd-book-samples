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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OrderCollectionDetailsEmbeddable {
    @Column(name = "collection_order_no")
    private Integer orderNo;

    @Column(name = "collection_boxes_to_be_collected")
    private Integer boxesToBeCollected;

    @Column(name = "collection_contact_details_confirmation_requested")
    private Boolean contactDetailsConfirmationRequested;

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getBoxesToBeCollected() {
        return boxesToBeCollected;
    }

    public void setBoxesToBeCollected(Integer boxesToBeCollected) {
        this.boxesToBeCollected = boxesToBeCollected;
    }

    public Boolean getContactDetailsConfirmationRequested() {
        return contactDetailsConfirmationRequested;
    }

    public void setContactDetailsConfirmationRequested(Boolean contactDetailsConfirmationRequested) {
        this.contactDetailsConfirmationRequested = contactDetailsConfirmationRequested;
    }
}
