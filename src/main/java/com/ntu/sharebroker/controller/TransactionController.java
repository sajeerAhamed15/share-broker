package com.ntu.sharebroker.controller;

import com.ntu.sharebroker.dto.TradeDTO;
import com.ntu.sharebroker.dto.UserTransactionAggDTO;
import com.ntu.sharebroker.dto.UserTransactionDTO;
import com.ntu.sharebroker.entity.Transaction;
import com.ntu.sharebroker.entity.User;
import com.ntu.sharebroker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<Transaction>> getAll() {
        return service.getAll();
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Transaction> create(@RequestBody Transaction item) {
        return service.create(item);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Integer id, @RequestBody Transaction item) {
        return service.update(id, item);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable int id) {
        return service.deleteById(id);
    }

    // buy/sell shares
    @PostMapping("/trade")
    public ResponseEntity<HttpStatus> trade(@RequestBody TradeDTO item) {
        return service.trade(item);
    }

    @GetMapping("/get-by-user-id/{id}")
    public ResponseEntity<List<Transaction>> getByUserId(@PathVariable Integer id) {
        return service.getByUserId(id);
    }

    @GetMapping("/get-by-user-id-extended/{id}")
    public ResponseEntity<List<UserTransactionDTO>> getByUserIdExtended(@PathVariable Integer id) {
        return service.getByUserIdExtended(id);
    }

    @GetMapping("/get-by-user-id-extended-agg/{id}")
    public ResponseEntity<List<UserTransactionAggDTO>> getByUserIdExtendedAgg(@PathVariable Integer id) {
        return service.getByUserIdExtendedAgg(id);
    }
}

