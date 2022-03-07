package com.ntu.sharebroker.service;

import com.ntu.sharebroker.entity.User;
import com.ntu.sharebroker.repository.UserRepository;
import com.ntu.sharebroker.utils.MapperUtils;
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
public class UserService {

    Logger logger = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository repository;

    public ResponseEntity<User> create(User item) {
        try {
            User savedItem = repository.save(item);
            return new ResponseEntity<>(removePassword(savedItem), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "User create: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    public ResponseEntity<List<User>> getAll() {
        try {
            List<User> items = new ArrayList<User>();
            items.addAll(repository.findAll());
            return new ResponseEntity<>(removePassword(items), HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "User getAll: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<User> getById(int id) {
        Optional<User> existingItemOptional = repository.findById(id);
        if (existingItemOptional.isPresent()) {
            return new ResponseEntity<>(removePassword(existingItemOptional.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<HttpStatus> deleteById(int id) {
        try {
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "User deleteById: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<User> update(int id, User item) {
        try {
            Optional<User> existingItemOptional = repository.findById(id);
            if (existingItemOptional.isPresent()) {
                User existingItem = existingItemOptional.get();
                MapperUtils.merge(existingItem, item);
                return new ResponseEntity<>(repository.save(existingItem), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "User update: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    private User removePassword(User obj) {
        obj.setPassword("");
        return obj;
    }

    private List<User> removePassword(List<User> list) {
        for (User obj : list) {
            obj.setPassword("");
        }
        return list;
    }

    public ResponseEntity<User> login(User item) {
        try {
            Optional<User> existingItemOptional = repository.findByNameAndPassword(item.getName(), item.getPassword());
            if (existingItemOptional.isPresent()) {
                return new ResponseEntity<>(removePassword(existingItemOptional.get()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "User update: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
