package com.wimp.localscenariocontext.app.models;

public class Notification {
    private String customerName;
    private String message;

    public Notification(String customerName, String message) {
        this.customerName = customerName;
        this.message = message;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
