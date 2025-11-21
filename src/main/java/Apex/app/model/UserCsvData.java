package Apex.app.model;


import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class UserCsvData {

    @CsvBindByName(column = "user_id")
    private String userId;

    @CsvBindByName(column = "bureauScore")
    private Integer bureauScore;

    @CsvBindByName(column = "bureauVintage")
    private Integer bureauVintage;

    @CsvBindByName(column = "uniqueAccountsCount")
    private Integer uniqueAccountsCount;

    @CsvBindByName(column = "activeAccountsCount")
    private Integer activeAccountsCount;

    @CsvBindByName(column = "loansCount")
    private Integer loansCount;

    @CsvBindByName(column = "unsecuredLoanCount")
    private Integer unsecuredLoanCount;

    @CsvBindByName(column = "ccActiveSumOfCB")
    private Double ccActiveSumOfCB;

    @CsvBindByName(column = "totalCreditCardLimit")
    private Double totalCreditCardLimit;

    @CsvBindByName(column = "inquiryCountIn1M")
    private Integer inquiryCountIn1M;

    @CsvBindByName(column = "imputedIncome")
    private Double imputedIncome;

    @CsvBindByName(column = "hasActiveExclusiveCreditCardAccount")
    private Boolean hasActiveExclusiveCreditCardAccount;

    @CsvBindByName(column = "avgMonthsBetweenAvailingNewTradeline")
    private Double avgMonthsBetweenAvailingNewTradeline;

    @CsvBindByName(column = "maxSanctionedAmountOnAllLoans")
    private Double maxSanctionedAmountOnAllLoans;

    @CsvBindByName(column = "maxOverdueAmountInActiveLoans")
    private Double maxOverdueAmountInActiveLoans;

    @CsvBindByName(column = "ccMaxDPDIn12M")
    private Integer ccMaxDPDIn12M;

    @CsvBindByName(column = "loansWrittenOffSettledCountsIn24M")
    private Integer loansWrittenOffSettledCountsIn24M;
}
