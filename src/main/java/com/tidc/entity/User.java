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
 * 用户表(User)表实体类
 *
 * @author 宋佳豪
 * @since 2023-05-03 19:31:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    @TableId
    private Long id;

    //用户唯一标识
    private String openId;
    //用户昵称
    private String nickname;
    //头像链接
    private String avatar;
    // 性别
    private String gender;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //用户角色（normal：普通用户；admin：管理员, super: 超管）
    private String role;
    // 院系
    private String department;
    // 班级
    private String userClass;
    // 学号
    private String userNo;
    //删除标志（0：未删除，1：已删除）
    private Integer delFlag;


}

