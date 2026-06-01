package cn.example.tenant.demo.usr.controller.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求体（PO = Parameter Object）。
 */
@Data
public class LoginPO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
