package vip.xiaonuo.rental.modular.rental.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: xiaofengs
 * @description :
 * @date: 2025/2/11 12:34
 * @version: 1.0
 */
@Getter
@Setter
public class RentParam {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "Start time")
    private Date startTime;

    @Schema(description = "Period (Hour)")
    private BigDecimal period;

    @Schema(description = "Cost")
    private BigDecimal totalCost;

    @Schema(description = "Start Location Longitude")
    private BigDecimal startLocationLongitude;

    @Schema(description = "Start Location Latitude")
    private BigDecimal startLocationLatitude;

}
