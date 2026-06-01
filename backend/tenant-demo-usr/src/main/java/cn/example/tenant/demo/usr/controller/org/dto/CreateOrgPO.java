package cn.example.tenant.demo.usr.controller.org.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrgPO {

    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    private String orgShortName;

    private String orgRemark;
}
