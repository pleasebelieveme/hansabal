package org.example.hansabal.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.product.dto.response.ShopItem;
import org.example.hansabal.domain.product.exception.ProductErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopSearchService {

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final ObjectMapper objectMapper;

    public List<ShopItem> searchShop(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String apiUrl = "https://openapi.naver.com/v1/search/shop?query=" + encodedQuery;

            HttpURLConnection con = (HttpURLConnection) new URL(apiUrl).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            con.setConnectTimeout(5000);
            con.setReadTimeout(10000);

            int responseCode = con.getResponseCode();
            InputStream inputStream = (responseCode == 200) ? con.getInputStream() : con.getErrorStream();
            String responseBody = readBody(inputStream);

            // JSON 파싱 후 items만 추출
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode itemsNode = root.path("items");

            List<ShopItem> items = new ArrayList<>();
            for (JsonNode itemNode : itemsNode) {
                String title = itemNode.path("title").asText();
                String lprice = itemNode.path("lprice").asText();
                items.add(new ShopItem(title, lprice));
            }
            return items;
        } catch (Exception e) {
            throw new BizException(ProductErrorCode.FAILED_NAVER);
        }
    }

    public String readBody(InputStream body){
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(body, StandardCharsets.UTF_8))) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (Exception e) {
            throw new BizException(ProductErrorCode.FAILED_NAVER);
        }
    }
}
