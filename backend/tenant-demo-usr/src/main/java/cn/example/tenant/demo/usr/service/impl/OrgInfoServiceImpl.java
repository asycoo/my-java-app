package cn.example.tenant.demo.usr.service.impl;

import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.usr.controller.org.dto.CreateOrgPO;
import cn.example.tenant.demo.usr.controller.org.dto.OrgListRO;
import cn.example.tenant.demo.usr.controller.org.dto.UpdateOrgPO;
import cn.example.tenant.demo.usr.entity.UsrOrgInfo;
import cn.example.tenant.demo.usr.mapper.UsrOrgInfoMapper;
import cn.example.tenant.demo.usr.service.OrgInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrgInfoServiceImpl implements OrgInfoService {

    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_DISABLED = 0;

    private final UsrOrgInfoMapper orgInfoMapper;

    @Override
    public List<OrgListRO> list(String orgNameKeyword) {
        String tenantCode = requireTenantCode();
        LambdaQueryWrapper<UsrOrgInfo> wrapper = new LambdaQueryWrapper<UsrOrgInfo>()
                .eq(UsrOrgInfo::getSysTenantCode, tenantCode)
                .eq(UsrOrgInfo::getOrgStatus, STATUS_NORMAL)
                .orderByDesc(UsrOrgInfo::getRAddTime);
        if (StringUtils.hasText(orgNameKeyword)) {
            wrapper.like(UsrOrgInfo::getOrgName, orgNameKeyword);
        }
        return orgInfoMapper.selectList(wrapper).stream()
                .map(this::toListRO)
                .collect(Collectors.toList());
    }

    @Override
    public Long create(CreateOrgPO po) {
        requireTenantCode(); // 确保有租户
        UsrOrgInfo org = new UsrOrgInfo();
        org.setOrgName(po.getOrgName());
        org.setOrgShortName(StringUtils.hasText(po.getOrgShortName()) ? po.getOrgShortName() : "");
        org.setOrgRemark(StringUtils.hasText(po.getOrgRemark()) ? po.getOrgRemark() : "");
        org.setOrgStatus(STATUS_NORMAL);
        orgInfoMapper.insert(org);
        return org.getId(); // insert 之后 MyBatis-Plus 会把数据库自增出来的主键写回 org 对象。
    }

    @Override
    public void update(UpdateOrgPO po) {
        String tenantCode = requireTenantCode();
        UsrOrgInfo existing = getOrgInTenant(po.getId(), tenantCode);
        if (StringUtils.hasText(po.getOrgName())) {
            existing.setOrgName(po.getOrgName());
        }
        if (po.getOrgShortName() != null) {
            existing.setOrgShortName(po.getOrgShortName());
        }
        if (po.getOrgRemark() != null) {
            existing.setOrgRemark(po.getOrgRemark());
        }
        orgInfoMapper.updateById(existing);
    }

    @Override
    public void disable(Long orgId) {
        String tenantCode = requireTenantCode();
        UsrOrgInfo existing = getOrgInTenant(orgId, tenantCode);
        existing.setOrgStatus(STATUS_DISABLED);
        orgInfoMapper.updateById(existing);
    }

    private UsrOrgInfo getOrgInTenant(Long orgId, String tenantCode) {
        UsrOrgInfo org = orgInfoMapper.selectOne(new LambdaQueryWrapper<UsrOrgInfo>()
                .eq(UsrOrgInfo::getId, orgId)
                .eq(UsrOrgInfo::getSysTenantCode, tenantCode)
                .last("LIMIT 1"));
        if (org == null) {
            throw new IllegalArgumentException("组织不存在或不属于当前租户");
        }
        return org;
    }

    private String requireTenantCode() {
        String tenantCode = TenantUserContext.getTenantCode();
        if (tenantCode == null) {
            throw new IllegalArgumentException("租户标识缺失");
        }
        return tenantCode;
    }

    private OrgListRO toListRO(UsrOrgInfo org) {
        OrgListRO ro = new OrgListRO();
        ro.setId(org.getId());
        ro.setOrgName(org.getOrgName());
        ro.setOrgShortName(org.getOrgShortName());
        ro.setOrgRemark(org.getOrgRemark());
        ro.setOrgStatus(org.getOrgStatus());
        ro.setRAddTime(org.getRAddTime());
        return ro;
    }
}
