package com.tidc.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.tidc.component.annotate.TidcEncryptCheck;
import com.tidc.entity.dto.BaseEntityDTO;
import com.tidc.entity.dto.SuperAdminDTO;
import com.tidc.entity.dto.UserInfoDTO;
import com.tidc.entity.dto.WifiListDTO;
import com.tidc.entity.vo.*;
import com.tidc.handler.ResultData;
import com.tidc.service.AdminService;
import com.tidc.utils.ClockInUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/super/login")
    @TidcEncryptCheck(SuperAdminDTO.class)
    public ResultData<LoginVo> login(@RequestBody SuperAdminDTO superAdminDTO) {
        return adminService.login(superAdminDTO);
    }

    @GetMapping("/super/getInfo")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<UserVo> getSuperAdminInfo(BaseEntityDTO dto) {
        String loginId = StpUtil.getLoginIdAsString();
        return adminService.getAdminInfo(loginId);
    }

    @PutMapping("/info")
    @TidcEncryptCheck(UserInfoDTO.class)
    public ResultData<String> updateAdminInfo(@RequestBody UserInfoDTO userInfoDTO) {
        String openId = StpUtil.getLoginIdAsString();
        return adminService.updateAdminInfo(userInfoDTO, openId);
    }

    @PutMapping("/super/info")
    @TidcEncryptCheck(UserInfoDTO.class)
    public ResultData<String> updateSuperAdminInfo(@RequestBody UserInfoDTO userInfoDTO) {
        String account = StpUtil.getLoginIdAsString();
        return adminService.updateSuperAdminInfo(userInfoDTO, account);
    }

    @GetMapping("/reviewList")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<List<ReviewVo>> reviewList(BaseEntityDTO dto) {
        return adminService.getReviewList();
    }

    @GetMapping("/review/passed/{openId}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<String> reviewPass(@PathVariable @NotBlank(message = "openId不能为空") String openId,
                                         BaseEntityDTO dto) {
        return adminService.reviewPass(openId);
    }

    @GetMapping("/review/fail/{openId}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<String> reviewFail(@PathVariable @NotBlank(message = "openId不能为空") String openId,
                                         BaseEntityDTO dto) {
        return adminService.reviewFail(openId);
    }

    @GetMapping("/userList")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<List<UserListVo>> userList(BaseEntityDTO dto) {
        return adminService.getUserList();
    }

    @PutMapping("/super/role/{openId}/{role}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<String> updateUserRole(@PathVariable @NotBlank(message = "openId不能为空") String openId,
                                             @PathVariable @NotBlank(message = "role不能为空") String role,
                                             BaseEntityDTO dto) {
        return adminService.updateUserRole(openId, role);
    }

    @GetMapping("/stopClockIn/{openId}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<String> stopClockInByOpenId(@PathVariable @NotBlank(message = "openId不能为空") String openId,
                                                  BaseEntityDTO dto) {
        return adminService.stopClockInByOpenId(openId);
    }

    @DeleteMapping("/super/deleteUser/{openId}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<String> deleteUserByOpenId(@PathVariable @NotBlank(message = "openId不能为空") String openId,
                                                 BaseEntityDTO dto) {
        return adminService.deleteUserByOpenId(openId);
    }

    @GetMapping("/super/wifiList")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<List<WifiVo>> wifiList(BaseEntityDTO dto) {
        return adminService.getWifiList();
    }

    @DeleteMapping("/super/deleteWifi/{id}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<String> deleteWifi(@PathVariable @NotBlank(message = "WiFi的id不能为空") String id,
                                         BaseEntityDTO dto) {
        return adminService.deleteWifi(id);
    }

    @PostMapping("/super/addWifi")
    @TidcEncryptCheck(WifiListDTO.class)
    public ResultData<String> addWifi(@RequestBody WifiListDTO wifiListDTO) {
        return adminService.addWifi(wifiListDTO.getWifiNames());
    }

    @GetMapping("/super/location")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<Map<String, Double>> getLocation(BaseEntityDTO dto) {
        Map<String, Double> map = new HashMap<>();
        map.put("中心纬度", ClockInUtil.getCenterLat());
        map.put("中心经度", ClockInUtil.getCenterLon());
        return ResultData.success(map);
    }

}
