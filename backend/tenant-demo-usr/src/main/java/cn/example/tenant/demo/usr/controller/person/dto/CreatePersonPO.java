package cn.example.tenant.demo.usr.controller.person.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePersonPO {

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    private String mobile;

    private String email;
}
