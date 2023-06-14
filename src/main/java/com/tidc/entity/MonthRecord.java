package com.tidc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 月打卡时长记录(MonthRecord)表实体类
 *
 * @author 宋佳豪
 * @since 2023-05-03 19:29:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("month_record")
public class MonthRecord  {
    @TableId
    private Long id;

    //用户id (对应openId)
    private String userId;
    //打卡年月份（例如202306）
    private String month;
    //月打卡时长（分钟）
    private Long duration;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //删除标志（0：未删除，1：已删除）
    private Integer delFlag;


}

