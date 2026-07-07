package com.company.consumables.platform.tenant.service.impl;

import com.company.consumables.common.enums.RoleType;
import com.company.consumables.common.enums.TenantStatus;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.platform.account.entity.Account;
import com.company.consumables.platform.account.mapper.AccountMapper;
import com.company.consumables.platform.tenant.entity.Tenant;
import com.company.consumables.platform.tenant.mapper.TenantMapper;
import com.company.consumables.platform.tenant.service.TenantService;
import com.company.consumables.platform.tenant.vo.TenantOpenVo;
import com.company.consumables.platform.tenant.vo.TenantQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 类描述: 平台租户（商家）管理服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantMapper tenantMapper;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 功能描述: 开通商家。校验登录名全局唯一，建租户并建初始商家管理员账号（密码 BCrypt）
     *
     * @param vo 开通入参
     * @return 租户ID
     * @author honghui
     * @date 2026/07/12 12:45
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String openTenant(TenantOpenVo vo) {
        if (accountMapper.countByLoginName(vo.getLoginName()) > 0) {
            throw new BusinessException(ErrorCode.LOGIN_NAME_DUPLICATE);
        }
        // 建租户
        Tenant tenant = new Tenant();
        tenant.setSName(vo.getName());
        tenant.setIStatus(TenantStatus.ENABLED.getCode());
        tenant.setDtOpenTime(new Date());
        // 有效期：默认 1 年
        int years = (vo.getExpireYears() == null || vo.getExpireYears() <= 0) ? 1 : vo.getExpireYears();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.YEAR, years);
        tenant.setDExpireDate(cal.getTime());
        tenantMapper.insert(tenant);

        // 建初始商家管理员账号
        Account account = new Account();
        account.setSLoginName(vo.getLoginName());
        account.setSPassword(passwordEncoder.encode(vo.getPassword()));
        account.setSTenantId(tenant.getSId());
        account.setIRole(RoleType.TENANT_ADMIN.getCode());
        account.setIStatus(TenantStatus.ENABLED.getCode());
        accountMapper.insert(account);

        return tenant.getSId();
    }

    /**
     * 功能描述: 启用商家
     *
     * @param tenantId 租户ID
     * @author honghui
     * @date 2026/07/12 12:45
     */
    @Override
    public void enable(String tenantId) {
        checkExist(tenantId);
        tenantMapper.updateStatus(tenantId, TenantStatus.ENABLED.getCode(), currentOperator());
    }

    /**
     * 功能描述: 取当前操作人（平台管理员），无则用 system
     *
     * @return 操作人
     * @author honghui
     * @date 2026/07/12 12:46
     */
    private String currentOperator() {
        String user = com.company.consumables.common.context.UserContext.getCurrentUser();
        return user == null ? "system" : user;
    }

    /**
     * 功能描述: 停用商家
     *
     * @param tenantId 租户ID
     * @author honghui
     * @date 2026/07/12 12:45
     */
    @Override
    public void disable(String tenantId) {
        checkExist(tenantId);
        tenantMapper.updateStatus(tenantId, TenantStatus.DISABLED.getCode(), currentOperator());
    }

    /**
     * 功能描述: 分页查询商家
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/12 12:45
     */
    /**
     * 功能描述: 续期商家（从当前到期日或今天取较晚者开始延长 N 年）
     *
     * @param tenantId 租户ID
     * @param years    续期年数
     * @author honghui
     * @date 2026/07/12 15:05
     */
    @Override
    public void renew(String tenantId, int years) {
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ErrorCode.TENANT_NOT_FOUND);
        }
        // 从"到期日"与"今天"取较晚者开始续期（已过期的从今天算，未过期的从到期日算）
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date base = tenant.getDExpireDate();
        if (base != null && base.after(cal.getTime())) {
            cal.setTime(base);
        }
        cal.add(java.util.Calendar.YEAR, years);
        tenantMapper.updateExpireDate(tenantId, cal.getTime(), currentOperator());
    }

    @Override
    public PageResult<Tenant> pageTenant(TenantQueryVo query) {
        long total = tenantMapper.countPage(query);
        List<Tenant> list = tenantMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 校验租户存在
     *
     * @param tenantId 租户ID
     * @author honghui
     * @date 2026/07/12 12:45
     */
    private void checkExist(String tenantId) {
        if (tenantMapper.selectById(tenantId) == null) {
            throw new BusinessException(ErrorCode.TENANT_NOT_FOUND);
        }
    }
}
