package com.tidc.handler.constant;

/**
 * 常量类
 */
public class TidcConstant {

    /**
     * 打卡中
     */
    public static final int CLOCK_IN = 1;

    /**
     * 未打卡
     */
    public static final int CLOCK_OUT = 0;

    /**
     * 注册申请
     */
    public static final int REGISTRATION_APPLICATION = 100;

    /**
     * 修改个人资料申请
     */
    public static final int UPDATE_PERSONAL_INFORMATION = 200;

    /**
     * 审核通过
     */
    public static final int APPROVAL_PASSED = 10;

    /**
     * 审核不通过
     */
    public static final int APPROVAL_FAILED = 20;

    /**
     * 审核中
     */
    public static final int UNDER_APPROVAL = 30;

    /**
     * 角色 - 超级管理员
     */
    public static final String SUPER_ADMIN = "super";

    /**
     * 角色 - 普通管理员
     */
    public static final String ADMIN = "admin";

    /**
     * 角色 - 普通用户
     */
    public static final String NORMAL = "normal";

    /**
     * 排行榜类型 - 日排行榜
     */
    public static final String DAY = "day";

    /**
     * 排行榜类型 - 月排行榜
     */
    public static final String MONTH = "month";

}
