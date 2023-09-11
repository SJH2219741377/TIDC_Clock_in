package com.tidc.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tidc.entity.*;
import com.tidc.entity.dto.SuperAdminDTO;
import com.tidc.entity.dto.UserInfoDTO;
import com.tidc.entity.vo.*;
import com.tidc.handler.ResultData;
import com.tidc.handler.constant.TidcConstant;
import com.tidc.handler.emun.ReturnCode;
import com.tidc.handler.exception.ServiceException;
import com.tidc.handler.exception.UserNotExistException;
import com.tidc.mapper.*;
import com.tidc.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AdminServiceImpl extends ServiceImpl<SuperAdminMapper, SuperAdmin> implements AdminService {

    private final SuperAdminMapper superAdminMapper;
    private final ReviewMapper reviewMapper;
    private final RegisterMapper registerMapper;
    private final UserMapper userMapper;
    private final DailyRecordMapper dailyRecordMapper;
    private final MonthRecordMapper monthRecordMapper;
    private final WifiMapper wifiMapper;
    private final UserServiceImpl userService;

    // 可重入锁
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public ResultData<LoginVo> login(SuperAdminDTO superAdminDTO) {
        SuperAdmin superAdmin = superAdminMapper.selectOne(Wrappers.<SuperAdmin>lambdaQuery()
                .eq(SuperAdmin::getAccount, superAdminDTO.getAccount())
                .eq(SuperAdmin::getAccountPassword, superAdminDTO.getAccountPassword()));
        if (ObjectUtil.isEmpty(superAdmin)) {
            return ResultData.fail(ReturnCode.ACCOUNT_OR_PASSWORD_ERROR.getCode(), ReturnCode.ACCOUNT_OR_PASSWORD_ERROR.getMessage());
        }
        StpUtil.login(superAdminDTO.getAccount());
        LoginVo loginVo = LoginVo.builder()
                .nickName(superAdmin.getNickname())
                .tokenName(StpUtil.getTokenName())
                .tokenValue(StpUtil.getTokenValue())
                .roleList(StpUtil.getRoleList())
                .build();
        return ResultData.success(loginVo, "登录成功");
    }

    @Override
    public ResultData<UserVo> getAdminInfo(String loginId) {
        SuperAdmin superAdmin = superAdminMapper.selectOne(Wrappers.<SuperAdmin>lambdaQuery()
                .eq(SuperAdmin::getAccount, loginId));
        if (ObjectUtil.isEmpty(superAdmin)) {
            return ResultData.fail(ReturnCode.USER_NOT_EXIST.getCode(), ReturnCode.USER_NOT_EXIST.getMessage());
        }
        UserVo userVo = BeanUtil.copyProperties(superAdmin, UserVo.class);
        return ResultData.success(userVo);
    }

    @Override
    public ResultData<List<ReviewVo>> getReviewList() {
        List<Review> reviews = reviewMapper.selectList(Wrappers.<Review>lambdaQuery().eq(Review::getCheckStatus, TidcConstant.UNDER_APPROVAL));
        List<ReviewVo> reviewVoList = reviews.stream()
                .map(review -> {
                    String type = "";
                    if (TidcConstant.REGISTRATION_APPLICATION == review.getCheckType()) {
                        type = "注册申请";
                    } else if (TidcConstant.UPDATE_PERSONAL_INFORMATION == review.getCheckType()) {
                        type = "修改资料";
                    }
                    return new ReviewVo(type, review.getUsername(), review.getUserClass(), review.getUserOpenId());
                })
                .collect(Collectors.toList());
        return ResultData.success(reviewVoList);
    }

    @Override
    @Transactional
    public ResultData<String> reviewPass(String openId) {
        Review review = reviewMapper.selectOne(Wrappers.<Review>lambdaQuery()
                .eq(Review::getUserOpenId, openId)
                .eq(Review::getCheckStatus, TidcConstant.UNDER_APPROVAL));
        if (review == null) {
            return ResultData.fail(ReturnCode.REVIEW_ERROR.getCode(), ReturnCode.REVIEW_ERROR.getMessage());
        }

        int checkType = review.getCheckType();
        if (checkType != TidcConstant.REGISTRATION_APPLICATION && checkType != TidcConstant.UPDATE_PERSONAL_INFORMATION) {
            return ResultData.fail(400,"审核类型出错");
        }

        // 根据审核类型执行相应操作
        if (checkType == TidcConstant.REGISTRATION_APPLICATION) {
            // 注册表用户插入到用户表
            RegisterTemp registerTemp = registerMapper.selectOne(Wrappers.<RegisterTemp>lambdaQuery()
                    .eq(RegisterTemp::getOpenId, openId));
            if (registerTemp == null) {
                return ResultData.fail(ReturnCode.REVIEW_ERROR.getCode(), ReturnCode.REVIEW_ERROR.getMessage());
            }
            Long userCount = userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getOpenId, openId));
            if (userCount > 0) {
                return ResultData.fail(ReturnCode.USER_ALREADY_EXIST.getCode(), ReturnCode.USER_ALREADY_EXIST.getMessage());
            }

            // 获取锁
            lock.lock();
            try {
                // 再次判断用户是否存在，避免竞争情况下出现重复插入的问题
                userCount = userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getOpenId, openId));
                if (userCount > 0) {
                    return ResultData.fail(ReturnCode.USER_ALREADY_EXIST.getCode(), ReturnCode.USER_ALREADY_EXIST.getMessage());
                }
                // 删除注册表对应信息
                registerMapper.delete(Wrappers.<RegisterTemp>lambdaQuery().eq(RegisterTemp::getOpenId, openId));
                User user = BeanUtil.copyProperties(registerTemp, User.class);
                user.setId(null); // 保证id自增,避免冲突
                userMapper.insert(user);
            } finally {
                // 释放锁
                lock.unlock();
            }

        } else {
            // 修改个人资料
            int count = userMapper.update(null, Wrappers.<User>lambdaUpdate()
                    .eq(User::getOpenId, openId)
                    .set(User::getNickname, review.getUsername())
                    .set(User::getGender, review.getGender()));
            if (count <= 0) {
                return ResultData.fail(ReturnCode.USER_NOT_EXIST.getCode(), ReturnCode.USER_NOT_EXIST.getMessage());
            }
        }

        // 更新审核记录状态
        int updateCount = reviewMapper.update(null, Wrappers.<Review>lambdaUpdate()
                .eq(Review::getUserOpenId, openId)
                .eq(Review::getCheckStatus, TidcConstant.UNDER_APPROVAL)
                .set(Review::getCheckStatus, TidcConstant.APPROVAL_PASSED));
        if (updateCount <= 0) {
            return ResultData.fail(ReturnCode.REVIEW_ERROR.getCode(), ReturnCode.REVIEW_ERROR.getMessage());
        }

        return ResultData.success();
    }


    @Override
    public ResultData<String> reviewFail(String openId) {
        int i = reviewMapper.update(null, Wrappers.<Review>lambdaUpdate()
                .eq(Review::getUserOpenId, openId)
                .eq(Review::getCheckStatus, TidcConstant.UNDER_APPROVAL)
                .set(Review::getCheckStatus, TidcConstant.APPROVAL_FAILED));
        registerMapper.delete(Wrappers.<RegisterTemp>lambdaQuery().eq(RegisterTemp::getOpenId,openId));
        if (i <= 0) throw new ServiceException(ReturnCode.REVIEW_ERROR.getCode(), ReturnCode.REVIEW_ERROR.getMessage());
        return ResultData.success();
    }

    @Override
    public ResultData<String> updateAdminInfo(UserInfoDTO userInfoDTO, String openId) {
        int count = userMapper.update(null, Wrappers.<User>lambdaUpdate()
                .eq(User::getOpenId, openId)
                .set(User::getNickname, userInfoDTO.getNickname())
                .set(User::getGender, userInfoDTO.getGender()));
        if (count <= 0) {
            throw new UserNotExistException();
        }
        return ResultData.success("修改成功");
    }

    @Override
    public ResultData<String> updateSuperAdminInfo(UserInfoDTO userInfoDTO, String account) {
        int count = superAdminMapper.update(null, Wrappers.<SuperAdmin>lambdaUpdate()
                .eq(SuperAdmin::getAccount, account)
                .set(SuperAdmin::getNickname, userInfoDTO.getNickname())
                .set(SuperAdmin::getGender, userInfoDTO.getGender()));
        if (count <= 0) {
            throw new UserNotExistException();
        }
        return ResultData.success("修改成功");
    }

    @Override
    public ResultData<String> updateUserRole(String openId, String role) {
        if (!TidcConstant.ADMIN.equals(role) && !TidcConstant.NORMAL.equals(role)) {
            throw new ServiceException("角色只能是 admin 或 normal !");
        }
        int count = userMapper.update(null, Wrappers.<User>lambdaUpdate()
                .eq(User::getOpenId, openId)
                .set(User::getRole, role));
        if (count == 0) {
            throw new UserNotExistException();
        }
        return ResultData.success("修改成功");
    }

    @Override
    @Transactional
    public ResultData<List<UserListVo>> getUserList() {
        List<User> userList = userMapper.selectList(null);
        List<DailyRecord> dailyRecordList = dailyRecordMapper.selectList(null);
        if (ObjectUtil.hasEmpty(userList, dailyRecordList)) {
            return ResultData.success(null, "暂无数据");
        }
        /*
            userList + dailyRecordList -> List<UserListVo>
            将 User 转换为 UserListVo，并填充 startTime、endTime 字段
            需要注意的是，在转换时需要根据 userId 和 openId 对应起来,
            如果不存在对应的 DailyRecord 对象，则 startTime 和 endTime 字段的值需要设置为 null。
         */
        List<UserListVo> userListVos = userList.stream()
                .map(user -> {
                    UserListVo userListVo = new UserListVo();
                    BeanUtil.copyProperties(user, userListVo);
                    DailyRecord dailyRecord = dailyRecordList.stream()
                            .filter(recode -> recode.getUserId().equals(user.getOpenId()))
                            .findFirst().orElse(null);
                    if (dailyRecord != null) {
                        userListVo.setStartTime(dailyRecord.getStartTime());
                        userListVo.setEndTime(dailyRecord.getEndTime());
                        userListVo.setClockStatus(dailyRecord.getClockStatus());
                    }
                    return userListVo;
                }).collect(Collectors.toList());
        return ResultData.success(userListVos);
    }

    @Override
    public ResultData<String> stopClockInByOpenId(String openId) { // 可能有多线程操作
        // 加锁
        lock.lock();
        try {
            return userService.clockOut(openId);
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    @Override
    public ResultData<String> deleteUserByOpenId(String openId) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getOpenId, openId));
        if (ObjectUtil.isEmpty(user)) {
            throw new UserNotExistException();
        }
        userMapper.deleteById(user);
        dailyRecordMapper.delete(Wrappers.<DailyRecord>lambdaQuery().eq(DailyRecord::getUserId, openId));
        monthRecordMapper.delete(Wrappers.<MonthRecord>lambdaQuery().eq(MonthRecord::getUserId, openId));
        registerMapper.delete(Wrappers.<RegisterTemp>lambdaQuery().eq(RegisterTemp::getOpenId, openId));
        reviewMapper.delete(Wrappers.<Review>lambdaQuery().eq(Review::getUserOpenId, openId));
        return ResultData.success("删除成功");
    }

    @Override
    public ResultData<List<WifiVo>> getWifiList() {
        List<Wifi> wifiList = wifiMapper.selectList(null);

        if (ObjectUtil.isEmpty(wifiList)) {
            return ResultData.success("暂无数据");
        }

        List<WifiVo> wifiVos = wifiList.stream()
                .map(wifi -> new WifiVo(wifi.getId(), wifi.getWifiName()))
                .collect(Collectors.toList());
        return ResultData.success(wifiVos);
    }

    @Override
    public ResultData<String> deleteWifi(String id) {
        int i = wifiMapper.deleteById(id);
        if (i <= 0) {
            throw new ServiceException(ReturnCode.WIFI_NOT_FOUND.getCode(), ReturnCode.WIFI_NOT_FOUND.getMessage());
        }
        return ResultData.success("删除成功");
    }

    /**
     * 如果表中已有删除标志为1的相同数据，则恢复该记录（即将del_flag置为0）；<br>
     * 如果表中已有未被删除的相同数据，则不进行插入操作；<br>
     * 如果表中没有相同数据，则插入该数据。
     *
     * @param wifiNames WiFi名称
     * @return 成功 or 失败
     */
    @Override
    @Transactional
    public ResultData<String> addWifi(List<String> wifiNames) {

        if (ObjectUtil.isEmpty(wifiNames)) {
            throw new ServiceException("添加的WiFi列表不能为空!");
        }

        List<Wifi> all = wifiMapper.selectAll();
        for (String wifiName : wifiNames) {
            boolean isExist = false;
            for (Wifi wifi : all) {
                //  如果表中存在被逻辑删除的相同数据，需要将其恢复
                if (wifi.getWifiName().equals(wifiName) && wifi.getDelFlag() == 1) {
                    wifiMapper.updateDelFlag(wifiName);
                    isExist = true;
                    break;
                }
                // 如果插入的数据的wifiName和表中的某条数据一致，则不插入
                if (wifi.getWifiName().equals(wifiName) && wifi.getDelFlag() == 0) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                wifiMapper.insert(new Wifi(wifiName));
            }
        }
        return ResultData.success("添加成功");
    }

}
