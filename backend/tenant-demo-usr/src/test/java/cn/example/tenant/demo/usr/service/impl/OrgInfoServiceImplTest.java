package cn.example.tenant.demo.usr.service.impl;

import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.usr.controller.org.dto.CreateOrgPO;
import cn.example.tenant.demo.usr.entity.UsrOrgInfo;
import cn.example.tenant.demo.usr.mapper.UsrOrgInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 组织服务单测：验证按租户隔离查询。
 *
 * <p>运行：{@code cd backend && mvn -pl tenant-demo-usr test}</p>
 */
@ExtendWith(MockitoExtension.class)
class OrgInfoServiceImplTest {

    @Mock
    private UsrOrgInfoMapper orgInfoMapper;

    @InjectMocks
    private OrgInfoServiceImpl orgInfoService;

    @AfterEach
    void tearDown() {
        TenantUserContext.clear();
    }

    @Test
    @DisplayName("list：查询条件必须包含当前租户")
    void list_shouldFilterByTenantCode() {
        TenantUserContext.setTenantCode("tenant_a");
        when(orgInfoMapper.selectList(any())).thenReturn(Collections.emptyList());

        orgInfoService.list(null);

        ArgumentCaptor<LambdaQueryWrapper<UsrOrgInfo>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(orgInfoMapper).selectList(captor.capture());
        assertThat(captor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("list：无租户上下文时抛异常")
    void list_withoutTenant_shouldThrow() {
        assertThatThrownBy(() -> orgInfoService.list(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("租户");
    }

    @Test
    @DisplayName("create：插入组织并返回 id")
    void create_shouldInsertOrg() {
        TenantUserContext.setTenantCode("tenant_a");
        TenantUserContext.setUserId(1L);

        CreateOrgPO po = new CreateOrgPO();
        po.setOrgName("测试组织");

        when(orgInfoMapper.insert(any(UsrOrgInfo.class))).thenAnswer(inv -> {
            UsrOrgInfo org = inv.getArgument(0);
            org.setId(100L);
            return 1;
        });

        Long id = orgInfoService.create(po);
        assertThat(id).isEqualTo(100L);
        verify(orgInfoMapper).insert(any(UsrOrgInfo.class));
    }
}
