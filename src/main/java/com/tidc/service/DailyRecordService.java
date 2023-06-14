package com.tidc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tidc.entity.DailyRecord;
import com.tidc.entity.vo.RankingVo;
import com.tidc.handler.ResultData;

import java.util.List;

/**
 * @author 宋佳豪
 * @version 1.0
 */
public interface DailyRecordService extends IService<DailyRecord> {

    /**
     * 根据type获取日排行榜 or 月排行榜的列表数据
     *
     * @param type 查询类型 - day | month
     * @return 排行榜列表数据
     */
    ResultData<List<RankingVo>> getRankingList(String type);

}
