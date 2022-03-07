package com.ntu.sharebroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTransactionDTO {
    private Float numShares;
    private Integer companyId;
    private String companyCode;
    private String companyName;
    private String date;
    private Float pricePerShare;
    private String currencyCode;
    private String type; // "buy" or "sell"
}
