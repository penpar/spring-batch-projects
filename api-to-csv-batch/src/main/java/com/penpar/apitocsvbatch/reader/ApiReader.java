package com.penpar.apitocsvbatch.reader;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.penpar.apitocsvbatch.model.Item;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiReader implements ItemReader<List<Item>> {

    private final RestTemplate restTemplate;
    private int nextPage;
    private List<Item> data;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final int PAGE_SIZE = 1000;
    private boolean dataRead = false;
    private String basDt;

    @Value("${api.service-key}")
    private String encodedServiceKey;

    public ApiReader(RestTemplate restTemplate, String basDt) {
        this.restTemplate = restTemplate;
        this.nextPage = 1;
        this.data = new ArrayList<>();
        this.basDt = basDt;
    }

    @Override
    public List<Item> read() throws Exception {

        if (!dataRead) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate startDate;
            LocalDate endDate;

            if (basDt.length() == 6) {
                // process for the whole month
                startDate = LocalDate.parse(basDt + "01", formatter);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            } else {
                // process only for that date
                startDate = LocalDate.parse(basDt, formatter);
                endDate = startDate;
            }
            
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                basDt = date.format(formatter);  // Add this line to update basDt

                int numOfRows = PAGE_SIZE;
                int pageNo = 1;
                int totalCount = 0;

                while(true) {
                    String url = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo?serviceKey="+encodedServiceKey+"&basDt="+basDt+"&numOfRows="+1000+"&pageNo="+pageNo+"&resultType=json";
                    URI uri = new URI(url);
                    ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

                    JsonNode rootNode = objectMapper.readTree(response.getBody());
                    JsonNode responseBody = rootNode.path("response").path("body");

                    totalCount = responseBody.path("totalCount").asInt();
                    JsonNode itemsNode = responseBody.path("items").path("item");

                    for (JsonNode itemNode : itemsNode) {
                        Item item = objectMapper.treeToValue(itemNode, Item.class);
                        data.add(item);
                    }

                    if(pageNo * numOfRows >= totalCount) {
                        log.info(url + " : " + totalCount + " rows read");
                        break;
                    }
      
                    pageNo++;
                }
            }

            if(!data.isEmpty()) {
                dataRead = true;
                log.info("basDt: " + basDt);
                log.info("Total: " + data.size());
                return data;
            } else {
                return null;
            }
        } else {
            log.info("No more data to read");
            return null;
        }
    }
}