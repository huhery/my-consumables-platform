package com.company.consumables.basedata.customer.mapper;

import com.company.consumables.basedata.customer.entity.Customer;
import com.company.consumables.basedata.customer.vo.CustomerQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 客户 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface CustomerMapper {

    /**
     * 功能描述: 新增客户
     *
     * @param customer 客户实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:25
     */
    int insert(Customer customer);

    /**
     * 功能描述: 更新客户
     *
     * @param customer 客户实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:25
     */
    int update(Customer customer);

    /**
     * 功能描述: 按主键物理删除
     *
     * @param sId 主键
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 13:25
     */
    int deleteById(@Param("sId") String sId);

    /**
     * 功能描述: 按主键查询
     *
     * @param sId 主键
     * @return 客户实体，可能为 null
     * @author honghui
     * @date 2026/06/30 13:25
     */
    Customer selectById(@Param("sId") String sId);

    /**
     * 功能描述: 分页查询
     *
     * @param query 查询条件
     * @return 列表
     * @author honghui
     * @date 2026/06/30 13:25
     */
    List<Customer> selectPage(CustomerQueryVo query);

    /**
     * 功能描述: 统计满足条件的总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/06/30 13:25
     */
    long countPage(CustomerQueryVo query);

    /**
     * 功能描述: 统计该客户关联的销售单数（使用中校验）
     *
     * @param customerId 客户ID
     * @return 数量
     * @author honghui
     * @date 2026/06/30 13:25
     */
    int countSaleByCustomerId(@Param("customerId") String customerId);
}
