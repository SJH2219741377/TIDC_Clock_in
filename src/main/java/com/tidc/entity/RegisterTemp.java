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
 * 临时注册表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("register_temp")
public class RegisterTemp {

    @TableId
    private Long id;
    //用户唯一标识
    private String openId;
    //姓名
    private String nickname;
    //头像链接
    private String avatar;
    // 性别
    private String gender;
    // 院系
    private String department;
    // 班级
    private String userClass;
    // 学号
    private String userNo;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //删除标志（0：未删除，1：已删除）
    private Integer delFlag;

}
