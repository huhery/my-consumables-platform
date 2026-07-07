package com.company.consumables.basedata.goods.service;

import com.company.consumables.basedata.goods.entity.Goods;
import com.company.consumables.basedata.goods.entity.GoodsUnit;
import com.company.consumables.basedata.goods.vo.GoodsQueryVo;
import com.company.consumables.basedata.goods.vo.GoodsSaveVo;
import com.company.consumables.basedata.goods.vo.GoodsUnitSaveVo;
import com.company.consumables.common.result.PageResult;

import java.util.List;

/**
 * 类描述: 商品档案与包装单位换算服务接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface GoodsService {

    /**
     * 功能描述: 新增商品
     *
     * @param vo 商品入参
     * @return 商品主键
     * @author honghui
     * @date 2026/06/30 11:35
     */
    String createGoods(GoodsSaveVo vo);

    /**
     * 功能描述: 修改商品
     *
     * @param vo 商品入参（含主键）
     * @author honghui
     * @date 2026/06/30 11:35
     */
    void updateGoods(GoodsSaveVo vo);

    /**
     * 功能描述: 删除商品（已被库存或流水使用时拒绝）
     *
     * @param sId 商品主键
     * @author honghui
     * @date 2026/06/30 11:35
     */
    void deleteGoods(String sId);

    /**
     * 功能描述: 分页查询商品
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 11:35
     */
    PageResult<Goods> pageGoods(GoodsQueryVo query);

    /**
     * 功能描述: 新增商品包装单位
     *
     * @param goodsId 商品ID
     * @param vo      包装单位入参
     * @return 包装单位主键
     * @author honghui
     * @date 2026/06/30 11:35
     */
    String addUnit(String goodsId, GoodsUnitSaveVo vo);

    /**
     * 功能描述: 删除商品包装单位（该商品已有流水时拒绝）
     *
     * @param unitId 包装单位主键
     * @author honghui
     * @date 2026/06/30 11:35
     */
    void deleteUnit(String unitId);

    /**
     * 功能描述: 查询某商品的全部包装单位
     *
     * @param goodsId 商品ID
     * @return 包装单位列表
     * @author honghui
     * @date 2026/06/30 11:35
     */
    List<GoodsUnit> listUnits(String goodsId);

    /**
     * 功能描述: 解析某商品指定单位名称对应的换算率（基本单位返回 1，包装单位返回其换算率）
     *
     * @param goodsId  商品ID
     * @param unitName 单位名称
     * @return 换算率（1 单位 = N 基本单位）
     * @author honghui
     * @date 2026/06/30 14:40
     */
    int resolveConvertRate(String goodsId, String unitName);
}
