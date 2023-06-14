package com.tidc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存储的Wifi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wifi {

    @TableId
    private Long id;

    private String wifiName;

    //删除标志（0：未删除，1：已删除）
    private Integer delFlag;

    public Wifi(String wifiName) {
        this.wifiName = wifiName;
    }

}
