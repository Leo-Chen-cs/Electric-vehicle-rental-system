package vip.xiaonuo.client.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: xiaofengs
 * @description :
 * @date: 2025/2/11 22:37
 * @version: 1.0
 */
@Getter
@Setter
public class FeedbackParam {
    @Schema(description = "租赁id")
    private String rentalId;
    @Schema(description = "级别")
    private Integer rating;
    @Schema(description = "描述")
    private String comment;
}
