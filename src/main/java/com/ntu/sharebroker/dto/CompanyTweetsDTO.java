package com.ntu.sharebroker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyTweetsDTO {
    private String content;
    private String date;
    private String screenName;

}
