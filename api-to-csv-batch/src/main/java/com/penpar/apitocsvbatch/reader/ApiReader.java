package com.penpar.apitocsvbatch.reader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiReader implements ItemReader<Item> {

    private final RestTemplate restTemplate;
    private int nextPage;
    private int totalItems;
    private List<Item> items;

    public ApiReader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.nextPage = 1;
        this.items = new ArrayList<>();
    }

    @Override
    public Item read() throws Exception {
        System.out.println("############# read" );
        if (items.isEmpty()) {
            String encodedServiceKey = "";

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode;
            int numOfRows = 1000;
            int pageNo = 1;
            int totalCount = 0;

                String url = "https://apis.data.go.kr/1160100/service/GetKrxListedInfoService/getStockPriceInfo?serviceKey=" + encodedServiceKey +
                        "&resultType=json&basDt=20230504&numOfRows=" + numOfRows + "&pageNo=" + pageNo;

                URI uri = new URI(url);
                ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

                rootNode = objectMapper.readTree(response.getBody());
                JsonNode responseBody = rootNode.path("response").path("body");

                totalCount = responseBody.path("totalCount").asInt();
                System.out.println("totalCount: " + totalCount);
                JsonNode itemsNode = responseBody.path("items").path("item");

                for (JsonNode itemNode : itemsNode) {
                    Item item = objectMapper.treeToValue(itemNode, Item.class);
                    items.add(item);
                }

                pageNo++;

        }

        System.out.println(items.size());

        return null;
    }
}
