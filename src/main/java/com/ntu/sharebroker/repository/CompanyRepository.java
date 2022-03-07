package com.ntu.sharebroker.repository;

import com.ntu.sharebroker.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer>, JpaSpecificationExecutor<Company> {

    Optional<Company> findByShortName(String shortName);

    @Query(value = "SELECT " +
            "c.* " +
            "FROM " +
            "company c " +
            "WHERE " +
            "c.total_shares >= :remainingSharesStart AND " +
            "c.total_shares <= :remainingSharesEnd AND " +
            "c.price_per_share >= :sharePriceStart AND " +
            "c.price_per_share <= :sharePriceEnd AND " +
            "(" +
            "c.name LIKE CONCAT('%',:companyName,'%')" +
            " OR " +
            "c.short_name LIKE CONCAT('%',:companyName,'%')" +
            ")",
            nativeQuery = true)
    List<Company> advanceSearch(String companyName, Float sharePriceStart, Float sharePriceEnd, Float remainingSharesStart, Float remainingSharesEnd);
}