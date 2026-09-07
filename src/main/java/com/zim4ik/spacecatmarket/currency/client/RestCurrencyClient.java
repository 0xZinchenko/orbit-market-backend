package com.zim4ik.spacecatmarket.currency.client;

import com.zim4ik.spacecatmarket.currency.dto.ExchangeRateResponse;
import com.zim4ik.spacecatmarket.currency.exception.CurrencyApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class RestCurrencyClient implements CurrencyClient {

    private final RestClient currencyRestClient;

    public RestCurrencyClient(RestClient currencyRestClient) {
        this.currencyRestClient = currencyRestClient;
    }

    @Override
    public ExchangeRateResponse getExchangeRate(String fromCurrency, String toCurrency) {
        FrankfurterExchangeRateResponse response = currencyRestClient.get()
                .uri("/latest?base={base}&symbols={symbols}", fromCurrency, toCurrency)
                .retrieve()
                .body(FrankfurterExchangeRateResponse.class);

        if (response == null || !response.rates().containsKey(toCurrency)) {
            throw new CurrencyApiException(
                    "Currency API did not return a rate for " + fromCurrency + " -> " + toCurrency
            );
        }

        BigDecimal rate = response.rates().get(toCurrency);
        return new ExchangeRateResponse(fromCurrency, toCurrency, rate);
    }
}
