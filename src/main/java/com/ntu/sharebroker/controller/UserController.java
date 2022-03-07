package com.ntu.sharebroker.controller;

import com.ntu.sharebroker.dto.TradeDTO;
import com.ntu.sharebroker.entity.User;
import com.ntu.sharebroker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<User>> getAll() {
        return service.getAll();
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<User> getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User item) {
        return service.create(item);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> update(@PathVariable Integer id, @RequestBody User item) {
        return service.update(id, item);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable int id) {
        return service.deleteById(id);
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User item) {
        return service.login(item);
    }

}

