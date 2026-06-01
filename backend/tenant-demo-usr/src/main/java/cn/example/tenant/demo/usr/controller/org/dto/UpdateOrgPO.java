package cn.example.tenant.demo.usr.controller.org.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrgPO {

    @NotNull(message = "组织ID不能为空")
    private Long id;

    private String orgName;

    private String orgShortName;

    private String orgRemark;
}
