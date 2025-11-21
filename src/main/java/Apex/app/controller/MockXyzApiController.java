package Apex.app.controller;

import Apex.app.dto.ExternalApiResponseDto;
import Apex.app.model.CombinedUserData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/mock/api")
public class MockXyzApiController {

    @PostMapping("/xyz")
    public ResponseEntity<ExternalApiResponseDto> mockXyzApi(@RequestBody CombinedUserData request) {

        // Create EMI plans based on bureau score and cart value
        List<ExternalApiResponseDto.Plan> plans = createEmiPlans(request);

        // Create UI message based on the best plan
        ExternalApiResponseDto.UiMessage uiMessage = createUiMessage(plans.get(0), request.getCartValue());

        // Create risk explanation based on bureau score
        String riskExplanation = createRiskExplanation(request);

        // Create debug information
        ExternalApiResponseDto.Debug debug = ExternalApiResponseDto.Debug.builder()
                .pincode_stability_score(calculatePincodeStability(request))
                .thin_file_flag(request.getUniqueAccountsCount() != null && request.getUniqueAccountsCount() < 3)
                .imputed_income(request.getImputedIncome() != null ? request.getImputedIncome().intValue() : 50000)
                .build();

        // Build complete response
        ExternalApiResponseDto response = ExternalApiResponseDto.builder()
                .plans(plans)
                .ui_message(uiMessage)
                .risk_explanation(riskExplanation)
                .debug(debug)
                .build();

        return ResponseEntity.ok(response);
    }

    private List<ExternalApiResponseDto.Plan> createEmiPlans(CombinedUserData request) {
        List<ExternalApiResponseDto.Plan> plans = new ArrayList<>();
        Long cartValue = request.getCartValue();
        Integer bureauScore = request.getBureauScore();
        Boolean merchantSubvention = request.getMerchantSubventionNoCostEmiFlag();

        if (bureauScore != null && bureauScore > 650) {
            // High credit score - offer good plans
            if (Boolean.TRUE.equals(merchantSubvention)) {
                // 0% plan for merchant subvention
                plans.add(ExternalApiResponseDto.Plan.builder()
                        .tenure(3)
                        .apr(0.0)
                        .monthly((int) Math.ceil(cartValue / 3.0))
                        .reason("Merchant subvention & stable profile")
                        .build());
            }

            // Standard plans
            plans.add(ExternalApiResponseDto.Plan.builder()
                    .tenure(6)
                    .apr(6.0)
                    .monthly((int) Math.ceil(cartValue * 1.06 / 6.0))
                    .reason("Balanced plan")
                    .build());

            plans.add(ExternalApiResponseDto.Plan.builder()
                    .tenure(9)
                    .apr(10.0)
                    .monthly((int) Math.ceil(cartValue * 1.10 / 9.0))
                    .reason("Longer tenor for stable customers")
                    .build());

        } else if (bureauScore != null && bureauScore > 500) {
            // Medium credit score - limited plans
            plans.add(ExternalApiResponseDto.Plan.builder()
                    .tenure(3)
                    .apr(12.0)
                    .monthly((int) Math.ceil(cartValue * 1.12 / 3.0))
                    .reason("Short tenor plan")
                    .build());

            plans.add(ExternalApiResponseDto.Plan.builder()
                    .tenure(6)
                    .apr(15.0)
                    .monthly((int) Math.ceil(cartValue * 1.15 / 6.0))
                    .reason("Standard plan")
                    .build());

        } else {
            // Low credit score - high interest
            plans.add(ExternalApiResponseDto.Plan.builder()
                    .tenure(3)
                    .apr(18.0)
                    .monthly((int) Math.ceil(cartValue * 1.18 / 3.0))
                    .reason("High risk - short tenor only")
                    .build());
        }

        return plans;
    }

    private ExternalApiResponseDto.UiMessage createUiMessage(ExternalApiResponseDto.Plan bestPlan, Long cartValue) {
        String headline = String.format("Pay ₹%,d/mo", bestPlan.getMonthly());
        String subcopy;

        if (bestPlan.getApr() == 0) {
            subcopy = String.format("Pre-approved %d-month 0%% EMI for this purchase. Click to view plans.", bestPlan.getTenure());
        } else {
            subcopy = String.format("Pre-approved %d-month EMI for this purchase. Click to view plans.", bestPlan.getTenure());
        }

        return ExternalApiResponseDto.UiMessage.builder()
                .headline(headline)
                .subcopy(subcopy)
                .build();
    }

    private String createRiskExplanation(CombinedUserData request) {
        Integer bureauScore = request.getBureauScore();
        Integer addressChangeMonths = request.getLiveAddressInfo().getAddress_change_months();

        if (bureauScore != null && bureauScore > 700) {
            if (addressChangeMonths != null && addressChangeMonths > 6) {
                return "Low risk: stable address + high credit score; offered premium rates.";
            } else {
                return "Low risk: high credit score; offered good rates despite recent address change.";
            }
        } else if (bureauScore != null && bureauScore > 500) {
            return "Medium risk: acceptable credit score; standard rates offered.";
        } else {
            return "High risk: low credit score; limited plans with higher rates.";
        }
    }

    private Double calculatePincodeStability(CombinedUserData request) {
        // Mock calculation based on address change months
        Integer addressChangeMonths = request.getLiveAddressInfo().getAddress_change_months();
        if (addressChangeMonths == null) return 0.5;

        // More stable if address hasn't changed for longer
        if (addressChangeMonths > 12) return 0.95;
        if (addressChangeMonths > 6) return 0.86;
        if (addressChangeMonths > 3) return 0.72;
        return 0.45;
    }
}