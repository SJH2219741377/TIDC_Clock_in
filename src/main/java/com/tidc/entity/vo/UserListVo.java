package com.tidc.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 成员管理页面Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListVo {

    // 唯一标识
    private String openId;
    // 昵称
    private String nickname;
    // 班级
    private String userClass;
    private String avatar;
    // 用户角色
    private String role;
    // 打卡状态（0：未打卡，1：打卡中）
    private Integer clockStatus;
    //开始打卡时间
    private LocalDateTime startTime;
    //结束打卡时间
    private LocalDateTime endTime;

}
