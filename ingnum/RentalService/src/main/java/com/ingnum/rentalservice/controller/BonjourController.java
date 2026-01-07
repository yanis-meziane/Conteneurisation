package com.ingnum.rentalservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BonjourController {

    @GetMapping("/bonjour")
    public String bonjour() {
        return "bonjour";
    }
}
