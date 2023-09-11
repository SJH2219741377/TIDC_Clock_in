package com.tidc.handler.emun;

/**
 * 状态码枚举
 *
 * @author 宋佳豪
 * @version 1.0
 */
public enum ReturnCode {

    SUCCESS(200, "操作成功"),
    FAIL(999, "操作失败"),
    ERROR(500, "系统异常"),
    ENCRYPT_ERROR(600, "密钥比对错误"),
    REGISTER_FAIL(300, "注册失败,用户已存在"),
    USER_NOT_EXIST(301, "用户不存在"),
    ACCOUNT_OR_PASSWORD_ERROR(401, "账号或密码错误"),
    CLOCK_IN_FAIL(302, "打卡失败"),
    CLOCK_OUT_FAIL(303, "退卡失败"),
    FIELD_CANNOT_BE_NULL(304, "字段不能为空"),
    NOT_FOUND(305, "未查找到数据"),
    FIELD_VALUE_ERROR(306, "参数值错误"),
    FIELD_NAME_ERROR_OR_VALUE_IS_NULL(307, "缺少字段或字段值错误"),
    REGISTER_REVIEW(308, "注册申请审核中,请勿重复提交"),
    REVIEW_ERROR(310, "审核失败,未找到相关信息"),
    USER_ALREADY_EXIST(311, "用户已存在"),
    REQUEST_REVIEW(312, "申请审核中,请勿重复提交"),
    WIFI_NOT_FOUND(313, "未查找到指定WiFi"),
    FILE_FORMAT_ERROR(314, "只支持png和jpg格式的图片上传"),
    UPLOAD_FILE_MAX_EXCEED(315, "上传文件大小超过限制"),
    FILE_NOT_EMPTY(316, "图片不能为空");

    private final int code;
    private final String message;

    ReturnCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
