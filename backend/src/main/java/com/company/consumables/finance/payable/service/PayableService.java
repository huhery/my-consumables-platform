package com.company.consumables.finance.payable.service;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.finance.payable.entity.Payable;
import com.company.consumables.finance.payable.vo.PayableQueryVo;
import com.company.consumables.finance.payable.vo.PayableSummaryVo;
import com.company.consumables.finance.payable.vo.PaymentSaveVo;

import java.util.List;

/**
 * 类描述: 应付账款与采购付款服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface PayableService {

    /**
     * 功能描述: 由进货单生成应付账款
     *
     * @param purchaseId  进货单ID
     * @param supplierId  供应商ID
     * @param totalAmount 应付金额（分）
     * @author honghui
     * @date 2026/07/06 11:10
     */
    void generateFromPurchase(String purchaseId, String supplierId, int totalAmount);

    /**
     * 功能描述: 登记采购付款（可选关联进货单，支持部分与多次付款）
     *
     * @param vo 付款入参
     * @author honghui
     * @date 2026/07/06 11:10
     */
    void createPayment(PaymentSaveVo vo);

    /**
     * 功能描述: 分页查询应付
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 11:10
     */
    PageResult<Payable> pagePayable(PayableQueryVo query);

    /**
     * 功能描述: 采购付款汇总（按供应商）
     *
     * @return 汇总列表
     * @author honghui
     * @date 2026/07/06 11:10
     */
    List<PayableSummaryVo> summary();

    /**
     * 功能描述: 查询某供应商应付明细
     *
     * @param supplierId 供应商ID
     * @return 应付列表
     * @author honghui
     * @date 2026/07/06 11:10
     */
    List<Payable> listBySupplier(String supplierId);
}
