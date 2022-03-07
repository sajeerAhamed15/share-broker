package com.ntu.sharebroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTransactionAggDTO {
    private Float numShares;
    private Integer companyId;
    private String companyCode;
    private String companyName;
    private String lastPurchasedDate;
    private Float totalAmountSpendInUSD;
}
