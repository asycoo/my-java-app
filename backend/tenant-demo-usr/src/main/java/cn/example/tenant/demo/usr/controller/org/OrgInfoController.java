package cn.example.tenant.demo.usr.controller.org;

import cn.example.tenant.demo.common.annotation.RequireRole;
import cn.example.tenant.demo.common.constant.UserRole;
import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.common.entity.Response;
import cn.example.tenant.demo.usr.controller.org.dto.CreateOrgPO;
import cn.example.tenant.demo.usr.controller.org.dto.OrgListRO;
import cn.example.tenant.demo.usr.controller.org.dto.UpdateOrgPO;
import cn.example.tenant.demo.usr.service.OrgInfoService;
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

import java.util.List;
import java.util.Map;

/**
 * 组织管理 REST API（扁平列表，按 {@code sys_tenant_code} 隔离）。
 */
@Slf4j
@Tag(name = "组织管理")
@RestController
@RequestMapping("/api/usr/org")
@RequiredArgsConstructor
public class OrgInfoController {

    private final OrgInfoService orgInfoService;

    @Operation(summary = "组织列表")
    @GetMapping("/r/list")
    public Response<List<OrgListRO>> list(@RequestParam(required = false) String orgName) {
        log.info("[{}] 查询组织列表, orgName={}", TenantUserContext.getTenantCode(), orgName);
        return Response.ok(orgInfoService.list(orgName));
    }

    @Operation(summary = "新增组织")
    @RequireRole(UserRole.ADMIN)
    @PostMapping("/w/create")
    public Response<Map<String, Long>> create(@RequestBody @Valid CreateOrgPO po) {
        Long orgId = orgInfoService.create(po);
        return Response.ok(Map.of("orgId", orgId));
    }

    @Operation(summary = "更新组织")
    @RequireRole(UserRole.ADMIN)
    @PutMapping("/w/update")
    public Response<Void> update(@RequestBody @Valid UpdateOrgPO po) {
        orgInfoService.update(po);
        return Response.ok(null);
    }

    @Operation(summary = "停用组织")
    @RequireRole(UserRole.ADMIN)
    @PostMapping("/w/disable")
    public Response<Void> disable(@RequestParam Long orgId) {
        orgInfoService.disable(orgId);
        return Response.ok(null);
    }
}
