package com.company.consumables.finance.payable.controller;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.finance.payable.entity.Payable;
import com.company.consumables.finance.payable.service.PayableService;
import com.company.consumables.finance.payable.vo.PayableQueryVo;
import com.company.consumables.finance.payable.vo.PayableSummaryVo;
import com.company.consumables.finance.payable.vo.PaymentSaveVo;
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
 * 类描述: 应付账款与采购付款 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PayableController {

    private final PayableService payableService;

    /**
     * 功能描述: 分页查询应付
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 11:20
     */
    @GetMapping("/payable/page")
    public RestApiResultVo<PageResult<Payable>> page(PayableQueryVo query) {
        return RestApiResultVo.ok(payableService.pagePayable(query));
    }

    /**
     * 功能描述: 采购付款汇总（按供应商）
     *
     * @return 汇总列表
     * @author honghui
     * @date 2026/07/06 11:20
     */
    @GetMapping("/payable/summary")
    public RestApiResultVo<List<PayableSummaryVo>> summary() {
        return RestApiResultVo.ok(payableService.summary());
    }

    /**
     * 功能描述: 某供应商应付明细
     *
     * @param supplierId 供应商ID
     * @return 应付列表
     * @author honghui
     * @date 2026/07/06 11:20
     */
    @GetMapping("/payable/supplier/{supplierId}")
    public RestApiResultVo<List<Payable>> bySupplier(@PathVariable("supplierId") String supplierId) {
        return RestApiResultVo.ok(payableService.listBySupplier(supplierId));
    }

    /**
     * 功能描述: 登记采购付款
     *
     * @param vo 付款入参
     * @return 空结果
     * @author honghui
     * @date 2026/07/06 11:20
     */
    @PostMapping("/payment")
    public RestApiResultVo<Void> createPayment(@RequestBody @Valid PaymentSaveVo vo) {
        payableService.createPayment(vo);
        return RestApiResultVo.ok();
    }
}
