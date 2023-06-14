package com.tidc.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 首页数据Vo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeVo {

    // 打卡状态（0：未打卡，1：打卡中）
    private Integer clockStatus;
    // 在日排行榜中的排名
    private Integer ranking;
    // 今日打卡时长
    private Long todayDuration;
    // 总时长（目标时长）
    private Long totalDuration;
    //开始打卡时间
    private LocalDateTime startTime;
    //结束打卡时间
    private LocalDateTime endTime;

}
