package com.tidc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tidc.entity.DailyRecord;
import com.tidc.entity.MonthRecord;
import com.tidc.entity.User;
import com.tidc.entity.vo.RankingVo;
import com.tidc.handler.ResultData;
import com.tidc.handler.emun.ReturnCode;
import com.tidc.handler.exception.ServiceException;
import com.tidc.mapper.DailyRecordMapper;
import com.tidc.mapper.MonthRecordMapper;
import com.tidc.mapper.UserMapper;
import com.tidc.service.DailyRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 排行榜实现类
 * @author 宋佳豪
 * @version 1.0
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DailyRecordServiceImpl extends ServiceImpl<DailyRecordMapper, DailyRecord> implements DailyRecordService {

    private final DailyRecordMapper dailyRecordMapper;
    private final MonthRecordMapper monthRecordMapper;
    private final UserMapper userMapper;

    @Override
    public ResultData<List<RankingVo>> getRankingList(String type) {
        // 根据类型查找
        if (StrUtil.isEmpty(type)) {
            throw new ServiceException(ReturnCode.FIELD_CANNOT_BE_NULL.getCode(), ReturnCode.FIELD_CANNOT_BE_NULL.getMessage());
        }
        List<RankingVo> rankingVos;
        List<User> users = userMapper.selectList(null);
        if ("day".equals(type)) {
            List<DailyRecord> dailyRecords = dailyRecordMapper.selectList(null);
            rankingVos = dailyRecords.stream()
                    .map(dailyRecord -> {
                        User user = users.stream()
                                .filter(u -> u.getOpenId().equals(dailyRecord.getUserId()))
                                .findFirst()
                                .orElseThrow(() -> new ServiceException("在榜中未找到此用户"));
                        return new RankingVo(user.getNickname(), user.getAvatar(), dailyRecord.getDuration());
                    })
                    .collect(Collectors.toList());
        } else if ("month".equals(type)) {
            List<MonthRecord> monthRecords = monthRecordMapper.selectList(null);
            rankingVos = monthRecords.stream()
                    .map(monthRecord -> {
                        User user = users.stream()
                                .filter(u -> u.getOpenId().equals(monthRecord.getUserId()))
                                .findFirst()
                                .orElseThrow(() -> new ServiceException("在榜中未找到此用户"));
                        return new RankingVo(user.getNickname(), user.getAvatar(), monthRecord.getDuration());
                    })
                    .collect(Collectors.toList());
        } else {
            throw new ServiceException(ReturnCode.FIELD_VALUE_ERROR.getCode(), ReturnCode.FIELD_VALUE_ERROR.getMessage());
        }
        return ResultData.success(rankingVos);
    }

}
