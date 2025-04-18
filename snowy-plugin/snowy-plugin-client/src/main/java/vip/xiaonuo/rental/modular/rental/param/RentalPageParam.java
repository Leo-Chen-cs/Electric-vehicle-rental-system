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
import java.math.BigDecimal;
import java.util.Date;

/**
 * 租赁记录查询参数
 *
 * @author 张一风
 * @date  2025/02/10 15:04
 **/
@Getter
@Setter
public class RentalPageParam {

    /** 当前页 */
    @Schema(description = "当前页码")
    private Integer current;

    /** 每页条数 */
    @Schema(description = "每页条数")
    private Integer size;

    /** 排序字段 */
    @Schema(description = "排序字段，字段驼峰名称，如：userName")
    private String sortField;

    /** 排序方式 */
    @Schema(description = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 关键词 */
    @Schema(description = "关键词")
    private String searchKey;

    /** USER_ID */
    @Schema(description = "USER_ID")
    private String userId;

    /** SCOOTER_ID */
    @Schema(description = "SCOOTER_ID")
    private String scooterId;

    /** START_TIME开始 */
    @Schema(description = "START_TIME开始")
    private String startStartTime;

    /** START_TIME结束 */
    @Schema(description = "START_TIME结束")
    private String endStartTime;

    /** END_TIME开始 */
    @Schema(description = "END_TIME开始")
    private String startEndTime;

    /** END_TIME结束 */
    @Schema(description = "END_TIME结束")
    private String endEndTime;

    /** TOTAL_COST */
    @Schema(description = "TOTAL_COST")
    private BigDecimal totalCost;

    /** STATUS */
    @Schema(description = "STATUS")
    private String status;

}
