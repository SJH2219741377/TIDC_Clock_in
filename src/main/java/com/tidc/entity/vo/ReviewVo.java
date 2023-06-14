package com.tidc.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审核管理Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewVo {

    /**
     * 审核类型
     */
    private String reviewType;

    /**
     * 姓名
     */
    private String nickName;

    /**
     * 班级
     */
    private String userClass;

    /**
     * 用户唯一标识
     */
    private String openId;

}
