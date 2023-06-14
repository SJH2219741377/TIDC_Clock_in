package com.tidc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 打卡统计表(Statistics)表实体类 暂时无用,方便以后扩展
 *
 * @author 宋佳豪
 * @since 2023-05-03 19:29:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("statistics")
public class Statistics  {
    @TableId
    private Long id;
    //用户id
    private Long userId;
    //打卡日期
    private LocalDateTime clockInDate;
    //打卡次数
    private Integer cardCount;
    //打卡时长(分钟)
    private Integer cardDuration;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //删除标志（0：未删除，1：已删除）
    private Integer delFlag;

}

