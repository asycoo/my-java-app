package cn.example.tenant.demo.usr.service;

import cn.example.tenant.demo.usr.controller.org.dto.CreateOrgPO;
import cn.example.tenant.demo.usr.controller.org.dto.OrgListRO;
import cn.example.tenant.demo.usr.controller.org.dto.UpdateOrgPO;

import java.util.List;

public interface OrgInfoService {

    List<OrgListRO> list(String orgNameKeyword);

    Long create(CreateOrgPO po);

    void update(UpdateOrgPO po);

    void disable(Long orgId);
}
