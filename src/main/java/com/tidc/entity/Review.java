package com.tidc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审核表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @TableId
    private Long id;
    // 审核类型（注册申请 - 100, 修改资料申请 - 200）
    private Integer checkType;
    // 提交人名称
    private String username;
    // 审核状态（通过 - 10, 未通过 - 20,审核中 - 30）
    private Integer checkStatus;
    // 提交人的唯一标识id
    private String userOpenId;
    // 提交人的班级
    private String userClass;
    // 性别
    private String gender;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //删除标志（0：未删除，1：已删除）
    private Integer delFlag;

}
