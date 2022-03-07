package com.ntu.sharebroker.repository;

import com.ntu.sharebroker.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Integer>, JpaSpecificationExecutor<Currency> {

    Optional<Currency> findByCode(String code);
}