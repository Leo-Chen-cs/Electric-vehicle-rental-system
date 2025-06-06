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

package vip.xiaonuo.area.modular.area.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 区域管理实体
 *
 * @author 胡勋
 * @date 2025/04/19 14:59
 **/

@Getter
@Setter
@TableName("areas")
public class Area {

    /** ID */
    @TableId
    @Schema(description = "ID")
    private String id;

    /** Area_Name */
    @Schema(description = "AREA_NAME")
    private String areaName;

    /** Area Type */
    @Schema(description = "AREA_TYPE")
    private String areaType;

    /** Area Coordinates */
    @Schema(description = "COORDINATES")
    private String coordinates;

    /** Is Active */
    @Schema(description = "IS_ACTIVE")
    private Boolean isActive;

    /** Area Color */
    @Schema(description = "COLOR")
    private String color;

    /** CREATED_AT */
    @Schema(description = "CREATED_AT")
    private Date createdAt;

    /** UPDATED_AT */
    @Schema(description = "UPDATED_AT")
    private Date updatedAt;
}
