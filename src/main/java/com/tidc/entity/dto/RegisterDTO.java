package com.tidc.entity.dto;

import com.tidc.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 注册DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO extends BaseEntity {

    // 姓名
    @NotBlank(message = "姓名不能为空")
    private String nickname;
    // 性别
    @NotBlank(message = "性别不能为空")
    private String gender;
    // 院系
    private String department;
    // 班级
    @NotBlank(message = "班级不能为空")
    private String userClass;
    // 学号
    private String userNo;

}
