package org.example.hansabal.domain.product.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlingService {
    public List<String> getNaverLinks() {
        List<String> results = new ArrayList<>();
        try {
            String url = "https://www.naver.com";
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String text = link.text();
                String href = link.absUrl("href");
                if (!text.isEmpty()) {
                    results.add(text + " -> " + href);
                }
            }

        } catch (Exception e) {
            results.add("크롤링 실패: " + e.getMessage());
        }

        return results;
    }
}
