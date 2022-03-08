package com.ntu.sharebroker.service;

import com.ntu.sharebroker.dto.AdvanceSearchDTO;
import com.ntu.sharebroker.dto.CompanyTweetsDTO;
import com.ntu.sharebroker.entity.Company;
import com.ntu.sharebroker.entity.Transaction;
import com.ntu.sharebroker.repository.CompanyRepository;
import com.ntu.sharebroker.repository.TransactionRepository;
import com.ntu.sharebroker.utils.HttpUtils;
import com.ntu.sharebroker.utils.MapperUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CompanyService {

    Logger logger = Logger.getLogger(CompanyService.class.getName());

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public ResponseEntity<Company> create(Company item) {
        try {
            Company savedItem = companyRepository.save(item);
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Company create: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    public ResponseEntity<List<Company>> getAll() {
        try {
            List<Company> items = new ArrayList<Company>();
            items.addAll(companyRepository.findAll());
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Company getAll: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Company> getByShortName(String shortName) {
        try {
            Optional<Company> items = companyRepository.findByShortName(shortName);
            if (items.isPresent()) {
                return new ResponseEntity<>(items.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Company getByShortName: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Company> getById(int id) {
        Optional<Company> existingItemOptional = companyRepository.findById(id);
        if (existingItemOptional.isPresent()) {
            return new ResponseEntity<>(existingItemOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<HttpStatus> deleteById(int id) {
        try {
            companyRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Company deleteById: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Company> update(int id, Company item) {
        try {
            Optional<Company> existingItemOptional = companyRepository.findById(id);
            if (existingItemOptional.isPresent()) {
                Company existingItem = existingItemOptional.get();
                MapperUtils.merge(existingItem, item);
                return new ResponseEntity<>(companyRepository.save(existingItem), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Company update: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void updateSharePriceEvent() {
        logger.info("Updating Share Price at " + Calendar.getInstance().getTime());

        List<Company> companies = new ArrayList<Company>(companyRepository.findAll());
        for (Company company : companies) {
            String companyCode = company.getShortName();
            try {
                String urlString = createUrl(companyCode);
                String results = HttpUtils.httpRequest(urlString);

                JSONObject sharePriceData = getSharePriceData(results);
                company.setPricePerShare(Float.parseFloat(String.valueOf(sharePriceData.get("sharePrice"))));
                company.setUpdatedDate(String.valueOf(sharePriceData.get("date")));
                System.out.println("New values: " + sharePriceData);
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }

        System.out.println("Saving All new values");
        companyRepository.saveAll(companies);
    }

    private JSONObject getSharePriceData(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        String date = (String) ((JSONObject) json.get("Meta Data")).get("3. Last Refreshed");
        JSONObject sharePrice = (JSONObject) ((JSONObject) json.get("Time Series (5min)")).get(date);

        JSONObject out = new JSONObject();
        out.put("date", date);
        out.put("sharePrice", sharePrice.get("1. open"));
        return out;
    }

    private String createUrl(String companyCode) {
        String apiKey = "D54CLEX28UGXFDT2";
        return "https://www.alphavantage.co/query?" +
                "function=TIME_SERIES_INTRADAY&" +
                "symbol=" + companyCode + "&" +
                "interval=5min&" +
                "apikey=" + apiKey;
    }

    public ResponseEntity<List<CompanyTweetsDTO>> getTweets(String name) {
        try {
            String url = "https://api.twitter.com/1.1/search/tweets.json?q=" + name;
            String resultsFrom = HttpUtils.httpRequestTwitter(url);
            List<CompanyTweetsDTO> tweetList = getFromJsonTweets(resultsFrom);
            return new ResponseEntity<>(tweetList, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<CompanyTweetsDTO> getFromJsonTweets(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        JSONArray jsonArray = (JSONArray) json.get("statuses");

        List<CompanyTweetsDTO> companyTweetsDTOList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jo = (JSONObject) jsonArray.get(i);
            CompanyTweetsDTO companyTweetsDTO = new CompanyTweetsDTO();
            companyTweetsDTO.setContent((String) jo.get("text"));
            companyTweetsDTO.setDate((String) jo.get("created_at"));
            companyTweetsDTO.setScreenName((String) ((JSONObject) jo.get("user")).get("screen_name"));
            companyTweetsDTOList.add(companyTweetsDTO);
        }

        return companyTweetsDTOList;
    }

    public ResponseEntity<List<Company>> advanceSearch(AdvanceSearchDTO item) {
        try {
            cleanAdvanceSearchQuery(item);
            List<Company> allCompany = companyRepository.advanceSearch(
                    item.getCompanyName(),
                    item.getSharePriceStart(),
                    item.getSharePriceEnd(),
                    item.getRemainingSharesStart(),
                    item.getRemainingSharesEnd());
            if (!allCompany.isEmpty()) {
                Optional<List<Transaction>> transactions = transactionRepository.findByUserId(item.getUserId());
                if (transactions.isPresent() && item.getPrevTransaction() != item.getNoPrevTransaction()) {
                    List<Company> prevTransCompany = new ArrayList<>();
                    for (Company company : allCompany) {
                        for (Transaction transaction: transactions.get()) {
                            if (Objects.equals(company.getId(), transaction.getCompanyId())) {
                                prevTransCompany.add(company);
                                break;
                            }
                        }
                    }
                    if (item.getPrevTransaction()) {
                        return new ResponseEntity<>(prevTransCompany, HttpStatus.OK);
                    } else {
                        for (Company company : prevTransCompany) {
                            allCompany.remove(company);
                        }
                        return new ResponseEntity<>(allCompany, HttpStatus.OK);
                    }
                }
                return new ResponseEntity<>(allCompany, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Company getByShortName: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void cleanAdvanceSearchQuery(AdvanceSearchDTO item) {
        if (item.getSharePriceEnd() == null || item.getSharePriceEnd() == 0) {
            item.setSharePriceEnd(1000000000F);
        }
        if (item.getRemainingSharesEnd() == null || item.getRemainingSharesEnd() == 0) {
            item.setRemainingSharesEnd(1000000000F);
        }
        if (item.getSharePriceStart() == null) {
            item.setSharePriceStart(0F);
        }
        if (item.getRemainingSharesStart() == null) {
            item.setRemainingSharesStart(0F);
        }
    }


    public ResponseEntity<HttpStatus> updateSharePrice() {
        updateSharePriceEvent();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
