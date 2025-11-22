package Apex.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiResponseDto {
    private String name;
    private String merchant_name;
    private String merchant_sub_category;
    private String loanEligibility;
    private DpdCheck dpd_check;
    private String riskCategory;
    private String segmentColor;
    private String merchantCategory;
    private String merchantPlanOutput;
    private String widgetHeadline;
    private String user_type;
    private Double cart_ratio;
    private Double gmv_monthly;
    private Double expense_percent;
    private Integer affluence_score;
    private Double numeric_assigned_limit;
    private String assignedLimit;
    private Boolean downpaymentRequired;
    private List<EmiOption> emiOptions;
    private String personalisedLoanSuggestion;
    private Integer confidence;
    private Debug debug;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DpdCheck {
        private Integer dpd_30_plus_last_6m;
        private Integer dpd_90_plus_last_36m;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmiOption {
        private String tenure;
        private String type;
        private String apr;
        private Integer montly; // Note: keeping the typo as per your API response
        private Boolean downpaymentRequired;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Debug {
        private Integer modifier_sum;
        private String applied_adjustments;
        private Double shortfall_ratio;
        private Long principal_amount;
        private String reasoning;
    }
}