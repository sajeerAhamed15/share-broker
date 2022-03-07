package com.ntu.sharebroker.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "currency")
@Data
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false, columnDefinition = "varchar(100) UNIQUE")
    private String code;

    @Column(name = "rate_in_usd", columnDefinition = "float default 0 NOT NULL")
    private Float rateInUsd;

    @Column(name = "updated_date")
    private String updatedDate;

}
