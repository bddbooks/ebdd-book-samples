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

public class OrderCollectionDetails {
    private int orderNo;
    private int boxesToBeCollected;
    private boolean contactDetailsConfirmationRequested;

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getBoxesToBeCollected() {
        return boxesToBeCollected;
    }

    public void setBoxesToBeCollected(int boxesToBeCollected) {
        this.boxesToBeCollected = boxesToBeCollected;
    }

    public boolean isContactDetailsConfirmationRequested() {
        return contactDetailsConfirmationRequested;
    }

    public void setContactDetailsConfirmationRequested(boolean contactDetailsConfirmationRequested) {
        this.contactDetailsConfirmationRequested = contactDetailsConfirmationRequested;
    }
}
