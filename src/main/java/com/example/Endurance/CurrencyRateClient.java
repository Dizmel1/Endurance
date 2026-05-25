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
        FrankfurterResponse[] response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/rates")
                        .queryParam("base", from)
                        .queryParam("quotes", to)
                        .build())
                .retrieve()
                .bodyToMono(FrankfurterResponse[].class)
                .block();

        if (response == null || response.length == 0) {
            throw new RuntimeException("Пустой ответ от API курсов валют");
        }

        FrankfurterResponse first = response[0];

        if (first.rate() == null) {
            throw new RuntimeException("Не удалось получить курс " + from + "/" + to);
        }

        return first.rate();
    }
}