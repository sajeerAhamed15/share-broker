package com.ntu.sharebroker.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "transaction")
@Data
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "company_id", nullable = false)
    private Integer companyId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "num_shares", nullable = false)
    private Float numShares;

    @Column(name = "price_per_share", columnDefinition = "float default 0 NOT NULL")
    private Float pricePerShare;

    @Column(name = "price_per_share_in_usd", columnDefinition = "float default 0 NOT NULL")
    private Float pricePerShareInUsd;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "date", nullable = false)
    private String date;

}
