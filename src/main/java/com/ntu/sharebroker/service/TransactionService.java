package com.ntu.sharebroker.service;

import com.ntu.sharebroker.dto.TradeDTO;
import com.ntu.sharebroker.dto.UserTransactionAggDTO;
import com.ntu.sharebroker.dto.UserTransactionDTO;
import com.ntu.sharebroker.entity.Company;
import com.ntu.sharebroker.entity.Transaction;
import com.ntu.sharebroker.entity.User;
import com.ntu.sharebroker.repository.CompanyRepository;
import com.ntu.sharebroker.repository.TransactionRepository;
import com.ntu.sharebroker.repository.UserRepository;
import com.ntu.sharebroker.utils.MapperUtils;
import com.ntu.sharebroker.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionService {

    Logger logger = Logger.getLogger(TransactionService.class.getName());

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public ResponseEntity<Transaction> create(Transaction item) {
        try {
            Transaction savedItem = transactionRepository.save(item);
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transaction create: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    public ResponseEntity<List<Transaction>> getAll() {
        try {
            List<Transaction> items = new ArrayList<Transaction>();
            items.addAll(transactionRepository.findAll());
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transaction getAll: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Transaction> getById(int id) {
        Optional<Transaction> existingItemOptional = transactionRepository.findById(id);
        if (existingItemOptional.isPresent()) {
            return new ResponseEntity<>(existingItemOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<HttpStatus> deleteById(int id) {
        try {
            transactionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transaction deleteById: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Transaction> update(int id, Transaction item) {
        try {
            Optional<Transaction> existingItemOptional = transactionRepository.findById(id);
            if (existingItemOptional.isPresent()) {
                Transaction existingItem = existingItemOptional.get();
                MapperUtils.merge(existingItem, item);
                return new ResponseEntity<>(transactionRepository.save(existingItem), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transaction update: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Transactional
    public ResponseEntity<HttpStatus> trade(TradeDTO item) {
        try {
            Optional<Company> companyOptional = companyRepository.findByShortName(item.getCompanyCode());
            if (companyOptional.isPresent()) {
                Company company = companyOptional.get();
                Float totShares = company.getTotalShares();

                if ("buy".equalsIgnoreCase(item.getType())) {
                    totShares = totShares - item.getNumShares();
                } else if ("sell".equalsIgnoreCase(item.getType())) {
                    totShares = totShares + item.getNumShares();
                } else {
                    logger.log(Level.INFO, "No Transaction type provided", item);
                    return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
                }

                company.setTotalShares(totShares);
                companyRepository.save(company);

                Transaction newTransaction = new Transaction();
                newTransaction.setCurrency(item.getCurrencyCode());
                newTransaction.setDate(Utils.getCurrentDate());
                newTransaction.setPricePerShare(item.getPricePerShare());
                newTransaction.setPricePerShareInUsd(item.getPricePerShareInUsd());
                if ("buy".equalsIgnoreCase(item.getType())) {
                    newTransaction.setNumShares(item.getNumShares());
                } else if ("sell".equalsIgnoreCase(item.getType())) {
                    newTransaction.setNumShares(item.getNumShares() * -1);
                }
                newTransaction.setCompanyId(company.getId());
                newTransaction.setUserId(item.getUserId());

                transactionRepository.save(newTransaction);

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.log(Level.INFO, "Wrong company name", item);
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transaction trade: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    public ResponseEntity<List<UserTransactionDTO>> getByUserIdExtended(Integer id) {
        try {
            Optional<List<Transaction>> items = transactionRepository.findByUserId(id);
            if (items.isPresent()) {
                List<UserTransactionDTO> userTransactionDTOS = new ArrayList<>();
                for (Transaction transaction : items.get()) {
                    UserTransactionDTO userTransactionDTO = new UserTransactionDTO();

                    Company company = getByCompanyId(transaction.getCompanyId());
                    userTransactionDTO.setCompanyCode(company.getShortName());
                    userTransactionDTO.setCompanyName(company.getName());

                    String type = transaction.getNumShares() > 0 ? "buy" : "sell";
                    userTransactionDTO.setType(type);

                    userTransactionDTO.setDate(transaction.getDate());
                    userTransactionDTO.setNumShares(transaction.getNumShares());
                    userTransactionDTO.setPricePerShare(transaction.getPricePerShare());
                    userTransactionDTO.setCurrencyCode(transaction.getCurrency());
                    userTransactionDTOS.add(userTransactionDTO);
                }
                return new ResponseEntity<>(userTransactionDTOS, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transaction getByUserIdExtended: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Company getByCompanyId(int id) {
        Optional<Company> existingItemOptional = companyRepository.findById(id);
        return existingItemOptional.orElse(null);
    }

    public ResponseEntity<List<Transaction>> getByUserId(Integer id) {
        try {
            Optional<List<Transaction>> items = transactionRepository.findByUserId(id);
            if (items.isPresent()) {
                return new ResponseEntity<>(items.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transaction getAll: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<UserTransactionAggDTO>> getByUserIdExtendedAgg(Integer id) {
        try {
            Optional<List<Transaction>> items = transactionRepository.findByUserId(id);
            if (items.isPresent()) {
                List<UserTransactionAggDTO> UserTransactionAggDTOS = new ArrayList<>();
                for (Transaction transaction : items.get()) {
                    List<UserTransactionAggDTO> UserTransactionAggDTOS1 = UserTransactionAggDTOS.stream().filter(
                            u -> Objects.equals(u.getCompanyId(), transaction.getCompanyId())
                    ).collect(Collectors.toList());
                    if (UserTransactionAggDTOS1.size() > 0) {
                        UserTransactionAggDTO UserTransactionAggDTO = UserTransactionAggDTOS1.get(0);
                        UserTransactionAggDTO.setLastPurchasedDate(transaction.getDate());
                        UserTransactionAggDTO.setNumShares(UserTransactionAggDTO.getNumShares() + transaction.getNumShares());
                        Float totAmount = (UserTransactionAggDTO.getTotalAmountSpendInUSD() + transaction.getPricePerShareInUsd() * transaction.getNumShares());
                        UserTransactionAggDTO.setTotalAmountSpendInUSD(totAmount);
                    } else {
                        UserTransactionAggDTO UserTransactionAggDTO = new UserTransactionAggDTO();

                        Company company = getByCompanyId(transaction.getCompanyId());
                        UserTransactionAggDTO.setCompanyCode(company.getShortName());
                        UserTransactionAggDTO.setCompanyName(company.getName());
                        UserTransactionAggDTO.setCompanyId(company.getId());

                        UserTransactionAggDTO.setLastPurchasedDate(transaction.getDate());
                        UserTransactionAggDTO.setNumShares(transaction.getNumShares());
                        Float totAmount = (transaction.getPricePerShareInUsd() * transaction.getNumShares());
                        UserTransactionAggDTO.setTotalAmountSpendInUSD(totAmount);

                        UserTransactionAggDTOS.add(UserTransactionAggDTO);
                    }
                }
                return new ResponseEntity<>(UserTransactionAggDTOS, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Transaction getByUserIdExtended: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
