package cn.example.tenant.demo.usr.service.impl;

import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.common.entity.PageResult;
import cn.example.tenant.demo.usr.controller.person.dto.PersonListRO;
import cn.example.tenant.demo.usr.controller.person.dto.UpdatePersonPO;
import cn.example.tenant.demo.usr.entity.UsrOrgInfo;
import cn.example.tenant.demo.usr.entity.UsrOrgPerson;
import cn.example.tenant.demo.usr.mapper.UsrOrgInfoMapper;
import cn.example.tenant.demo.usr.mapper.UsrOrgPersonMapper;
import cn.example.tenant.demo.usr.service.OrgPersonService;
import cn.example.tenant.demo.usr.service.cmd.CreatePersonCmd;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrgPersonServiceImpl implements OrgPersonService {

    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_DISABLED = 0;

    private final UsrOrgPersonMapper personMapper;
    private final UsrOrgInfoMapper orgInfoMapper;

    @Override
    public PageResult<PersonListRO> list(Long orgId, String realNameKeyword, int pageNum, int pageSize) {
        String tenantCode = requireTenantCode();
        assertOrgInTenant(orgId, tenantCode);

        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<UsrOrgPerson> wrapper = new LambdaQueryWrapper<UsrOrgPerson>()
                .eq(UsrOrgPerson::getSysTenantCode, tenantCode)
                .eq(UsrOrgPerson::getOrgId, orgId)
                .eq(UsrOrgPerson::getPersonStatus, STATUS_NORMAL)
                .orderByDesc(UsrOrgPerson::getRAddTime);
        if (StringUtils.hasText(realNameKeyword)) {
            wrapper.like(UsrOrgPerson::getRealName, realNameKeyword);
        }
        List<UsrOrgPerson> list = personMapper.selectList(wrapper);
        PageInfo<UsrOrgPerson> pageInfo = new PageInfo<>(list);
        List<PersonListRO> records = pageInfo.getList().stream()
                .map(this::toListRO)
                .collect(Collectors.toList());
        return PageResult.of(records, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public Long createPerson(CreatePersonCmd cmd) {
        if (cmd.getTenantCode() == null) {
            throw new IllegalArgumentException("tenantCode must not be null");
        }
        assertOrgInTenant(cmd.getOrgId(), cmd.getTenantCode());

        UsrOrgPerson person = new UsrOrgPerson();
        person.setOrgId(cmd.getOrgId());
        person.setRealName(cmd.getRealName());
        person.setMobile(cmd.getMobile() != null ? cmd.getMobile() : "");
        person.setEmail(cmd.getEmail() != null ? cmd.getEmail() : "");
        person.setPersonStatus(STATUS_NORMAL);
        personMapper.insert(person);
        return person.getId();
    }

    @Override
    public void updatePerson(UpdatePersonPO po) {
        String tenantCode = requireTenantCode();
        UsrOrgPerson person = getPersonInTenant(po.getId(), tenantCode);
        if (StringUtils.hasText(po.getRealName())) {
            person.setRealName(po.getRealName());
        }
        if (po.getMobile() != null) {
            person.setMobile(po.getMobile());
        }
        if (po.getEmail() != null) {
            person.setEmail(po.getEmail());
        }
        personMapper.updateById(person);
    }

    @Override
    public void disablePerson(Long personId) {
        String tenantCode = requireTenantCode();
        UsrOrgPerson person = getPersonInTenant(personId, tenantCode);
        person.setPersonStatus(STATUS_DISABLED);
        personMapper.updateById(person);
    }

    private void assertOrgInTenant(Long orgId, String tenantCode) {
        UsrOrgInfo org = orgInfoMapper.selectOne(new LambdaQueryWrapper<UsrOrgInfo>()
                .eq(UsrOrgInfo::getId, orgId)
                .eq(UsrOrgInfo::getSysTenantCode, tenantCode)
                .eq(UsrOrgInfo::getOrgStatus, STATUS_NORMAL)
                .last("LIMIT 1"));
        if (org == null) {
            throw new IllegalArgumentException("组织不存在或不属于当前租户");
        }
    }

    private UsrOrgPerson getPersonInTenant(Long personId, String tenantCode) {
        UsrOrgPerson person = personMapper.selectOne(new LambdaQueryWrapper<UsrOrgPerson>()
                .eq(UsrOrgPerson::getId, personId)
                .eq(UsrOrgPerson::getSysTenantCode, tenantCode)
                .last("LIMIT 1"));
        if (person == null) {
            throw new IllegalArgumentException("成员不存在或不属于当前租户");
        }
        return person;
    }

    private String requireTenantCode() {
        String tenantCode = TenantUserContext.getTenantCode();
        if (tenantCode == null) {
            throw new IllegalArgumentException("租户标识缺失");
        }
        return tenantCode;
    }

    private PersonListRO toListRO(UsrOrgPerson p) {
        PersonListRO ro = new PersonListRO();
        ro.setId(p.getId());
        ro.setOrgId(p.getOrgId());
        ro.setRealName(p.getRealName());
        ro.setMobile(maskMobile(p.getMobile()));
        ro.setEmail(p.getEmail());
        ro.setPersonStatus(p.getPersonStatus());
        return ro;
    }

    private String maskMobile(String mobile) {
        if (!StringUtils.hasText(mobile) || mobile.length() < 7) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
}
