package com.company.consumables.basedata.customer.controller;

import com.company.consumables.basedata.customer.entity.Customer;
import com.company.consumables.basedata.customer.service.CustomerService;
import com.company.consumables.basedata.customer.vo.CustomerQueryVo;
import com.company.consumables.basedata.customer.vo.CustomerSaveVo;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 类描述: 客户 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 功能描述: 新增客户
     *
     * @param vo 入参
     * @return 主键
     * @author honghui
     * @date 2026/06/30 13:34
     */
    @PostMapping
    public RestApiResultVo<String> create(@RequestBody @Valid CustomerSaveVo vo) {
        return RestApiResultVo.ok(customerService.createCustomer(vo));
    }

    /**
     * 功能描述: 修改客户
     *
     * @param id 主键
     * @param vo 入参
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 13:34
     */
    @PutMapping("/{id}")
    public RestApiResultVo<Void> update(@PathVariable("id") String id, @RequestBody @Valid CustomerSaveVo vo) {
        vo.setSId(id);
        customerService.updateCustomer(vo);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 删除客户
     *
     * @param id 主键
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 13:34
     */
    @DeleteMapping("/{id}")
    public RestApiResultVo<Void> delete(@PathVariable("id") String id) {
        customerService.deleteCustomer(id);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 分页查询客户
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 13:34
     */
    @GetMapping("/page")
    public RestApiResultVo<PageResult<Customer>> page(CustomerQueryVo query) {
        return RestApiResultVo.ok(customerService.pageCustomer(query));
    }
}
