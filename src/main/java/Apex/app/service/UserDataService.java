package Apex.app.service;

import Apex.app.dto.ExternalApiRequestDto;
import Apex.app.dto.ExternalApiResponseDto;
import Apex.app.dto.UserRequestDto;
import Apex.app.model.CombinedUserData;
import Apex.app.model.UserCsvData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserDataService {

    @Autowired
    private CsvReaderService csvReaderService;

    @Autowired
    private ExternalApiService externalApiService;

    public ExternalApiResponseDto processUserData(UserRequestDto request) {

        // 1. Find user data from CSV
        UserCsvData csvData = csvReaderService.findUserById(request.getUser_id());

        // 2. Map to external API request format
        ExternalApiRequestDto apiRequest = mapToExternalApiRequest(request, csvData);

        // 3. Call external API and return response directly
        return externalApiService.callExternalApi(apiRequest);
    }

    private ExternalApiRequestDto mapToExternalApiRequest(UserRequestDto request, UserCsvData csvData) {
        return ExternalApiRequestDto.builder()
                .name(csvData.getName() != null ? csvData.getName() : "User")
                .age(csvData.getAge() != null ? csvData.getAge() : 25)
                .merchantCategory(request.getMerchant_category())
                .merchant_name(request.getMerchant_name())
                .merchant_sub_category(request.getMerchant_sub_category())
                .imputedIncome(csvData.getImputedIncome())
                .cartValue(request.getCart_value())
                .bureauScore(csvData.getBureauScore())
                .noOfPersonalLoans(csvData.getLoansCount() != null ? csvData.getLoansCount() : 0)
                .pincodeStabilityMonths(request.getLive_address_info().getAddress_change_months())
                .loanInquiryCountIn1M(csvData.getInquiryCountIn1M() != null ? csvData.getInquiryCountIn1M() : 0)
                .thinFileFlag(csvData.getUniqueAccountsCount() != null && csvData.getUniqueAccountsCount() < 3 ? 1 : 0)
                .NTC(calculateNTC(csvData))
                .dpd_30_plus_last_6m(csvData.getDpd30PlusLast6m() != null ? csvData.getDpd30PlusLast6m() : 0)
                .dpd_90_plus_last_36m(csvData.getDpd90PlusLast36m() != null ? csvData.getDpd90PlusLast36m() : 0)
                .cardedFlag(csvData.getHasActiveExclusiveCreditCardAccount() != null && csvData.getHasActiveExclusiveCreditCardAccount() ? 1 : 0)
                .gmv_6m_total(csvData.getGmv6mTotal() != null ? csvData.getGmv6mTotal() : 0.0)
                .build();
    }

    private Integer calculateNTC(UserCsvData csvData) {
        // NTC (New To Credit) - simplified logic
        if (csvData.getBureauVintage() == null || csvData.getBureauVintage() < 12) {
            return 1; // New to credit
        }
        return 0; // Experienced credit user
    }
}