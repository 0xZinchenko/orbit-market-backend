package com.zim4ik.spacecatmarket.currency.client;

import com.zim4ik.spacecatmarket.currency.dto.ExchangeRateResponse;
import com.zim4ik.spacecatmarket.currency.exception.CurrencyApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class RestCurrencyClientIntegrationTest {

    @Container
    static WireMockContainer wireMock = new WireMockContainer("wiremock/wiremock:3.9.1");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("currency-api.base-url", wireMock::getBaseUrl);
    }

    @Autowired
    private CurrencyClient currencyClient;

    @Test
    void shouldReturnExchangeRateFromExternalApi() {
        configureFor(wireMock.getHost(), wireMock.getMappedPort(8080));
        stubFor(get(urlEqualTo("/latest?base=USD&symbols=EUR"))
                .willReturn(okJson("""
                        {"amount":1.0,"base":"USD","date":"2026-09-07","rates":{"EUR":0.92}}
                        """)));

        ExchangeRateResponse response = currencyClient.getExchangeRate("USD", "EUR");

        assertThat(response.from()).isEqualTo("USD");
        assertThat(response.to()).isEqualTo("EUR");
        assertThat(response.rate()).isEqualByComparingTo(BigDecimal.valueOf(0.92));
    }

    @Test
    void shouldThrowWhenExternalApiReturnsServerError() {
        configureFor(wireMock.getHost(), wireMock.getMappedPort(8080));
        stubFor(get(urlEqualTo("/latest?base=USD&symbols=ZZZ"))
                .willReturn(serverError()));

        assertThatThrownBy(() -> currencyClient.getExchangeRate("USD", "ZZZ"))
                .isInstanceOf(CurrencyApiException.class);
    }

    @Test
    void shouldThrowWhenExternalApiDoesNotReturnRequestedCurrency() {
        configureFor(wireMock.getHost(), wireMock.getMappedPort(8080));
        stubFor(get(urlEqualTo("/latest?base=USD&symbols=EUR"))
                .willReturn(okJson("""
                        {"amount":1.0,"base":"USD","date":"2026-09-07","rates":{}}
                        """)));

        assertThatThrownBy(() -> currencyClient.getExchangeRate("USD", "EUR"))
                .isInstanceOf(CurrencyApiException.class);
    }
}
