package com.zim4ik.spacecatmarket.currency.dto;

import java.math.BigDecimal;

public record ExchangeRateResponse(String from, String to, BigDecimal rate) {
}
