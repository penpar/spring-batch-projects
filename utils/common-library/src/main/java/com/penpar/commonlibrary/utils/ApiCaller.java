package com.penpar.commonlibrary.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiCaller.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ApiCaller() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode fetchDataFromApi(String url, Map<String, String> queryParams) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);

        queryParams.forEach(uriBuilder::queryParam);

        String uri = uriBuilder.toUriString();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        JsonNode rootNode = null;

        try {
            rootNode = objectMapper.readTree(response.getBody());
            LOGGER.debug("Fetched data from API: {}", rootNode);
        } catch (Exception e) {
            LOGGER.error("Error fetching data from API: {}", e.getMessage());
        }

        return rootNode;
    }
}
