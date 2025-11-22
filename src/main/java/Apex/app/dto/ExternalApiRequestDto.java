package Apex.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiRequestDto {
    private String name;
    private Integer age;
    private String merchantCategory;
    private String merchant_name;
    private String merchant_sub_category;
    private Double imputedIncome;
    private Long cartValue;
    private Integer bureauScore;
    private Integer noOfPersonalLoans;
    private Integer pincodeStabilityMonths;
    private Integer loanInquiryCountIn1M;
    private Integer thinFileFlag;
    private Integer NTC;
    private Integer dpd_30_plus_last_6m;
    private Integer dpd_90_plus_last_36m;
    private Integer cardedFlag;
    private Double gmv_6m_total;
}
