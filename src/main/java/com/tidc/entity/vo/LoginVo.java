package com.tidc.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVo {

    private String nickName;
    private String tokenName;
    private String tokenValue;
    private List<String> roleList;

}
