package com.company.consumables.dashboard.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 类描述: 工作台首页聚合数据。金额单位为分，前端展示时转元。
 *         列表类字段仅取前几条用于首页展示，完整列表在对应功能页查看。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Data
public class DashboardSummaryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 今日收入（分） */
    private Long todayIncome;

    /** 今日支出（分） */
    private Long todayExpense;

    /** 本月销售额/收入（分） */
    private Long monthSales;

    /** 别人欠我的钱合计（分） */
    private Long totalReceivable;

    /** 欠我最多的客户 */
    private PartyAmount topDebtor;

    /** 我欠供应商的钱合计（分） */
    private Long totalPayable;

    /** 我欠最多的供应商 */
    private PartyAmount topCreditor;

    /** 要补货的商品数量（库存不足） */
    private Integer lowStockCount;

    /** 要补货的商品（前几条） */
    private List<LowStockItem> lowStockItems;

    /** 近几天要送的货数量 */
    private Integer deliveryCount;

    /** 近几天要送的货（前几条） */
    private List<DeliveryItem> deliveryItems;

    /**
     * 类描述: 往来方（客户/供应商）及金额
     *
     * @author honghui
     * @version 1.0
     * @date 2026/07/08
     */
    @Data
    public static class PartyAmount implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 名称 */
        private String name;
        /** 金额（分） */
        private Long amount;
    }

    /**
     * 类描述: 低库存商品项
     *
     * @author honghui
     * @version 1.0
     * @date 2026/07/08
     */
    @Data
    public static class LowStockItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 商品名称 */
        private String goodsName;
        /** 当前库存（基本单位） */
        private Integer qty;
    }

    /**
     * 类描述: 待送货项
     *
     * @author honghui
     * @version 1.0
     * @date 2026/07/08
     */
    @Data
    public static class DeliveryItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 销售单号 */
        private String saleNo;
        /** 期望送达日期 yyyy-MM-dd */
        private String expectDelivery;
    }
}
