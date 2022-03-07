package com.ntu.sharebroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDTO {
    private Float numShares;
    private String companyCode;
    private String date;
    private Integer userId;
    private Float pricePerShare;
    private Float pricePerShareInUsd;
    private String currencyCode;
    private String type; // "buy" or "sell"
}
