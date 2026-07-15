package com.company.consumables.platform.tenant.mapper;

import com.company.consumables.platform.tenant.entity.Tenant;
import com.company.consumables.platform.tenant.vo.TenantQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 租户（商家）Mapper（平台级表，不参与租户隔离）
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/12
 */
public interface TenantMapper {

    /**
     * 功能描述: 新增租户
     *
     * @param tenant 租户实体
     * @return 影响行数
     * @author honghui
     * @date 2026/07/12 12:00
     */
    int insert(Tenant tenant);

    /**
     * 功能描述: 按主键查询租户
     *
     * @param sId 主键
     * @return 租户实体，可能为 null
     * @author honghui
     * @date 2026/07/12 12:00
     */
    Tenant selectById(@Param("sId") String sId);

    /**
     * 功能描述: 更新租户状态
     *
     * @param sId      主键
     * @param status   状态
     * @param operator 更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/07/12 12:00
     */
    int updateStatus(@Param("sId") String sId,
                     @Param("status") int status,
                     @Param("operator") String operator);

    /**
     * 功能描述: 更新租户到期日期
     *
     * @param sId        主键
     * @param expireDate 新的到期日期
     * @param operator   更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/07/12 15:00
     */
    int updateExpireDate(@Param("sId") String sId,
                         @Param("expireDate") java.util.Date expireDate,
                         @Param("operator") String operator);

    /**
     * 功能描述: 更新租户 AI 开关
     *
     * @param sId       主键
     * @param aiEnabled AI 开关：0关闭 1开启
     * @param operator  更新人
     * @return 影响行数
     * @author honghui
     * @date 2026/07/08 10:00
     */
    int updateAiEnabled(@Param("sId") String sId,
                        @Param("aiEnabled") int aiEnabled,
                        @Param("operator") String operator);

    /**
     * 功能描述: 按主键删除租户
     *
     * @param sId 主键
     * @return 影响行数
     * @author honghui
     * @date 2026/07/15 20:30
     */
    int deleteById(@Param("sId") String sId);

    /**
     * 功能描述: 分页查询租户
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/07/12 12:00
     */
    List<Tenant> selectPage(TenantQueryVo query);

    /**
     * 功能描述: 统计满足条件的租户总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/07/12 12:00
     */
    long countPage(TenantQueryVo query);
}
