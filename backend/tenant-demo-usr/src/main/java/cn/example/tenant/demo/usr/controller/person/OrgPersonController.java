package cn.example.tenant.demo.usr.controller.person;

import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.common.entity.PageResult;
import cn.example.tenant.demo.common.entity.Response;
import cn.example.tenant.demo.usr.controller.person.dto.CreatePersonPO;
import cn.example.tenant.demo.usr.controller.person.dto.CreatePersonRO;
import cn.example.tenant.demo.usr.controller.person.dto.PersonListRO;
import cn.example.tenant.demo.usr.controller.person.dto.UpdatePersonPO;
import cn.example.tenant.demo.usr.service.OrgPersonService;
import cn.example.tenant.demo.usr.service.cmd.CreatePersonCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 组织成员管理：PO → Cmd → Service，列表分页。
 */
@Slf4j
@Tag(name = "组织成员")
@RestController
@RequestMapping("/api/usr/person")
@RequiredArgsConstructor
public class OrgPersonController {

    private final OrgPersonService orgPersonService;

    @Operation(summary = "成员分页列表")
    @GetMapping("/r/list")
    public Response<PageResult<PersonListRO>> list(
            @RequestParam Long orgId,
            @RequestParam(required = false) String realName,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Response.ok(orgPersonService.list(orgId, realName, pageNum, pageSize));
    }

    @Operation(summary = "新增成员")
    @PostMapping("/w/create")
    public Response<CreatePersonRO> create(@RequestBody @Valid CreatePersonPO po) {
        CreatePersonCmd cmd = toCreatePersonCmd(po);
        Long personId = orgPersonService.createPerson(cmd);
        CreatePersonRO ro = new CreatePersonRO();
        ro.setPersonId(personId);
        return Response.ok(ro);
    }

    @Operation(summary = "更新成员")
    @PutMapping("/w/update")
    public Response<Void> update(@RequestBody @Valid UpdatePersonPO po) {
        orgPersonService.updatePerson(po);
        return Response.ok(null);
    }

    @Operation(summary = "停用成员")
    @PostMapping("/w/disable")
    public Response<Void> disable(@RequestParam Long personId) {
        orgPersonService.disablePerson(personId);
        return Response.ok(null);
    }

    private CreatePersonCmd toCreatePersonCmd(CreatePersonPO po) {
        CreatePersonCmd cmd = new CreatePersonCmd();
        cmd.setTenantCode(TenantUserContext.getTenantCode());
        cmd.setOrgId(po.getOrgId());
        cmd.setRealName(po.getRealName());
        cmd.setMobile(po.getMobile());
        cmd.setEmail(po.getEmail());
        return cmd;
    }
}
