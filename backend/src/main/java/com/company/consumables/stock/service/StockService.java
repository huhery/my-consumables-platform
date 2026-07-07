package com.company.consumables.stock.service;

import com.company.consumables.common.enums.FlowType;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.stock.entity.Stock;
import com.company.consumables.stock.entity.StockFlow;
import com.company.consumables.stock.vo.StockAdjustVo;
import com.company.consumables.stock.vo.StockFlowQueryVo;
import com.company.consumables.stock.vo.StockQueryVo;

/**
 * 类描述: 库存内核服务接口，统一封装库存增减与流水生成，供进货/出货复用
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface StockService {

    /**
     * 功能描述: 入库——写正向流水并增加库存汇总（调用方需保证事务）
     *
     * @param warehouseId 库存地点ID
     * @param goodsId     商品ID
     * @param qtyBase     入库数量（基本单位，正数）
     * @param flowType    流水类型
     * @param sourceNo    关联单据ID
     * @author honghui
     * @date 2026/06/30 14:15
     */
    void increaseStock(String warehouseId, String goodsId, int qtyBase, FlowType flowType, String sourceNo);

    /**
     * 功能描述: 出库——校验库存充足后扣减并写负向流水（调用方需保证事务，库存不足抛业务异常）
     *
     * @param warehouseId 库存地点ID
     * @param goodsId     商品ID
     * @param qtyBase     出库数量（基本单位，正数）
     * @param flowType    流水类型
     * @param sourceNo    关联单据ID
     * @author honghui
     * @date 2026/06/30 14:15
     */
    void deductStock(String warehouseId, String goodsId, int qtyBase, FlowType flowType, String sourceNo);

    /**
     * 功能描述: 手工调整库存（正负皆可）并写调整流水
     *
     * @param vo 调整入参
     * @author honghui
     * @date 2026/06/30 14:15
     */
    void adjustStock(StockAdjustVo vo);

    /**
     * 功能描述: 分页查询库存
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 14:15
     */
    PageResult<Stock> pageStock(StockQueryVo query);

    /**
     * 功能描述: 分页查询出入库流水
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 14:15
     */
    PageResult<StockFlow> pageFlow(StockFlowQueryVo query);

    /**
     * 功能描述: 查询某商品某地点当前库存数量（基本单位），无记录返回 0
     *
     * @param warehouseId 库存地点ID
     * @param goodsId     商品ID
     * @return 当前库存数量
     * @author honghui
     * @date 2026/06/30 14:15
     */
    int getQty(String warehouseId, String goodsId);
}
