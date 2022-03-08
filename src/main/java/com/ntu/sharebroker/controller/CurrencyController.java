package com.ntu.sharebroker.controller;

import com.ntu.sharebroker.entity.Currency;
import com.ntu.sharebroker.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<Currency>> getAll() {
        return service.getAll();
    }

    @GetMapping("/get-all-curr-code")
    public ResponseEntity<List<String>> getAllCodes() {
        return service.getAllCodes();
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Currency> getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Currency> create(@RequestBody Currency item) {
        return service.create(item);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Currency> update(@PathVariable Integer id, @RequestBody Currency item) {
        return service.update(id, item);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable int id) {
        return service.deleteById(id);
    }

    // Get currency by code
    @GetMapping("/get-by-code/{name}")
    public ResponseEntity<Currency> getByCode(@PathVariable String name) {
        return service.getByCode(name);
    }

    // Update rate_in_usd of a currency given the code
    @GetMapping("/update-rate/{code}")
    public ResponseEntity<Currency> updateRateInUSD(@RequestParam String code, @RequestParam Float rate_in_usd) {
        return service.updateRateInUSD(code, rate_in_usd);
    }

    // get exchange rate between two currencies
    @GetMapping("/exchange-rate")
    public ResponseEntity<Float> exchangeRate(@RequestParam String from, @RequestParam String to) {
        return service.exchangeRate(from, to);
    }

    @GetMapping("/update-exchange-rate")
    public ResponseEntity<HttpStatus> updateExchangeRate() {
        return service.updateExchangeRate();
    }

}

