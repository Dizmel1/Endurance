package com.example.Endurance;

import com.example.Endurance.dto.FrankfurterResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
public class CurrencyRateClient {

    private final WebClient webClient;

    public CurrencyRateClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.frankfurter.dev")
                .build();
    }

    public BigDecimal getRate(String from, String to) {
        FrankfurterResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/latest")
                        .queryParam("base", from)
                        .queryParam("symbols", to)
                        .build())
                .retrieve()
                .bodyToMono(FrankfurterResponse.class)
                .block();

        if (response == null || response.rates() == null || !response.rates().containsKey(to)) {
            throw new RuntimeException("Не удалось получить курс " + from + "/" + to);
        }

        return response.rates().get(to);
    }
}