package com.ntu.sharebroker.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "company")
@Data
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_name", nullable = false, columnDefinition = "varchar(100) UNIQUE")
    private String shortName;

    @Column(name = "total_shares", columnDefinition = "float default 0 NOT NULL")
    private Float totalShares;

    @Column(name = "price_per_share", columnDefinition = "float default 0 NOT NULL")
    private Float pricePerShare;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "updated_date")
    private String updatedDate;

    @Column(name = "url")
    private String url;

}
