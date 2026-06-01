package cn.example.tenant.demo.usr.controller.org;

import cn.example.tenant.demo.common.entity.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 组织接口占位（阶段3 实现完整 CRUD），阶段2 用于验证未登录返回 401。
 */
@Tag(name = "组织管理")
@RestController
@RequestMapping("/api/usr/org")
public class OrgInfoController {

    @Operation(summary = "组织列表（占位）")
    @GetMapping("/r/list")
    public Response<List<String>> list() {
        return Response.ok(Collections.emptyList());
    }
}
