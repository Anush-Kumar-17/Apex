package Apex.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestDto {

    @NotBlank(message = "User ID is required")
    private String user_id;

    @NotNull(message = "Cart value is required")
    private Long cart_value;

    @NotBlank(message = "Merchant category is required")
    private String merchant_category;

    @NotBlank(message = "Merchant name is required")
    private String merchant_name;

    @NotBlank(message = "Merchant sub category is required")
    private String merchant_sub_category;

    @NotNull(message = "Merchant subvention flag is required")
    private Boolean merchant_subvention_no_cost_emi_flag;

    @Valid
    @NotNull(message = "Live address info is required")
    private LiveAddressInfo live_address_info;

    @Data
    public static class LiveAddressInfo {
        @NotNull(message = "Pincode is required")
        private Integer pincode;

        @NotNull(message = "Address change months is required")
        private Integer address_change_months;
    }
}