package com.tidc.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {

    private String nickname;

    private String avatar;

    private String gender;

    private String role;

}
