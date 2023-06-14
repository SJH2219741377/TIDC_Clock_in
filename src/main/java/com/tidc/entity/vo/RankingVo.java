package com.tidc.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排行榜排名Vo对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingVo {

    private String nickName;

    private String avatar;

    // 打卡时长
    private Long duration;

}
