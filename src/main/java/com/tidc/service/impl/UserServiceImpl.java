package com.tidc.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tidc.entity.*;
import com.tidc.entity.dto.PositionDTO;
import com.tidc.entity.dto.RegisterDTO;
import com.tidc.entity.dto.UserInfoDTO;
import com.tidc.entity.vo.HomeVo;
import com.tidc.entity.vo.LoginVo;
import com.tidc.entity.vo.UserVo;
import com.tidc.handler.ResultData;
import com.tidc.handler.constant.TidcConstant;
import com.tidc.handler.emun.ReturnCode;
import com.tidc.handler.exception.ServiceException;
import com.tidc.handler.exception.UserNotExistException;
import com.tidc.mapper.*;
import com.tidc.service.UserService;
import com.tidc.utils.ClockInUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;


/**
 * @author 宋佳豪
 * @version 1.0
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final DailyRecordMapper dailyRecordMapper;
    private final MonthRecordMapper monthRecordMapper;
    private final RegisterMapper registerMapper;
    private final UserMapper userMapper;
    private final ReviewMapper checkMapper;
    private final WifiMapper wifiMapper;

    @Value("${avatar.default}")
    private String defaultAvatar;

    @Override
    public ResultData<LoginVo> wxLogin(String openId) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getOpenId, openId));
        if (ObjectUtil.isEmpty(user)) {
            throw new ServiceException(ReturnCode.USER_NOT_EXIST.getCode(), ReturnCode.USER_NOT_EXIST.getMessage());
        }
        StpUtil.login(openId);
        LoginVo loginVo = LoginVo.builder()
                .nickName(user.getNickname())
                .tokenName(StpUtil.getTokenName())
                .tokenValue(StpUtil.getTokenValue())
                .roleList(StpUtil.getRoleList())
                .build();
        return ResultData.success(loginVo, "登录成功");
    }

    /**
     * 打卡功能, 逻辑如下: <br>
     * 1. 打卡必须在指定范围的30米内,且连接上指定的WIFI后才能打卡 <br>
     * 2. 打卡只是更新开始打卡时间,并以此作为基准
     *
     * @param position 位置信息
     * @return 打卡信息
     */
    @Override
    @Transactional
    public ResultData<String> clockIn(PositionDTO position, String openId) {
        // 确定打卡位置是否符合 -> 使用WiFi组和位置进行比对
        List<Wifi> wifiList = wifiMapper.selectList(null);

        if (ObjectUtil.isEmpty(wifiList)) {
            return ResultData.success("WiFi列表为空,请设置WiFi列表!");
        }

        boolean isExist = wifiList.stream()
                .anyMatch(wifi -> wifi.getWifiName().equals(position.getWifiName()));
        if (!isExist) {
            return ResultData.fail(ReturnCode.WIFI_NOT_FOUND.getCode(), ReturnCode.WIFI_NOT_FOUND.getMessage());
        }
        double distance = ClockInUtil.calculateDistance(position.getLatitude(), position.getLongitude());
        if (distance >= 35.0) {
            return ResultData.fail(ReturnCode.CLOCK_IN_FAIL.getCode(), "不在打卡范围内");
        }
        // 更新开始打卡时间
        DailyRecord dailyRecord = dailyRecordMapper.selectOne(Wrappers.<DailyRecord>lambdaQuery()
                .eq(DailyRecord::getUserId, openId));

        if (dailyRecord == null) { // 无记录则创建
            dailyRecord = DailyRecord.builder()
                    .userId(openId)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now())
                    .clockStatus(TidcConstant.CLOCK_IN)
                    .build();
            dailyRecordMapper.insert(dailyRecord);
        } else {
            // 已有打卡记录
            if (TidcConstant.CLOCK_IN == dailyRecord.getClockStatus()) {
                throw new ServiceException(ReturnCode.CLOCK_IN_FAIL.getCode(), "打卡失败,目前正在打卡中");
            }
            dailyRecordMapper.update(null, Wrappers.<DailyRecord>lambdaUpdate()
                    .eq(DailyRecord::getUserId, openId)
                    .set(DailyRecord::getStartTime, LocalDateTime.now())
                    .set(DailyRecord::getClockStatus, TidcConstant.CLOCK_IN));
        }
        return ResultData.success("打卡成功");
    }

    /**
     * 退卡功能, 逻辑如下: <br>
     * 1. 退卡必须在指定范围的30米内,且连接上指定的WIFI后才能退卡 <br>
     * 2. 当打卡时间和退卡时间不是同一天时,以退卡时间当日为新的一天,并将当日打卡时长置为0分钟 <br>
     * 3. 退卡完成后,将当日时长将累计到月打卡时长中,值得注意的是,当日打卡时长置为0分钟,并不会影响月打卡时长的累计,而是将A->B的时间段完整的累计到月时长中
     *
     * @param position 位置信息
     * @return 退卡信息
     */
    @Override
    @Transactional
    public ResultData<String> clockOut(PositionDTO position, String openId) {
//         确定退卡位置是否符合 -> 使用WiFi组和位置进行比对
        List<Wifi> wifiList = wifiMapper.selectList(null);

        if (ObjectUtil.isEmpty(wifiList)) {
            return ResultData.success("WiFi列表为空,请设置WiFi列表!");
        }

        boolean isExist = wifiList.stream()
                .anyMatch(wifi -> wifi.getWifiName().equals(position.getWifiName()));
        if (!isExist) {
            return ResultData.fail(ReturnCode.WIFI_NOT_FOUND.getCode(), ReturnCode.WIFI_NOT_FOUND.getMessage());
        }
        double distance = ClockInUtil.calculateDistance(position.getLatitude(), position.getLongitude());
        if (distance >= 30.0) {
            return ResultData.fail(ReturnCode.CLOCK_OUT_FAIL.getCode(), ReturnCode.CLOCK_OUT_FAIL.getMessage());
        }
        return clockOut(openId);
    }

    /**
     * 执行退卡的内部方法
     *
     * @param openId 目标用户的唯一标识
     * @return 退卡信息
     */
    public ResultData<String> clockOut(String openId) {
        // 查找日打卡表和月打卡表,更新其中的月份和时长数据
        DailyRecord dailyRecord = dailyRecordMapper.selectOne(Wrappers.<DailyRecord>lambdaQuery()
                .eq(DailyRecord::getUserId, openId));
        MonthRecord monthRecord = monthRecordMapper.selectOne(Wrappers.<MonthRecord>lambdaQuery()
                .eq(MonthRecord::getUserId, openId));
        // 获取当前日期并格式化 -> 202305
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String month = formatter.format(localDate);

        if (ObjectUtil.isEmpty(dailyRecord)) { // 退卡时找不到用户,说明用户是首次注册,还没打卡就先退卡了
            throw new UserNotExistException("用户不存在");
        }

        // 确定打卡状态
        if (TidcConstant.CLOCK_OUT == dailyRecord.getClockStatus()) {
            throw new ServiceException(ReturnCode.CLOCK_OUT_FAIL.getCode(), "退卡失败,目前未打卡,不能退卡");
        }

        LocalDateTime startTime = dailyRecord.getStartTime();
        LocalDateTime endTime = LocalDateTime.now();

        if (endTime.isBefore(startTime)) { // 合法性判断
            return ResultData.fail(ReturnCode.CLOCK_OUT_FAIL.getCode(), ReturnCode.CLOCK_OUT_FAIL.getMessage());
        }

        boolean isSameDay = startTime.toLocalDate().equals(endTime.toLocalDate());
        Long duration = dailyRecord.getDuration();
        // 计算打卡时长(开始打卡 ~ 结束打卡)
        Duration between = Duration.between(startTime, endTime);
        long minutes = between.toMinutes();
        // 不是同一天则将当日打卡时长重置
        if (!isSameDay) {
            duration = 0L;
        }
        // 更新当日打卡时长,更新最后打卡时间、打卡状态
        duration += minutes;
        dailyRecordMapper.update(null, Wrappers.lambdaUpdate(DailyRecord.class)
                .eq(DailyRecord::getUserId, openId)
                .set(DailyRecord::getEndTime, endTime)
                .set(DailyRecord::getDuration, duration)
                .set(DailyRecord::getClockStatus, TidcConstant.CLOCK_OUT));
        // 月打卡记录不存在则创建记录
        if (ObjectUtil.isEmpty(monthRecord)) {
            MonthRecord buildMonthRecord = MonthRecord.builder()
                    .userId(openId)
                    .month(month)
                    .duration(duration)
                    .build();
            monthRecordMapper.insert(buildMonthRecord);
        } else {
            // 计算月时长
            Long monthDuration = monthRecord.getDuration();
            monthDuration += minutes;
            LambdaUpdateWrapper<MonthRecord> monthRecordLambdaUpdateWrapper = Wrappers.lambdaUpdate(MonthRecord.class)
                    .eq(MonthRecord::getUserId, openId)
                    .set(MonthRecord::getMonth, month)
                    .set(MonthRecord::getDuration, monthDuration);
            monthRecordMapper.update(null, monthRecordLambdaUpdateWrapper);
        }
        return ResultData.success("退卡成功");
    }

    /**
     * 根据传入的OpenId和type查找用户在对应排行榜中的排名
     *
     * @param openId 用户唯一标识
     * @param type   查找类型 - day(日排行榜) | month(月排行榜)
     * @return 在对应排行榜中的排名(ranking)
     */
    @Override
    public ResultData<Integer> getRankingByOpenId(String openId, String type) {
        if (StrUtil.isEmpty(type) || StrUtil.isEmpty(openId)) {
            return ResultData.fail(ReturnCode.FIELD_CANNOT_BE_NULL.getCode(), ReturnCode.FIELD_CANNOT_BE_NULL.getMessage());
        }

        // 超管无排名
        if (StpUtil.hasRole("super")) {
            return ResultData.success(-1);
        }

        if (TidcConstant.DAY.equals(type)) {
            List<DailyRecord> dailyRecords = dailyRecordMapper.selectList(
                    Wrappers.<DailyRecord>lambdaQuery()
                            .isNotNull(DailyRecord::getDuration)
                            .orderByDesc(DailyRecord::getDuration)
            );
            /*
                遍历一个 DailyRecord 类型的列表，查找其中 UserId 等于指定值的元素，并获取其在列表中的下标(排名)。
             */
            OptionalInt indexOpt = IntStream.range(0, dailyRecords.size())
                    .filter(i -> dailyRecords.get(i).getUserId().equals(openId))
                    .findFirst();
            if (indexOpt.isPresent()) {
                int ranking = indexOpt.getAsInt() + 1;
                return ResultData.success(ranking);
            } else {
                return ResultData.fail(ReturnCode.NOT_FOUND.getCode(), ReturnCode.NOT_FOUND.getMessage());
            }
        } else if (TidcConstant.MONTH.equals(type)) {
            List<MonthRecord> monthRecords = monthRecordMapper.selectList(
                    Wrappers.<MonthRecord>lambdaQuery()
                            .isNotNull(MonthRecord::getDuration)
                            .orderByDesc(MonthRecord::getDuration)
            );
            OptionalInt indexOpt = IntStream.range(0, monthRecords.size())
                    .filter(i -> monthRecords.get(i).getUserId().equals(openId))
                    .findFirst();
            if (indexOpt.isPresent()) {
                int ranking = indexOpt.getAsInt() + 1;
                return ResultData.success(ranking);
            } else {
                return ResultData.fail(ReturnCode.NOT_FOUND.getCode(), ReturnCode.NOT_FOUND.getMessage());
            }
        } else {
            return ResultData.fail(ReturnCode.FIELD_VALUE_ERROR.getCode(), ReturnCode.FIELD_VALUE_ERROR.getMessage());
        }
    }

    @Override
    @Transactional
    public ResultData<String> register(RegisterDTO registerDTO, String openId) {
        // 检查是否重复注册
        Long uCount = userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getOpenId, openId));
        if (uCount > 0) {
            throw new ServiceException(ReturnCode.REGISTER_FAIL.getCode(), ReturnCode.REGISTER_FAIL.getMessage());
        }
        Long rCount = registerMapper.selectCount(Wrappers.<RegisterTemp>lambdaQuery().eq(RegisterTemp::getOpenId, openId));
        if (rCount > 0) {
            throw new ServiceException(ReturnCode.REGISTER_REVIEW.getCode(), ReturnCode.REGISTER_REVIEW.getMessage());
        }
        // Bean拷贝 -> (DTO + openId) copy -> RegisterTemp
        RegisterTemp registerTemp = new RegisterTemp();
        BeanUtil.copyProperties(registerDTO, registerTemp);
        registerTemp.setOpenId(openId);
        // 充满希望的默认头像
        registerTemp.setAvatar(defaultAvatar);
        // 插入到注册表中
        registerMapper.insert(registerTemp);
        // 提交审核申请
        Review review = Review.builder()
                .checkType(TidcConstant.REGISTRATION_APPLICATION)
                .checkStatus(TidcConstant.UNDER_APPROVAL)
                .username(registerDTO.getNickname())
                .userOpenId(openId)
                .userClass(registerDTO.getUserClass())
                .gender(registerDTO.getGender())
                .build();
        checkMapper.insert(review);
        return ResultData.success("注册申请提交成功");
    }

    @Override
    public ResultData<UserVo> getUserInfo(String openId) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenId, openId));
        if (ObjectUtil.isEmpty(user)) {
            return ResultData.fail(ReturnCode.USER_NOT_EXIST.getCode(), "超管不可访问此接口");
        }
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
        return ResultData.success(userVo);
    }

    @Override
    public ResultData<String> updateUserInfo(UserInfoDTO userInfoDTO, String openId) {
        // 判断是否已有待审核的
        Long count = checkMapper.selectCount(Wrappers.<Review>lambdaQuery()
                .eq(Review::getUserOpenId, openId)
                .eq(Review::getCheckStatus, TidcConstant.UNDER_APPROVAL));
        if (count > 0) {
            return ResultData.fail(ReturnCode.REQUEST_REVIEW.getCode(), ReturnCode.REQUEST_REVIEW.getMessage());
        }
        String userClass = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getOpenId, openId)).getUserClass();
        Review review = Review.builder()
                .userOpenId(openId)
                .checkStatus(TidcConstant.UNDER_APPROVAL)
                .username(userInfoDTO.getNickname())
                .userClass(userClass)
                .gender(userInfoDTO.getGender())
                .checkType(TidcConstant.UPDATE_PERSONAL_INFORMATION)
                .build();
        checkMapper.insert(review);
        return ResultData.success("修改资料申请提交成功");
    }

    @Override
    public ResultData<HomeVo> getHomeData(String openId) {
        DailyRecord dailyRecord = dailyRecordMapper.selectOne(Wrappers.<DailyRecord>lambdaQuery()
                .eq(DailyRecord::getUserId, openId));
        MonthRecord monthRecord = monthRecordMapper.selectOne(Wrappers.<MonthRecord>lambdaQuery()
                .eq(MonthRecord::getUserId, openId));
        Integer ranking = -1;
        if (ObjectUtil.isNotEmpty(dailyRecord)) {
            ranking = getRankingByOpenId(openId, TidcConstant.DAY).getData();
        }
        HomeVo homeVo = HomeVo.builder()
                .clockStatus(dailyRecord != null ? dailyRecord.getClockStatus() : 0)
                .todayDuration(dailyRecord != null ? dailyRecord.getDuration() : 0L)
                .totalDuration(monthRecord != null ? monthRecord.getDuration() : 0L)
                .ranking(ranking)
                .startTime(dailyRecord != null ? dailyRecord.getStartTime() : null)
                .endTime(dailyRecord != null ? dailyRecord.getEndTime() : null)
                .build();
        return ResultData.success(homeVo);
    }

}
