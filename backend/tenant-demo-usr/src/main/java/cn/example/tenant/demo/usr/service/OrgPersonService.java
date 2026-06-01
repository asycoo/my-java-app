package cn.example.tenant.demo.usr.service;

import cn.example.tenant.demo.common.entity.PageResult;
import cn.example.tenant.demo.usr.controller.person.dto.PersonListRO;
import cn.example.tenant.demo.usr.controller.person.dto.UpdatePersonPO;
import cn.example.tenant.demo.usr.service.cmd.CreatePersonCmd;

public interface OrgPersonService {

    PageResult<PersonListRO> list(Long orgId, String realNameKeyword, int pageNum, int pageSize);

    Long createPerson(CreatePersonCmd cmd);

    void updatePerson(UpdatePersonPO po);

    void disablePerson(Long personId);
}
