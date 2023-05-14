package com.penpar.apitocsvbatch.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.penpar.apitocsvbatch.model.Item;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ApiReader implements ItemReader<List<Item>> {

    private final RestTemplate restTemplate;
    private int nextPage;
    private List<Item> data;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final int PAGE_SIZE = 1000;
    private boolean dataRead = false;

    public ApiReader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.nextPage = 1;
        this.data = new ArrayList<>();
    }

    @Override
    public List<Item> read() throws Exception {
        if (!dataRead) {
            if (data.isEmpty()) {
                String encodedServiceKey = "service key";

                int numOfRows = PAGE_SIZE;
                int pageNo = 1;
                int totalCount = 0;

                while(true) {
                    String url = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo?serviceKey="+encodedServiceKey+"&basDt=20220504&numOfRows="+1000+"&pageNo="+pageNo+"&resultType=json";

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
                        System.out.println(data.size());
                        break;
                    }
      
                    pageNo++;
                }
            }

            if(!data.isEmpty()) {
                // System.out.println(data.size());
                // List<Item> items = data.subList(0, Math.min(PAGE_SIZE, data.size()));
                // data = data.subList(Math.min(PAGE_SIZE, data.size()), data.size());
                // System.out.println("data size" + data.size());
                dataRead = true;
                return data;
            } else {
                return null;
            }
        } else {
            System.out.println("data is empty");
            return null;
        }
    }
}