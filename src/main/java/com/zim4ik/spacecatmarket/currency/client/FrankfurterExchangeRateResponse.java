package com.zim4ik.spacecatmarket.currency.client;

import java.math.BigDecimal;
import java.util.Map;

record FrankfurterExchangeRateResponse(BigDecimal amount, String base, String date, Map<String, BigDecimal> rates) {
}
