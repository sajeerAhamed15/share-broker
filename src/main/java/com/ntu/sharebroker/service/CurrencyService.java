package com.ntu.sharebroker.service;

import com.ntu.sharebroker.entity.Currency;
import com.ntu.sharebroker.repository.CurrencyRepository;
import com.ntu.sharebroker.utils.HttpUtils;
import com.ntu.sharebroker.utils.MapperUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CurrencyService {

    Logger logger = Logger.getLogger(CurrencyService.class.getName());

    @Autowired
    private CurrencyRepository repository;

    public ResponseEntity<Currency> create(Currency item) {
        try {
            Currency savedItem = repository.save(item);
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Currency create: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    public ResponseEntity<List<Currency>> getAll() {
        try {
            List<Currency> items = new ArrayList<Currency>();
            items.addAll(repository.findAll());
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Currency getAll: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Cacheable(value="currency")
    public ResponseEntity<Currency> getByCode(String code) {
        try {
            Optional<Currency> items = repository.findByCode(code);
            if (items.isPresent()) {
                return new ResponseEntity<>(items.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Currency getByShortName: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Currency> getById(int id) {
        Optional<Currency> existingItemOptional = repository.findById(id);
        if (existingItemOptional.isPresent()) {
            return new ResponseEntity<>(existingItemOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<HttpStatus> deleteById(int id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Currency deleteById: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Currency> update(int id, Currency item) {
        try {
            Optional<Currency> existingItemOptional = repository.findById(id);
            if (existingItemOptional.isPresent()) {
                Currency existingItem = existingItemOptional.get();
                MapperUtils.merge(existingItem, item);
                return new ResponseEntity<>(repository.save(existingItem), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Currency update: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    public ResponseEntity<Currency> updateRateInUSD(String code, Float rate_in_usd) {
        try {
            Optional<Currency> items = repository.findByCode(code);
            if (items.isPresent() && rate_in_usd != null) {
                items.get().setRateInUsd(rate_in_usd);
                return new ResponseEntity<>(repository.save(items.get()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Currency updateRateInUSD: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Float> exchangeRate(String from, String to) {
        try {
            String url1 = createUrl(from, "USD");
            String resultsFrom = HttpUtils.httpRequest(url1);
            Float rateFrom = updateRate(resultsFrom, from);

            String url2 = createUrl(to, "USD");
            String resultsTo = HttpUtils.httpRequest(url2);
            Float rateTo = updateRate(resultsTo, to);

            Float rate = rateFrom / rateTo;
            return new ResponseEntity<>(rate, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Exception from server - Trying persistent DB to get the response");
            try {
                Optional<Currency> fromCur = repository.findByCode(from);
                Optional<Currency> toCur = repository.findByCode(to);
                if (fromCur.isPresent() && toCur.isPresent()) {
                    Float rateFrom = fromCur.get().getRateInUsd();
                    Float rateTo = toCur.get().getRateInUsd();
                    Float rate = rateFrom / rateTo;
                    return new ResponseEntity<>(rate, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
                }
            } catch (Exception e1) {
                System.out.println("Exception: " + e1);
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private Float updateRate(String resultsFrom, String code) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(resultsFrom);
        String date = (String) json.get("date");
        Float rate = Float.parseFloat(String.valueOf(json.get("rate")));

        Optional<Currency> currency = repository.findByCode(code);
        if (currency.isPresent()) {
            currency.get().setUpdatedDate(date);
            currency.get().setRateInUsd(rate);
            repository.save(currency.get());
        }

        return rate;
    }

    private String createUrl(String from, String to) {
        return "http://localhost:8080/CurConvRS/webresources/exchangeRate?" +
                "from=" + from + "&to=" + to;
    }

    public ResponseEntity<List<String>> getAllCodes() {
        try {
            List<Currency> items = repository.findAll();
            List<String> currCodes = new ArrayList<>();
            items.forEach(currency -> currCodes.add(currency.getCode()));
            return new ResponseEntity<>(currCodes, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Currency getAllCodes: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateSingleExchangeRate(String curr) {
        try {
            String url1 = createUrl(curr, "USD");
            String resultsFrom = HttpUtils.httpRequest(url1);
            updateRate(resultsFrom, curr);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Currency updateSingleExchangeRate: " + e.getMessage());
        }
    }

    public ResponseEntity<HttpStatus> updateExchangeRate() {
        List<Currency> currencies = repository.findAll();
        for (Currency currency : currencies) {
            try {
                updateSingleExchangeRate(currency.getCode());
            } catch (Exception ignore) {}
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
