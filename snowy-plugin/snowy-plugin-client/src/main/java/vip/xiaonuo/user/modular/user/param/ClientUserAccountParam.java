package vip.xiaonuo.user.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: xiaofengs
 * @description :
 * @date: 2025/2/10 21:42
 * @version: 1.0
 */
@Getter
@Setter
public class ClientUserAccountParam {
    @Schema(description = "Account", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Account Can't be empty")
    private String account;
}
