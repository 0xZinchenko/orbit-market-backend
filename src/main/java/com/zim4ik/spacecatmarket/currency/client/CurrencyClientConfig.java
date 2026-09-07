package com.zim4ik.spacecatmarket.currency.client;

import com.zim4ik.spacecatmarket.currency.exception.CurrencyApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class CurrencyClientConfig {

    @Bean
    public RestClient currencyRestClient(@Value("${currency-api.base-url}") String baseUrl) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultStatusHandler(
                        status -> status.isError(),
                        (request, response) -> {
                            throw new CurrencyApiException(
                                    "Currency API returned status: " + response.getStatusCode()
                            );
                        }
                )
                .build();
    }
}
