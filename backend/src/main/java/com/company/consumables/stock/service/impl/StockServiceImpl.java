package com.company.consumables.stock.service.impl;

import com.company.consumables.common.constant.Constant;
import com.company.consumables.common.context.UserContext;
import com.company.consumables.common.enums.FlowType;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.stock.entity.Stock;
import com.company.consumables.stock.entity.StockFlow;
import com.company.consumables.stock.mapper.StockFlowMapper;
import com.company.consumables.stock.mapper.StockMapper;
import com.company.consumables.stock.service.StockService;
import com.company.consumables.stock.vo.StockAdjustVo;
import com.company.consumables.stock.vo.StockFlowQueryVo;
import com.company.consumables.stock.vo.StockQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 类描述: 库存内核服务实现。遵循"先写流水、再更新汇总"，保证库存汇总 = 流水累加
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    /** 免登录场景默认操作人 */
    @Value("${consumables.audit.default-user:admin}")
    private String defaultUser;

    private final StockMapper stockMapper;
    private final StockFlowMapper stockFlowMapper;

    /**
     * 功能描述: 入库，写正向流水并增加库存汇总（无记录则新建）
     *
     * @param warehouseId 库存地点ID
     * @param goodsId     商品ID
     * @param qtyBase     入库数量（基本单位）
     * @param flowType    流水类型
     * @param sourceNo    关联单据ID
     * @author honghui
     * @date 2026/06/30 14:20
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseStock(String warehouseId, String goodsId, int qtyBase, FlowType flowType, String sourceNo) {
        // 写正向流水
        writeFlow(goodsId, warehouseId, flowType, qtyBase, sourceNo, Constant.EMPTY);
        // 增加库存汇总：已有记录则累加，否则新建
        Stock stock = stockMapper.selectByGoodsAndWarehouse(goodsId, warehouseId);
        if (stock == null) {
            insertStock(goodsId, warehouseId, qtyBase);
        } else {
            stockMapper.increaseQty(goodsId, warehouseId, qtyBase, resolveOperator());
        }
    }

    /**
     * 功能描述: 出库，校验库存充足后条件扣减并写负向流水，库存不足抛业务异常
     *
     * @param warehouseId 库存地点ID
     * @param goodsId     商品ID
     * @param qtyBase     出库数量（基本单位）
     * @param flowType    流水类型
     * @param sourceNo    关联单据ID
     * @author honghui
     * @date 2026/06/30 14:20
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(String warehouseId, String goodsId, int qtyBase, FlowType flowType, String sourceNo) {
        // 条件扣减，依据影响行数判断是否成功（防超卖）
        int affected = stockMapper.deductQtyIfEnough(goodsId, warehouseId, qtyBase, resolveOperator());
        if (affected == 0) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
        }
        // 出库流水变动数量为负
        writeFlow(goodsId, warehouseId, flowType, -qtyBase, sourceNo, Constant.EMPTY);
    }

    /**
     * 功能描述: 手工调整库存（正负皆可）并写调整流水
     *
     * @param vo 调整入参
     * @author honghui
     * @date 2026/06/30 14:20
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustStock(StockAdjustVo vo) {
        int changeQty = vo.getIChangeQty();
        Stock stock = stockMapper.selectByGoodsAndWarehouse(vo.getSGoodsId(), vo.getSWarehouseId());
        if (stock == null) {
            // 无库存记录时，负向调整会导致库存为负，禁止
            if (changeQty < 0) {
                throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
            }
            insertStock(vo.getSGoodsId(), vo.getSWarehouseId(), changeQty);
        } else {
            // 负向调整时校验充足
            if (changeQty < 0 && stock.getIQty() + changeQty < 0) {
                throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
            }
            stockMapper.adjustQty(vo.getSGoodsId(), vo.getSWarehouseId(), changeQty, resolveOperator());
        }
        writeFlow(vo.getSGoodsId(), vo.getSWarehouseId(), FlowType.MANUAL_ADJUST,
                changeQty, Constant.EMPTY, vo.getSReason());
    }

    /**
     * 功能描述: 分页查询库存
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 14:20
     */
    @Override
    public PageResult<Stock> pageStock(StockQueryVo query) {
        long total = stockMapper.countPage(query);
        List<Stock> list = stockMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 分页查询出入库流水
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 14:20
     */
    @Override
    public PageResult<StockFlow> pageFlow(StockFlowQueryVo query) {
        long total = stockFlowMapper.countPage(query);
        List<StockFlow> list = stockFlowMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 查询某商品某地点当前库存数量，无记录返回 0
     *
     * @param warehouseId 库存地点ID
     * @param goodsId     商品ID
     * @return 当前库存数量
     * @author honghui
     * @date 2026/06/30 14:20
     */
    @Override
    public int getQty(String warehouseId, String goodsId) {
        Stock stock = stockMapper.selectByGoodsAndWarehouse(goodsId, warehouseId);
        return stock == null ? 0 : stock.getIQty();
    }

    /**
     * 功能描述: 新建库存记录
     *
     * @param goodsId     商品ID
     * @param warehouseId 地点ID
     * @param qty         初始数量
     * @author honghui
     * @date 2026/06/30 14:20
     */
    private void insertStock(String goodsId, String warehouseId, int qty) {
        Stock stock = new Stock();
        stock.setSGoodsId(goodsId);
        stock.setSWarehouseId(warehouseId);
        stock.setIQty(qty);
        stockMapper.insert(stock);
    }

    /**
     * 功能描述: 写一条出入库流水
     *
     * @param goodsId     商品ID
     * @param warehouseId 地点ID
     * @param flowType    流水类型
     * @param changeQty   变动数量（含正负号）
     * @param sourceNo    关联单据ID
     * @param remark      备注
     * @author honghui
     * @date 2026/06/30 14:20
     */
    private void writeFlow(String goodsId, String warehouseId, FlowType flowType,
                           int changeQty, String sourceNo, String remark) {
        StockFlow flow = new StockFlow();
        flow.setSGoodsId(goodsId);
        flow.setSWarehouseId(warehouseId);
        flow.setIFlowType(flowType.getCode());
        flow.setIChangeQty(changeQty);
        flow.setSSourceNo(sourceNo);
        flow.setSRemark(remark);
        stockFlowMapper.insert(flow);
    }

    /**
     * 功能描述: 解析当前操作人，无上下文时取默认用户
     *
     * @return 操作人
     * @author honghui
     * @date 2026/06/30 14:20
     */
    private String resolveOperator() {
        String currentUser = UserContext.getCurrentUser();
        return StringUtils.hasText(currentUser) ? currentUser : defaultUser;
    }
}
