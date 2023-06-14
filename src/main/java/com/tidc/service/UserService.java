package com.tidc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tidc.entity.User;
import com.tidc.entity.dto.PositionDTO;
import com.tidc.entity.dto.RegisterDTO;
import com.tidc.entity.dto.UserInfoDTO;
import com.tidc.entity.vo.HomeVo;
import com.tidc.entity.vo.LoginVo;
import com.tidc.entity.vo.UserVo;
import com.tidc.handler.ResultData;

/**
 * @author 宋佳豪
 * @version 1.0
 */
public interface UserService extends IService<User> {

    /**
     * 普通用户微信登录
     *
     * @param openId 用户唯一标识
     * @return LoginVo(包含token信息)
     */
    ResultData<LoginVo> wxLogin(String openId);

    /**
     * 打卡功能, 逻辑如下: <br>
     * 1. 打卡必须在指定范围的30米内,且连接上指定的WIFI后才能打卡 <br>
     * 2. 打卡只是更新开始打卡时间,并以此作为基准
     *
     * @param position 位置信息
     * @param openId 用户唯一标识
     * @return 打卡信息
     */
    ResultData<String> clockIn(PositionDTO position, String openId);

    /**
     * 退卡功能, 逻辑如下: <br>
     * 1. 退卡必须在指定范围的30米内,且连接上指定的WIFI后才能退卡 <br>
     * 2. 当打卡时间和退卡时间不是同一天时,以退卡时间当日为新的一天,并将当日打卡时长置为0分钟 <br>
     * 3. 退卡完成后,将当日时长将累计到月打卡时长中,值得注意的是,当日打卡时长置为0分钟,并不会影响月打卡时长的累计,而是将A->B的时间段完整的累计到月时长中
     *
     * @param position 位置信息
     * @param openId 用户唯一标识
     * @return 退卡信息
     */
    ResultData<String> clockOut(PositionDTO position, String openId);

    /**
     * 根据传入的OpenId和type查找用户在对应排行榜中的排名
     *
     * @param openId 用户唯一标识
     * @param type   查找类型 - day(日排行榜) | month(月排行榜)
     * @return 在对应排行榜中的排名(ranking)
     */
    ResultData<Integer> getRankingByOpenId(String openId, String type);

    /**
     * 注册账号,注册后将暂存入临时注册表中,等待管理员审核通过方可成功注册
     *
     * @param registerDTO 注册信息
     * @param openId      用户唯一标识
     * @return 注册申请成功 or 失败
     */
    ResultData<String> register(RegisterDTO registerDTO, String openId);

    /**
     * 获取用户的个人信息
     *
     * @param openId 唯一标识
     * @return 个人信息
     */
    ResultData<UserVo> getUserInfo(String openId);

    /**
     * 提交用户修改个人信息申请,同时只能存在一条申请
     *
     * @param userInfoDTO 用户修改的资料信息
     * @param openId      用户唯一标识
     * @return 提交成功 or 失败
     */
    ResultData<String> updateUserInfo(UserInfoDTO userInfoDTO, String openId);

    /**
     * 获取首页数据，注意：超管没有首页
     *
     * @param openId 用户唯一标识
     * @return 首页数据
     */
    ResultData<HomeVo> getHomeData(String openId);

}
