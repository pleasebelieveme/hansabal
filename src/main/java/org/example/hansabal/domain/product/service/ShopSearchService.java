package org.example.hansabal.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopSearchService {

    String clientId = "oZzdyk_N0TEa6HyXjfwg";
    String clientSecret = "j_3pQgBaa0";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class ShopItem {
        private String title;
        private String lprice;

        public ShopItem(String title, String lprice) {
            this.title = title;
            this.lprice = lprice;
        }

        public String getTitle() {
            return title;
        }

        public String getLprice() {
            return lprice;
        }
    }

    public List<ShopItem> searchShop(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String apiUrl = "https://openapi.naver.com/v1/search/shop?query=" + encodedQuery;

            HttpURLConnection con = (HttpURLConnection) new URL(apiUrl).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

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

        } catch (IOException e) {
            throw new RuntimeException("네이버 쇼핑 API 호출 실패", e);
        }
    }

    private String readBody(InputStream body) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body))) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        }
    }
}
