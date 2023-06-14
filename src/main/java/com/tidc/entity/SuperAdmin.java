package com.tidc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdmin {

    @TableId
    private Long id;
    private String account;
    private String accountPassword;
    //用户昵称
    private String nickname;
    //头像链接
    private String avatar;
    // 性别
    private String gender;
    //用户角色（normal：普通用户；admin：管理员, super: 超管）
    private String role;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //删除标志（0：未删除，1：已删除）
    private Integer delFlag;

}
