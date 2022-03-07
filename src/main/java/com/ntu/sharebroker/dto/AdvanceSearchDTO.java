package com.ntu.sharebroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceSearchDTO {
    private String companyName;
    private Float sharePriceStart;
    private Float sharePriceEnd;
    private Float remainingSharesStart;
    private Float remainingSharesEnd;
    private Boolean prevTransaction;
    private Boolean noPrevTransaction;
    private Integer userId;
}