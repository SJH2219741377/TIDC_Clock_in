package com.tidc.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.tidc.component.annotate.TidcEncryptCheck;
import com.tidc.entity.dto.BaseEntityDTO;
import com.tidc.entity.dto.PositionDTO;
import com.tidc.entity.dto.RegisterDTO;
import com.tidc.entity.dto.UserInfoDTO;
import com.tidc.entity.vo.HomeVo;
import com.tidc.entity.vo.LoginVo;
import com.tidc.entity.vo.UserVo;
import com.tidc.handler.ResultData;
import com.tidc.service.UserService;
import com.tidc.utils.WxLoginUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * @author 宋佳豪
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
public class UserController {

    private final WxLoginUtil wxLoginUtil;
    private final UserService userService;

    @GetMapping("/home")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<HomeVo> home(BaseEntityDTO dto) {
        String openId = StpUtil.getLoginIdAsString();
        return userService.getHomeData(openId);
    }

    @PostMapping("/wxLogin/{code}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<LoginVo> wxLogin(@PathVariable @NotBlank(message = "临时凭证不能为空") String code, BaseEntityDTO dto) {
        String openId = wxLoginUtil.getOpenId(code);
        return userService.wxLogin(openId);
    }

    @PostMapping("/register/{code}")
    @TidcEncryptCheck(RegisterDTO.class)
    public ResultData<String> register(@RequestBody RegisterDTO registerDTO, @PathVariable @NotBlank(message = "临时凭证不能为空") String code) {
        String openId = wxLoginUtil.getOpenId(code);
        return userService.register(registerDTO, openId);
    }

    @SaIgnore
    @GetMapping("/getInfo")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<UserVo> getUserInfo(BaseEntityDTO dto) {
        String openId = StpUtil.getLoginIdAsString();
        return userService.getUserInfo(openId);
    }

    @PutMapping("/info")
    @TidcEncryptCheck(UserInfoDTO.class)
    public ResultData<String> updateUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
        String openId = StpUtil.getLoginIdAsString();
        return userService.updateUserInfo(userInfoDTO, openId);
    }

    @PostMapping("/clockIn")
    @TidcEncryptCheck(PositionDTO.class)
    public ResultData<String> clockIn(@RequestBody PositionDTO position) {
        String openId = StpUtil.getLoginIdAsString();
        return userService.clockIn(position, openId);
    }

    @PostMapping("/clockOut")
    @TidcEncryptCheck(PositionDTO.class)
    public ResultData<String> clockOut(@RequestBody PositionDTO position) {
        String openId = StpUtil.getLoginIdAsString();
        return userService.clockOut(position, openId);
    }

}
