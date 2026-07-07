package com.company.consumables.finance.report.controller;

import com.company.consumables.common.result.RestApiResultVo;
import com.company.consumables.sale.entity.SaleOrder;
import com.company.consumables.sale.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类描述: 送货提醒 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
@RestController
@RequestMapping("/api/delivery-reminder")
@RequiredArgsConstructor
public class DeliveryReminderController {

    private final SaleService saleService;

    /**
     * 功能描述: 查询送货提醒（待发货批发单，按期望送达日期升序）
     *
     * @return 待发货批发单列表
     * @author honghui
     * @date 2026/07/06 12:35
     */
    @GetMapping
    public RestApiResultVo<List<SaleOrder>> list() {
        return RestApiResultVo.ok(saleService.deliveryReminder());
    }
}
