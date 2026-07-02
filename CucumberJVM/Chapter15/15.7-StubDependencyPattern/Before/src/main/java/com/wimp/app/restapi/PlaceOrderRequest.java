package com.wimp.app.restapi;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PlaceOrderRequest(
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime expectedDeliveryTime
) {
}
