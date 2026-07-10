package com.company.consumables.dashboard.service.impl;

import com.company.consumables.basedata.goods.entity.Goods;
import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsQueryVo;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.dashboard.service.DashboardService;
import com.company.consumables.dashboard.vo.DashboardSummaryVo;
import com.company.consumables.finance.payable.service.PayableService;
import com.company.consumables.finance.payable.vo.PayableSummaryVo;
import com.company.consumables.finance.receivable.service.ReceivableService;
import com.company.consumables.finance.receivable.vo.ReceivableSummaryVo;
import com.company.consumables.finance.report.service.ReportService;
import com.company.consumables.finance.report.vo.FundSummaryVo;
import com.company.consumables.sale.entity.SaleOrder;
import com.company.consumables.sale.service.SaleService;
import com.company.consumables.stock.entity.Stock;
import com.company.consumables.stock.service.StockService;
import com.company.consumables.stock.vo.StockQueryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述: 工作台首页聚合服务实现。只读，复用既有 Service 取数（自动经租户隔离），
 *         各子项独立异常降级为零/空，不使整个首页失败。
 *
 * @author honghui
 * @version 1.0
 * @date 2026/07/08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ReportService reportService;
    private final ReceivableService receivableService;
    private final PayableService payableService;
    private final StockService stockService;
    private final SaleService saleService;
    private final GoodsService goodsService;

    /** 低库存阈值（基本单位） */
    private static final int LOW_STOCK_THRESHOLD = 10;

    /** 列表首页展示条数 */
    private static final int TOP_N = 5;

    /** 库存取样上限 */
    private static final int STOCK_SAMPLE = 500;

    /** 商品取样上限 */
    private static final int GOODS_SAMPLE = 1000;

    /** 期望送达未填哨兵值 */
    private static final String UNSET_DATE = "1970-01-01";

    /**
     * 功能描述: 聚合工作台数据
     *
     * @return 工作台聚合数据
     * @author honghui
     * @date 2026/07/08 14:05
     */
    @Override
    public DashboardSummaryVo summary() {
        DashboardSummaryVo vo = new DashboardSummaryVo();
        fillTodayAndMonth(vo);
        fillReceivable(vo);
        fillPayable(vo);
        fillLowStock(vo);
        fillDelivery(vo);
        return vo;
    }

    /**
     * 功能描述: 今日收支与本月销售额
     *
     * @param vo 聚合数据
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private void fillTodayAndMonth(DashboardSummaryVo vo) {
        try {
            Date today = truncate(Calendar.getInstance());
            FundSummaryVo todaySummary = reportService.fundSummary(today, today);
            vo.setTodayIncome(nz(todaySummary.getIncomeTotal()));
            vo.setTodayExpense(nz(todaySummary.getExpenseTotal()));

            Calendar monthStart = Calendar.getInstance();
            monthStart.set(Calendar.DAY_OF_MONTH, 1);
            FundSummaryVo monthSummary = reportService.fundSummary(truncate(monthStart), today);
            vo.setMonthSales(nz(monthSummary.getIncomeTotal()));
        } catch (Exception e) {
            log.warn("工作台-今日/本月汇总失败：{}", e.getMessage());
            vo.setTodayIncome(0L);
            vo.setTodayExpense(0L);
            vo.setMonthSales(0L);
        }
    }

    /**
     * 功能描述: 别人欠我的钱合计与最多客户
     *
     * @param vo 聚合数据
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private void fillReceivable(DashboardSummaryVo vo) {
        try {
            List<ReceivableSummaryVo> list = receivableService.summary();
            long total = 0L;
            ReceivableSummaryVo top = null;
            for (ReceivableSummaryVo r : list) {
                int unreceived = r.getIUnreceivedAmount() == null ? 0 : r.getIUnreceivedAmount();
                total += unreceived;
                if (unreceived > 0 && (top == null || unreceived > safeInt(top.getIUnreceivedAmount()))) {
                    top = r;
                }
            }
            vo.setTotalReceivable(total);
            if (top != null) {
                vo.setTopDebtor(party(top.getSCustomerName(), top.getIUnreceivedAmount()));
            }
        } catch (Exception e) {
            log.warn("工作台-应收汇总失败：{}", e.getMessage());
            vo.setTotalReceivable(0L);
        }
    }

    /**
     * 功能描述: 我欠供应商的钱合计与最多供应商
     *
     * @param vo 聚合数据
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private void fillPayable(DashboardSummaryVo vo) {
        try {
            List<PayableSummaryVo> list = payableService.summary();
            long total = 0L;
            PayableSummaryVo top = null;
            for (PayableSummaryVo p : list) {
                int unpaid = p.getIUnpaidAmount() == null ? 0 : p.getIUnpaidAmount();
                total += unpaid;
                if (unpaid > 0 && (top == null || unpaid > safeInt(top.getIUnpaidAmount()))) {
                    top = p;
                }
            }
            vo.setTotalPayable(total);
            if (top != null) {
                vo.setTopCreditor(party(top.getSSupplierName(), top.getIUnpaidAmount()));
            }
        } catch (Exception e) {
            log.warn("工作台-应付汇总失败：{}", e.getMessage());
            vo.setTotalPayable(0L);
        }
    }

    /**
     * 功能描述: 要补货的商品（低库存前 N 条 + 总数）
     *
     * @param vo 聚合数据
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private void fillLowStock(DashboardSummaryVo vo) {
        try {
            StockQueryVo query = new StockQueryVo();
            query.setPageNum(1);
            query.setPageSize(STOCK_SAMPLE);
            PageResult<Stock> page = stockService.pageStock(query);

            Map<String, String> goodsNameMap = loadGoodsNameMap();
            List<Stock> low = new ArrayList<>();
            for (Stock s : page.getList()) {
                if (s.getIQty() != null && s.getIQty() < LOW_STOCK_THRESHOLD) {
                    low.add(s);
                }
            }
            low.sort(Comparator.comparingInt(s -> s.getIQty() == null ? 0 : s.getIQty()));
            vo.setLowStockCount(low.size());

            List<DashboardSummaryVo.LowStockItem> items = new ArrayList<>();
            for (int i = 0; i < Math.min(TOP_N, low.size()); i++) {
                Stock s = low.get(i);
                DashboardSummaryVo.LowStockItem item = new DashboardSummaryVo.LowStockItem();
                item.setGoodsName(goodsNameMap.getOrDefault(s.getSGoodsId(), s.getSGoodsId()));
                item.setQty(s.getIQty());
                items.add(item);
            }
            vo.setLowStockItems(items);
        } catch (Exception e) {
            log.warn("工作台-低库存失败：{}", e.getMessage());
            vo.setLowStockCount(0);
            vo.setLowStockItems(new ArrayList<>());
        }
    }

    /**
     * 功能描述: 近几天要送的货（待发货前 N 条 + 总数）
     *
     * @param vo 聚合数据
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private void fillDelivery(DashboardSummaryVo vo) {
        try {
            List<SaleOrder> orders = saleService.deliveryReminder();
            vo.setDeliveryCount(orders == null ? 0 : orders.size());
            List<DashboardSummaryVo.DeliveryItem> items = new ArrayList<>();
            if (orders != null) {
                for (int i = 0; i < Math.min(TOP_N, orders.size()); i++) {
                    SaleOrder o = orders.get(i);
                    DashboardSummaryVo.DeliveryItem item = new DashboardSummaryVo.DeliveryItem();
                    item.setSaleNo(o.getSSaleNo());
                    item.setExpectDelivery(formatDelivery(o.getDExpectDelivery()));
                    items.add(item);
                }
            }
            vo.setDeliveryItems(items);
        } catch (Exception e) {
            log.warn("工作台-送货提醒失败：{}", e.getMessage());
            vo.setDeliveryCount(0);
            vo.setDeliveryItems(new ArrayList<>());
        }
    }

    /**
     * 功能描述: 加载商品 id→name 映射（小微商户商品量小，取样上限内一次拉取）
     *
     * @return id→name 映射
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private Map<String, String> loadGoodsNameMap() {
        Map<String, String> map = new HashMap<>();
        try {
            GoodsQueryVo query = new GoodsQueryVo();
            query.setPageNum(1);
            query.setPageSize(GOODS_SAMPLE);
            PageResult<Goods> page = goodsService.pageGoods(query);
            for (Goods g : page.getList()) {
                map.put(g.getSId(), g.getSName());
            }
        } catch (Exception e) {
            log.warn("工作台-加载商品名失败：{}", e.getMessage());
        }
        return map;
    }

    /**
     * 功能描述: 构造往来方金额对象
     *
     * @param name   名称
     * @param amount 金额（分）
     * @return 往来方金额
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private DashboardSummaryVo.PartyAmount party(String name, Integer amount) {
        DashboardSummaryVo.PartyAmount p = new DashboardSummaryVo.PartyAmount();
        p.setName(name);
        p.setAmount((long) safeInt(amount));
        return p;
    }

    /**
     * 功能描述: 格式化期望送达日期，哨兵值返回空串
     *
     * @param date 期望送达日期
     * @return 文本
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private String formatDelivery(Date date) {
        if (date == null) {
            return "";
        }
        String s = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return UNSET_DATE.equals(s) ? "" : s;
    }

    /**
     * 功能描述: 取当天零点
     *
     * @param cal 日历
     * @return 当天零点
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private Date truncate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 功能描述: Long 空转 0
     *
     * @param v 值
     * @return 非空 long
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private long nz(Long v) {
        return v == null ? 0L : v;
    }

    /**
     * 功能描述: Integer 空转 0
     *
     * @param v 值
     * @return 非空 int
     * @author honghui
     * @date 2026/07/08 14:05
     */
    private int safeInt(Integer v) {
        return v == null ? 0 : v;
    }
}
