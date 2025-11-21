package Apex.app.service;

import Apex.app.dto.ExternalApiResponseDto;
import Apex.app.dto.UserRequestDto;
import Apex.app.model.CombinedUserData;
import Apex.app.model.UserCsvData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDataService {

    @Autowired
    private CsvReaderService csvReaderService;

    @Autowired
    private ExternalApiService externalApiService;

    public ExternalApiResponseDto processUserData(UserRequestDto request) {

        // 1. Find user data from CSV (throws UserNotFoundException if not found)
        UserCsvData csvData = csvReaderService.findUserById(request.getUser_id());

        // 2. Combine request data with CSV data
        CombinedUserData combinedData = CombinedUserData.builder()
                .userId(request.getUser_id())
                .cartValue(request.getCart_value())
                .merchantCategory(request.getMerchant_category())
                .merchantSubventionNoCostEmiFlag(request.getMerchant_subvention_no_cost_emi_flag())
                .liveAddressInfo(request.getLive_address_info())
                .bureauScore(csvData.getBureauScore())
                .bureauVintage(csvData.getBureauVintage())
                .uniqueAccountsCount(csvData.getUniqueAccountsCount())
                .activeAccountsCount(csvData.getActiveAccountsCount())
                .loansCount(csvData.getLoansCount())
                .unsecuredLoanCount(csvData.getUnsecuredLoanCount())
                .ccActiveSumOfCB(csvData.getCcActiveSumOfCB())
                .totalCreditCardLimit(csvData.getTotalCreditCardLimit())
                .inquiryCountIn1M(csvData.getInquiryCountIn1M())
                .imputedIncome(csvData.getImputedIncome())
                .hasActiveExclusiveCreditCardAccount(csvData.getHasActiveExclusiveCreditCardAccount())
                .avgMonthsBetweenAvailingNewTradeline(csvData.getAvgMonthsBetweenAvailingNewTradeline())
                .maxSanctionedAmountOnAllLoans(csvData.getMaxSanctionedAmountOnAllLoans())
                .maxOverdueAmountInActiveLoans(csvData.getMaxOverdueAmountInActiveLoans())
                .ccMaxDPDIn12M(csvData.getCcMaxDPDIn12M())
                .loansWrittenOffSettledCountsIn24M(csvData.getLoansWrittenOffSettledCountsIn24M())
                .build();

        // 3. Call external API (throws ExternalApiException on failure)
        return externalApiService.callExternalApi(combinedData);
    }
}