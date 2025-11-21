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
    private List<Plan> plans;
    private UiMessage ui_message;
    private String risk_explanation;
    private Debug debug;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Plan {
        private Integer tenure;
        private Double apr;
        private Integer monthly;
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UiMessage {
        private String headline;
        private String subcopy;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Debug {
        private Double pincode_stability_score;
        private Boolean thin_file_flag;
        private Integer imputed_income;
    }
}
