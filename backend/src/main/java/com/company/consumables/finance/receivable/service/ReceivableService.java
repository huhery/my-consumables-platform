package com.company.consumables.finance.receivable.service;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.finance.receivable.entity.Receivable;
import com.company.consumables.finance.receivable.vo.ReceiptSaveVo;
import com.company.consumables.finance.receivable.vo.ReceivableQueryVo;
import com.company.consumables.finance.receivable.vo.ReceivableSummaryVo;

import java.util.List;

/**
 * 类描述: 应收账款与销售收款服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/06
 */
public interface ReceivableService {

    /**
     * 功能描述: 由批发销售单生成应收账款（散卖不生成）
     *
     * @param saleId      销售单ID
     * @param customerId  客户ID
     * @param totalAmount 应收金额（分）
     * @author honghui
     * @date 2026/07/06 10:30
     */
    void generateFromSale(String saleId, String customerId, int totalAmount);

    /**
     * 功能描述: 登记销售收款（可选关联销售单，支持部分与多次收款）
     *
     * @param vo 收款入参
     * @author honghui
     * @date 2026/07/06 10:30
     */
    void createReceipt(ReceiptSaveVo vo);

    /**
     * 功能描述: 分页查询应收
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/07/06 10:30
     */
    PageResult<Receivable> pageReceivable(ReceivableQueryVo query);

    /**
     * 功能描述: 销售欠款汇总（按客户）
     *
     * @return 汇总列表
     * @author honghui
     * @date 2026/07/06 10:30
     */
    List<ReceivableSummaryVo> summary();

    /**
     * 功能描述: 查询某客户应收明细
     *
     * @param customerId 客户ID
     * @return 应收列表
     * @author honghui
     * @date 2026/07/06 10:30
     */
    List<Receivable> listByCustomer(String customerId);
}
