package com.ingnum.rentalservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BonjourController {

    @Value("${php.service.url}")
    private String phpServiceUrl;

    @GetMapping("/bonjour")
    public String bonjour() {
        RestTemplate restTemplate = new RestTemplate();
        String prenom = restTemplate.getForObject(phpServiceUrl, String.class);
        return "Bonjour " + prenom;
    }
}