package cn.example.tenant.demo.usr.service.cmd;

import lombok.Data;

/**
 * 新增成员命令对象：Controller 将 PO 转成 Cmd 再交给 Service（参考 pubfund-zcb）。
 */
@Data
public class CreatePersonCmd {

    private String tenantCode;
    private Long orgId;
    private String realName;
    private String mobile;
    private String email;
}
