package com.company.consumables.sale.service;

import com.company.consumables.common.result.PageResult;
import com.company.consumables.sale.entity.SaleOrder;
import com.company.consumables.sale.vo.SaleDetailVo;
import com.company.consumables.sale.vo.SaleQueryVo;
import com.company.consumables.sale.vo.StoreSaleSaveVo;
import com.company.consumables.sale.vo.WholesaleSaveVo;

/**
 * 类描述: 出货（批发 + 门店散卖）服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface SaleService {

    /**
     * 功能描述: 新增批发销售单（保存为待发货，不扣库存）
     *
     * @param vo 批发入参
     * @return 销售单主键
     * @author honghui
     * @date 2026/06/30 16:00
     */
    String createWholesaleOrder(WholesaleSaveVo vo);

    /**
     * 功能描述: 批发销售单发货（校验库存充足后扣减、写出库流水、单据转已完成）
     *
     * @param saleId 销售单主键
     * @author honghui
     * @date 2026/06/30 16:00
     */
    void ship(String saleId);

    /**
     * 功能描述: 门店散卖（出库地点必须为门店，保存即出库扣库存）
     *
     * @param vo 散卖入参
     * @return 销售单主键
     * @author honghui
     * @date 2026/06/30 16:00
     */
    String createStoreSale(StoreSaleSaveVo vo);

    /**
     * 功能描述: 分页查询销售单
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 16:00
     */
    PageResult<SaleOrder> pageSale(SaleQueryVo query);

    /**
     * 功能描述: 查询销售单详情（含明细）
     *
     * @param sId 销售单主键
     * @return 详情
     * @author honghui
     * @date 2026/06/30 16:00
     */
    SaleDetailVo getDetail(String sId);

    /**
     * 功能描述: 查询送货提醒（待发货批发单，按期望送达日期升序）
     *
     * @return 待发货批发单列表
     * @author honghui
     * @date 2026/07/06 12:15
     */
    java.util.List<com.company.consumables.sale.entity.SaleOrder> deliveryReminder();
}
