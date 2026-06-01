package cn.example.tenant.demo.usr.controller.person.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePersonPO {

    @NotNull(message = "成员ID不能为空")
    private Long id;

    private String realName;

    private String mobile;

    private String email;
}
