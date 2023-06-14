package com.tidc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tidc.entity.SuperAdmin;
import com.tidc.entity.dto.SuperAdminDTO;
import com.tidc.entity.dto.UserInfoDTO;
import com.tidc.entity.vo.*;
import com.tidc.handler.ResultData;

import java.util.List;

public interface AdminService extends IService<SuperAdmin> {

    /**
     * 超管登录
     *
     * @param superAdminDTO 账号和密码 (其中,账号将作为loginId)
     * @return LoginVo(包含token信息)
     */
    ResultData<LoginVo> login(SuperAdminDTO superAdminDTO);

    /**
     * 获取超管信息
     *
     * @param loginId 账号
     * @return 超管信息
     */
    ResultData<UserVo> getAdminInfo(String loginId);

    /**
     * 获取待审核列表
     *
     * @return 审核列表
     */
    ResultData<List<ReviewVo>> getReviewList();

    /**
     * 审核通过,根据openId来查找审核表,并判断审核类型,从而进行各种操作
     *
     * @param openId 用户唯一标识
     * @return 审核通过
     */
    ResultData<String> reviewPass(String openId);

    /**
     * 审核不通过,将审核信息状态更改为未通过
     *
     * @param openId 用户唯一标识
     * @return 审核不通过
     */
    ResultData<String> reviewFail(String openId);

    /**
     * 修改普通管理员的个人资料
     *
     * @param userInfoDTO 提交的修改资料
     * @param openId      唯一标识
     * @return 成功 or 失败
     */
    ResultData<String> updateAdminInfo(UserInfoDTO userInfoDTO, String openId);

    /**
     * 修改超管的个人资料
     *
     * @param userInfoDTO 提交的修改资料
     * @param account     唯一标识,同时也是管理员的账号
     * @return 成功 or 失败
     */
    ResultData<String> updateSuperAdminInfo(UserInfoDTO userInfoDTO, String account);

    /**
     * 修改用户角色,只能由超管操作
     *
     * @param openId 目标用户的唯一标识
     * @param role   变更角色
     * @return 修改成功
     */
    ResultData<String> updateUserRole(String openId, String role);

    /**
     * 获取成员管理页面的成员列表
     *
     * @return UserListVo
     */
    ResultData<List<UserListVo>> getUserList();

    /**
     * 强制结束指定用户的打卡(使其退卡)
     *
     * @param openId 目标用户唯一标识
     * @return 成功 or 失败
     */
    ResultData<String> stopClockInByOpenId(String openId);

    /**
     * 逻辑删除用户 (超管操作)
     *
     * @param openId 目标用户唯一标识
     * @return 成功 or 失败
     */
    ResultData<String> deleteUserByOpenId(String openId);

    /**
     * 获取WiFi列表
     *
     * @return WiFi列表
     */
    ResultData<List<WifiVo>> getWifiList();

    /**
     * 删除指定id的WiFi
     *
     * @param id WiFi的id
     * @return 成功 or 失败
     */
    ResultData<String> deleteWifi(String id);

    /**
     * 批量添加WiFi
     *
     * @param wifiNames WiFi名称
     * @return 成功 or 失败
     */
    ResultData<String> addWifi(List<String> wifiNames);
}
