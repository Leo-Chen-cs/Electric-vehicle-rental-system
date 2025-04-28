/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.rental.modular.rental.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 租赁记录编辑参数
 *
 * @author 张一风
 * @date  2025/02/10 15:04
 **/
@Getter
@Setter
public class RentalEditParam {

    /** ID */
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "id不能为空")
    private String id;

    /** USER_ID */
    @Schema(description = "USER_ID")
    private String userId;

    /** SCOOTER_ID */
    @Schema(description = "SCOOTER_ID")
    private String scooterId;

    /** START_TIME */
    @Schema(description = "START_TIME")
    private Date startTime;

    /** END_TIME */
    @Schema(description = "END_TIME")
    private Date endTime;

    /** START_LOCATION_LATITUDE */
    @Schema(description = "START_LOCATION_LATITUDE")
    private BigDecimal startLocationLatitude;

    /** END_LOCATION_LATITUDE */
    @Schema(description = "END_LOCATION_LATITUDE")
    private BigDecimal endLocationLatitude;

    /** START_LOCATION_LONGITUDE */
    @Schema(description = "START_LOCATION_LONGITUDE")
    private BigDecimal startLocationLongitude;

    /** END_LOCATION_LONGITUDE */
    @Schema(description = "END_LOCATION_LONGITUDE")
    private BigDecimal endLocationLongitude;

    /** TOTAL_COST */
    @Schema(description = "TOTAL_COST")
    private BigDecimal totalCost;

    /** STATUS */
    @Schema(description = "STATUS")
    private String status;

    /** CREATED_AT */
    @Schema(description = "CREATED_AT")
    private Date createdAt;

    /** UPDATED_AT */
    @Schema(description = "UPDATED_AT")
    private Date updatedAt;

}
