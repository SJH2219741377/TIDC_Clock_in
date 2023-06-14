package com.tidc.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 加密基类,接收时间戳和加密字段
 */
@Data
public abstract class BaseEntity {

    /**
     * 时间戳
     */
    @NotBlank(message = "时间戳不能为空")
    private String timestamp;

    /**
     * 加密字段
     */
    @NotBlank(message = "密钥不能为空")
    private String encryption;

}
