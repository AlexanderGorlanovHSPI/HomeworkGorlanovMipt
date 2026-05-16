package com.example.homework4.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient externalRestClient(
            RestClient.Builder builder,
            @Value("${external.api.base-url}") String baseUrl,
            @Value("${external.api.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${external.api.read-timeout-ms}") int readTimeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        return builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, "homework4-gateway/1.0")
                .requestFactory(requestFactory)
                .build();
    }
}
