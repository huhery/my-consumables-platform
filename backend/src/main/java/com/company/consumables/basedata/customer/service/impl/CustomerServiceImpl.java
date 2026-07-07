package com.company.consumables.basedata.customer.service.impl;

import com.company.consumables.basedata.customer.entity.Customer;
import com.company.consumables.basedata.customer.mapper.CustomerMapper;
import com.company.consumables.basedata.customer.service.CustomerService;
import com.company.consumables.basedata.customer.vo.CustomerQueryVo;
import com.company.consumables.basedata.customer.vo.CustomerSaveVo;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述: 客户服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    /**
     * 功能描述: 新增客户
     *
     * @param vo 入参
     * @return 主键
     * @author honghui
     * @date 2026/06/30 13:32
     */
    @Override
    public String createCustomer(CustomerSaveVo vo) {
        Customer customer = new Customer();
        customer.setSName(vo.getSName());
        customer.setSContact(ValueUtil.defaultEmpty(vo.getSContact()));
        customer.setSAddress(ValueUtil.defaultEmpty(vo.getSAddress()));
        customer.setSPhone(ValueUtil.defaultEmpty(vo.getSPhone()));
        customerMapper.insert(customer);
        return customer.getSId();
    }

    /**
     * 功能描述: 修改客户
     *
     * @param vo 入参
     * @author honghui
     * @date 2026/06/30 13:32
     */
    @Override
    public void updateCustomer(CustomerSaveVo vo) {
        Customer existing = customerMapper.selectById(vo.getSId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND);
        }
        existing.setSName(vo.getSName());
        existing.setSContact(ValueUtil.defaultEmpty(vo.getSContact()));
        existing.setSAddress(ValueUtil.defaultEmpty(vo.getSAddress()));
        existing.setSPhone(ValueUtil.defaultEmpty(vo.getSPhone()));
        customerMapper.update(existing);
    }

    /**
     * 功能描述: 删除客户，已关联销售单时拒绝
     *
     * @param sId 主键
     * @author honghui
     * @date 2026/06/30 13:32
     */
    @Override
    public void deleteCustomer(String sId) {
        Customer existing = customerMapper.selectById(sId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND);
        }
        if (customerMapper.countSaleByCustomerId(sId) > 0) {
            throw new BusinessException(ErrorCode.CUSTOMER_IN_USE);
        }
        customerMapper.deleteById(sId);
    }

    /**
     * 功能描述: 分页查询客户
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 13:32
     */
    @Override
    public PageResult<Customer> pageCustomer(CustomerQueryVo query) {
        long total = customerMapper.countPage(query);
        List<Customer> list = customerMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 按主键查询客户
     *
     * @param sId 主键
     * @return 客户实体
     * @author honghui
     * @date 2026/06/30 13:32
     */
    @Override
    public Customer getById(String sId) {
        return customerMapper.selectById(sId);
    }
}
