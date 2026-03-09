package com.ecommerce.order.application.dto;

public record OrderAddressResponse(
    String fullName,
    String phone,
    String fullAddress,
    String city,
    String district,
    String postalCode
) {}
