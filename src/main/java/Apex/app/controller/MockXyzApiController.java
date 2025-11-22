package Apex.app.controller;

import Apex.app.dto.ExternalApiRequestDto;
import Apex.app.dto.ExternalApiResponseDto;
import Apex.app.model.CombinedUserData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/mock/api")
public class MockXyzApiController {

    @PostMapping("/xyz")
    public ResponseEntity<ExternalApiResponseDto> mockXyzApi(@RequestBody ExternalApiRequestDto request) {

        // Calculate loan eligibility based on bureau score and other factors
        String eligibility = calculateEligibility(request);
        String riskCategory = calculateRiskCategory(request);
        String segmentColor = calculateSegmentColor(riskCategory);
        String userType = request.getCardedFlag() == 1 ? "Carded" : "Non-Carded";

        // Calculate assigned limit
        Double numericLimit = calculateAssignedLimit(request);
        String formattedLimit = formatCurrency(numericLimit);

        // Generate EMI options
        List<ExternalApiResponseDto.EmiOption> emiOptions = generateEmiOptions(request, eligibility);

        // Calculate confidence
        Integer confidence = calculateConfidence(request);

        // Determine if downpayment required
        Boolean downpaymentRequired = request.getBureauScore() < 600 || request.getCartValue() > numericLimit;

        // Generate merchant plan output
        String merchantPlan = generateMerchantPlan(eligibility, downpaymentRequired);

        ExternalApiResponseDto response = ExternalApiResponseDto.builder()
                .name(request.getName())
                .merchant_name(request.getMerchant_name())
                .merchant_sub_category(request.getMerchant_sub_category())
                .loanEligibility(eligibility)
                .dpd_check(ExternalApiResponseDto.DpdCheck.builder()
                        .dpd_30_plus_last_6m(request.getDpd_30_plus_last_6m())
                        .dpd_90_plus_last_36m(request.getDpd_90_plus_last_36m())
                        .build())
                .riskCategory(riskCategory)
                .segmentColor(segmentColor)
                .merchantCategory(request.getMerchantCategory())
                .merchantPlanOutput(merchantPlan)
                .widgetHeadline("")
                .user_type(userType)
                .cart_ratio(request.getCartValue() / request.getImputedIncome())
                .gmv_monthly(request.getGmv_6m_total() / 6.0)
                .expense_percent(0.0)
                .affluence_score(calculateAffluenceScore(request))
                .numeric_assigned_limit(numericLimit)
                .assignedLimit(formattedLimit)
                .downpaymentRequired(downpaymentRequired)
                .emiOptions(emiOptions)
                .personalisedLoanSuggestion(merchantPlan)
                .confidence(confidence)
                .debug(ExternalApiResponseDto.Debug.builder()
                        .modifier_sum(calculateModifierSum(request))
                        .applied_adjustments(generateAdjustments(request))
                        .shortfall_ratio(0.0)
                        .principal_amount(request.getCartValue())
                        .reasoning("Deterministic rules applied with modifiers and ML override.")
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    private String calculateEligibility(ExternalApiRequestDto request) {
        if (request.getBureauScore() < 300) return "Not Eligible";
        if (request.getDpd_90_plus_last_36m() > 2) return "Not Eligible";
        return "Eligible";
    }

    private String calculateRiskCategory(ExternalApiRequestDto request) {
        if (request.getBureauScore() >= 700) return "Low_Risk";
        if (request.getBureauScore() >= 500) return "Medium_Risk";
        return "High_Risk";
    }

    private String calculateSegmentColor(String riskCategory) {
        switch (riskCategory) {
            case "Low_Risk": return "Green";
            case "Medium_Risk": return "Orange";
            case "High_Risk": return "Red";
            default: return "Red";
        }
    }

    private Double calculateAssignedLimit(ExternalApiRequestDto request) {
        double baseLimit = request.getImputedIncome() * 0.1; // 10% of income

        // Adjust based on bureau score
        if (request.getBureauScore() >= 700) baseLimit *= 1.5;
        else if (request.getBureauScore() >= 500) baseLimit *= 1.0;
        else baseLimit *= 0.5;

        return Math.max(baseLimit, 10000.0); // Minimum 10k
    }

    private Integer calculateAffluenceScore(ExternalApiRequestDto request) {
        int score = 0;
        if (request.getImputedIncome() > 100000) score += 30;
        else if (request.getImputedIncome() > 50000) score += 20;
        else score += 10;

        if (request.getBureauScore() >= 700) score += 20;
        else if (request.getBureauScore() >= 500) score += 10;

        return Math.min(score, 100);
    }

    private List<ExternalApiResponseDto.EmiOption> generateEmiOptions(ExternalApiRequestDto request, String eligibility) {
        List<ExternalApiResponseDto.EmiOption> options = new ArrayList<>();

        if ("Eligible".equals(eligibility)) {
            boolean requiresDP = request.getBureauScore() < 600;

            // Pay-in-3 option
            options.add(ExternalApiResponseDto.EmiOption.builder()
                    .tenure("Pay-in-3")
                    .type("Pay-in-3")
                    .apr("0")
                    .montly((int) Math.ceil(request.getCartValue() / 3.0))
                    .downpaymentRequired(requiresDP)
                    .build());

            // 6-month option for good scores
            if (request.getBureauScore() >= 500) {
                options.add(ExternalApiResponseDto.EmiOption.builder()
                        .tenure("6")
                        .type("Standard")
                        .apr("12")
                        .montly((int) Math.ceil(request.getCartValue() * 1.12 / 6.0))
                        .downpaymentRequired(false)
                        .build());
            }
        }

        return options;
    }

    private String generateMerchantPlan(String eligibility, Boolean downpaymentRequired) {
        if (!"Eligible".equals(eligibility)) return "decline";
        if (downpaymentRequired) return "Pay-in-3 with DP OR decline";
        return "Pay-in-3 OR Standard EMI";
    }

    private Integer calculateConfidence(ExternalApiRequestDto request) {
        int confidence = 50; // Base confidence

        if (request.getBureauScore() >= 700) confidence += 30;
        else if (request.getBureauScore() >= 500) confidence += 10;
        else confidence -= 20;

        if (request.getDpd_90_plus_last_36m() == 0) confidence += 10;
        if (request.getPincodeStabilityMonths() >= 12) confidence += 5;

        return Math.max(Math.min(confidence, 100), 0);
    }

    private Integer calculateModifierSum(ExternalApiRequestDto request) {
        int sum = 0;
        if (request.getNTC() == 1) sum -= 1;
        if (calculateAffluenceScore(request) < 30) sum -= 1;
        return sum;
    }

    private String generateAdjustments(ExternalApiRequestDto request) {
        List<String> adjustments = new ArrayList<>();
        if (request.getNTC() == 1) adjustments.add("NTC -1");
        if (calculateAffluenceScore(request) < 30) adjustments.add("affluence -1");
        return String.join(", ", adjustments);
    }

    private String formatCurrency(Double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(amount).replace("₹", "₹");
    }
}