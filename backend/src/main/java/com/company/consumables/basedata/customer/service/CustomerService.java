package com.company.consumables.basedata.customer.service;

import com.company.consumables.basedata.customer.entity.Customer;
import com.company.consumables.basedata.customer.vo.CustomerQueryVo;
import com.company.consumables.basedata.customer.vo.CustomerSaveVo;
import com.company.consumables.common.result.PageResult;

/**
 * 类描述: 客户服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface CustomerService {

    /**
     * 功能描述: 新增客户
     *
     * @param vo 入参
     * @return 主键
     * @author honghui
     * @date 2026/06/30 13:30
     */
    String createCustomer(CustomerSaveVo vo);

    /**
     * 功能描述: 修改客户
     *
     * @param vo 入参（含主键）
     * @author honghui
     * @date 2026/06/30 13:30
     */
    void updateCustomer(CustomerSaveVo vo);

    /**
     * 功能描述: 删除客户（已关联销售单时拒绝）
     *
     * @param sId 主键
     * @author honghui
     * @date 2026/06/30 13:30
     */
    void deleteCustomer(String sId);

    /**
     * 功能描述: 分页查询客户
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 13:30
     */
    PageResult<Customer> pageCustomer(CustomerQueryVo query);

    /**
     * 功能描述: 按主键查询客户（供其他模块校验使用）
     *
     * @param sId 主键
     * @return 客户实体，可能为 null
     * @author honghui
     * @date 2026/06/30 13:30
     */
    Customer getById(String sId);
}
