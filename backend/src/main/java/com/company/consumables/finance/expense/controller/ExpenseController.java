package com.company.consumables.finance.expense.controller;

import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.finance.expense.entity.ExpenseCategory;
import com.company.consumables.finance.expense.service.ExpenseService;
import com.company.consumables.finance.expense.vo.ExpenseCategorySaveVo;
import com.company.consumables.finance.expense.vo.ExpenseSaveVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 类描述: 费用/收入分类与记账 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * 功能描述: 新增分类
     *
     * @param vo 分类入参
     * @return 主键
     * @author honghui
     * @date 2026/07/06 11:50
     */
    @PostMapping("/expense-category")
    public RestApiResultVo<String> createCategory(@RequestBody @Valid ExpenseCategorySaveVo vo) {
        return RestApiResultVo.ok(expenseService.createCategory(vo));
    }

    /**
     * 功能描述: 删除分类
     *
     * @param id 主键
     * @return 空结果
     * @author honghui
     * @date 2026/07/06 11:50
     */
    @DeleteMapping("/expense-category/{id}")
    public RestApiResultVo<Void> deleteCategory(@PathVariable("id") String id) {
        expenseService.deleteCategory(id);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 按类型查询分类（type 可空查全部）
     *
     * @param type 分类类型
     * @return 分类列表
     * @author honghui
     * @date 2026/07/06 11:50
     */
    @GetMapping("/expense-category")
    public RestApiResultVo<List<ExpenseCategory>> listCategory(@RequestParam(value = "type", required = false) Integer type) {
        return RestApiResultVo.ok(expenseService.listCategory(type));
    }

    /**
     * 功能描述: 登记费用支出
     *
     * @param vo 记账入参
     * @return 空结果
     * @author honghui
     * @date 2026/07/06 11:50
     */
    @PostMapping("/expense")
    public RestApiResultVo<Void> recordExpense(@RequestBody @Valid ExpenseSaveVo vo) {
        expenseService.recordExpense(vo);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 登记其他收入
     *
     * @param vo 记账入参
     * @return 空结果
     * @author honghui
     * @date 2026/07/06 11:50
     */
    @PostMapping("/income")
    public RestApiResultVo<Void> recordIncome(@RequestBody @Valid ExpenseSaveVo vo) {
        expenseService.recordIncome(vo);
        return RestApiResultVo.ok();
    }
}
