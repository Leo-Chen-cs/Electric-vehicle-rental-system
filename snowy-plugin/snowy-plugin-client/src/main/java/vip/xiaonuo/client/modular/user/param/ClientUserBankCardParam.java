package vip.xiaonuo.client.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: xiaofengs
 * @description :
 * @date: 2025/2/10 22:06
 * @version: 1.0
 */
@Getter
@Setter
public class ClientUserBankCardParam {

    @Schema(description = "用户")
    private String account;

    @Schema(description = "ID")
    private String bankCardId;

    @Schema(description = "bankCardExpiryMonth")
    private Integer bankCardExpiryMonth;

    @Schema(description = "bankCardExpiryYear")
    private Integer bankCardExpiryYear;

    @Schema(description = "bankCardCvv")
    private String bankCardCvv;


}
