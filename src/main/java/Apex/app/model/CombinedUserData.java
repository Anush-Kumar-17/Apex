package Apex.app.model;

import Apex.app.dto.UserRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedUserData {
    // Original request data
    private String userId;
    private Long cartValue;
    private String merchantCategory;
    private Boolean merchantSubventionNoCostEmiFlag;
    private UserRequestDto.LiveAddressInfo liveAddressInfo;

    // CSV data
    private Integer bureauScore;
    private Integer bureauVintage;
    private Integer uniqueAccountsCount;
    private Integer activeAccountsCount;
    private Integer loansCount;
    private Integer unsecuredLoanCount;
    private Double ccActiveSumOfCB;
    private Double totalCreditCardLimit;
    private Integer inquiryCountIn1M;
    private Double imputedIncome;
    private Boolean hasActiveExclusiveCreditCardAccount;
    private Double avgMonthsBetweenAvailingNewTradeline;
    private Double maxSanctionedAmountOnAllLoans;
    private Double maxOverdueAmountInActiveLoans;
    private Integer ccMaxDPDIn12M;
    private Integer loansWrittenOffSettledCountsIn24M;
}
