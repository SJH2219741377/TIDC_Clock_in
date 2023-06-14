package com.tidc.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.tidc.component.annotate.TidcEncryptCheck;
import com.tidc.entity.dto.BaseEntityDTO;
import com.tidc.entity.vo.RankingVo;
import com.tidc.handler.ResultData;
import com.tidc.service.DailyRecordService;
import com.tidc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
public class RankingListController {

    private final UserService userService;

    private final DailyRecordService dailyRecordService;

    @GetMapping("/list/{type}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<List<RankingVo>> rankingList(@PathVariable @NotBlank(message = "type不能为空") String type,
                                                   BaseEntityDTO dto) {
        return dailyRecordService.getRankingList(type);
    }

    @GetMapping("/level/{type}")
    @TidcEncryptCheck(BaseEntityDTO.class)
    public ResultData<Integer> getRankingLevelByOpenId(@PathVariable @NotBlank(message = "type不能为空") String type,
                                                       BaseEntityDTO dto) {
        String openId = StpUtil.getLoginIdAsString();
        return userService.getRankingByOpenId(openId, type);
    }

}
