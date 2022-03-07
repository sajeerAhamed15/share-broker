package com.ntu.sharebroker.controller;

import com.ntu.sharebroker.dto.CompanyTweetsDTO;
import com.ntu.sharebroker.entity.Company;
import com.ntu.sharebroker.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<Company>> getAll() {
        return service.getAll();
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Company> getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Company> create(@RequestBody Company item) {
        return service.create(item);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Company> update(@PathVariable Integer id, @RequestBody Company item) {
        return service.update(id, item);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable int id) {
        return service.deleteById(id);
    }

    // Get company by short name
    @GetMapping("/get-by-short-name/{name}")
    public ResponseEntity<Company> getByShortName(@PathVariable String name) {
        return service.getByShortName(name);
    }

    @GetMapping("/get-tweets/{name}")
    public ResponseEntity<List<CompanyTweetsDTO>> getTweets(@PathVariable String name) {
        return service.getTweets(name);
    }

}
