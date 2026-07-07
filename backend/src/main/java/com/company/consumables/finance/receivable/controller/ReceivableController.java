package com.company.consumables.finance.receivable.controller;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.finance.receivable.entity.Receivable;
import com.company.consumables.finance.receivable.service.ReceivableService;
import com.company.consumables.finance.receivable.vo.ReceiptSaveVo;
import com.company.consumables.finance.receivable.vo.ReceivableQueryVo;
import com.company.consumables.finance.receivable.vo.ReceivableSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 类描述: 应收账款与销售收款 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReceivableController {

    private final ReceivableService receivableService;

    /**
     * 功能描述: 分页查询应收
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 10:40
     */
    @GetMapping("/receivable/page")
    public RestApiResultVo<PageResult<Receivable>> page(ReceivableQueryVo query) {
        return RestApiResultVo.ok(receivableService.pageReceivable(query));
    }

    /**
     * 功能描述: 销售欠款汇总（按客户）
     *
     * @return 汇总列表
     * @author honghui
     * @date 2026/07/06 10:40
     */
    @GetMapping("/receivable/summary")
    public RestApiResultVo<List<ReceivableSummaryVo>> summary() {
        return RestApiResultVo.ok(receivableService.summary());
    }

    /**
     * 功能描述: 某客户应收明细
     *
     * @param customerId 客户ID
     * @return 应收列表
     * @author honghui
     * @date 2026/07/06 10:40
     */
    @GetMapping("/receivable/customer/{customerId}")
    public RestApiResultVo<List<Receivable>> byCustomer(@PathVariable("customerId") String customerId) {
        return RestApiResultVo.ok(receivableService.listByCustomer(customerId));
    }

    /**
     * 功能描述: 登记销售收款
     *
     * @param vo 收款入参
     * @return 空结果
     * @author honghui
     * @date 2026/07/06 10:40
     */
    @PostMapping("/receipt")
    public RestApiResultVo<Void> createReceipt(@RequestBody @Valid ReceiptSaveVo vo) {
        receivableService.createReceipt(vo);
        return RestApiResultVo.ok();
    }
}
