package org.example.hansabal.domain.product.controller;

import org.example.hansabal.domain.product.service.CrawlingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class CrawlingController {

    private final CrawlingService crawlingService;

    public CrawlingController(CrawlingService crawlingService) {
        this.crawlingService = crawlingService;
    }

    @GetMapping("/crawl")
    public List<String> crawlNaver() {
        return crawlingService.getNaverLinks();
    }
}