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
 * 每日打卡记录表(DailyRecord)表实体类
 *
 * @author 宋佳豪
 * @since 2023-05-03 19:28:21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("daily_record")
public class DailyRecord  {
    @TableId
    private Long id;

    //用户id (对应openId)
    private String userId;
    //开始打卡时间
    private LocalDateTime startTime;
    //结束打卡时间
    private LocalDateTime endTime;
    //当日打卡时长
    private Long duration;
    // 打卡状态（0：未打卡，1：打卡中）
    private Integer clockStatus;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //删除标志（0：未删除，1：已删除）
    private Integer delFlag;

}

