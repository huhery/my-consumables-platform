package com.company.consumables.platform.account.mapper;

import com.company.consumables.platform.account.entity.Account;
import org.apache.ibatis.annotations.Param;

/**
 * 类描述: 账号 Mapper（平台级表，不参与租户隔离）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
public interface AccountMapper {

    /**
     * 功能描述: 新增账号
     *
     * @param account 账号实体
     * @return 影响行数
     * @author honghui
     * @date 2026/07/12 11:40
     */
    int insert(Account account);

    /**
     * 功能描述: 按登录名查询账号
     *
     * @param loginName 登录名
     * @return 账号实体，可能为 null
     * @author honghui
     * @date 2026/07/12 11:40
     */
    Account selectByLoginName(@Param("loginName") String loginName);

    /**
     * 功能描述: 统计登录名数量（唯一校验）
     *
     * @param loginName 登录名
     * @return 数量
     * @author honghui
     * @date 2026/07/12 11:40
     */
    int countByLoginName(@Param("loginName") String loginName);
}
