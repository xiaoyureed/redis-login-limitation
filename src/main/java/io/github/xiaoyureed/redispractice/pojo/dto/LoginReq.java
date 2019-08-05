package io.github.xiaoyureed.redispractice.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author EX-XIAOYU003
 * Date: 2019-7-25
 */
@Data
public class LoginReq {
    @NotBlank(message = "name cannot be null")
    private String name;

    @NotBlank(message = "pwd cannot be null")
    private String pwd;
}
