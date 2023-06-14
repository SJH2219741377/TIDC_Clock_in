package com.tidc.entity.dto;

import com.tidc.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 修改个人资料的DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO extends BaseEntity {

    @NotBlank(message = "姓名不能为空")
    private String nickname;
    @NotBlank(message = "性别不能为空")
    private String gender;

}
