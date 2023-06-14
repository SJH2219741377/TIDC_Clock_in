package com.tidc.entity.dto;

import com.tidc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用于接收经纬度、WiFi信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PositionDTO extends BaseEntity {

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    @DecimalMax(value = "180.0", message = "经度不能大于180度")
    @DecimalMin(value = "-180.0", message = "经度不能小于-180度")
    private double longitude;

    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    @DecimalMax(value = "90.0", message = "纬度不能大于90度")
    @DecimalMin(value = "-90.0", message = "纬度不能小于-90度")
    private double latitude;

    @NotBlank(message = "当前连接WiFi名称不能为空")
    private String wifiName;

}
