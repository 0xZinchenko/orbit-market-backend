package com.zim4ik.spacecatmarket.currency.client;

import com.zim4ik.spacecatmarket.currency.dto.ExchangeRateResponse;

public interface CurrencyClient {

    ExchangeRateResponse getExchangeRate(String fromCurrency, String toCurrency);
}
