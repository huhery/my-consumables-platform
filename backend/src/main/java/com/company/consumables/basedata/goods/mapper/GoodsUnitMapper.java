package com.company.consumables.basedata.goods.mapper;

import com.company.consumables.basedata.goods.entity.GoodsUnit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 商品包装单位换算 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface GoodsUnitMapper {

    /**
     * 功能描述: 新增包装单位
     *
     * @param goodsUnit 包装单位实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 11:25
     */
    int insert(GoodsUnit goodsUnit);

    /**
     * 功能描述: 按主键物理删除包装单位
     *
     * @param sId 主键
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 11:25
     */
    int deleteById(@Param("sId") String sId);

    /**
     * 功能描述: 按商品ID物理删除其全部包装单位
     *
     * @param goodsId 商品ID
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 11:25
     */
    int deleteByGoodsId(@Param("goodsId") String goodsId);

    /**
     * 功能描述: 按主键查询包装单位
     *
     * @param sId 主键
     * @return 包装单位实体，可能为 null
     * @author honghui
     * @date 2026/06/30 11:25
     */
    GoodsUnit selectById(@Param("sId") String sId);

    /**
     * 功能描述: 查询某商品的全部包装单位
     *
     * @param goodsId 商品ID
     * @return 包装单位列表
     * @author honghui
     * @date 2026/06/30 11:25
     */
    List<GoodsUnit> selectByGoodsId(@Param("goodsId") String goodsId);

    /**
     * 功能描述: 校验某商品下包装单位名称是否重复
     *
     * @param goodsId  商品ID
     * @param unitName 包装单位名称
     * @return 数量
     * @author honghui
     * @date 2026/06/30 11:25
     */
    int countByGoodsIdAndName(@Param("goodsId") String goodsId, @Param("unitName") String unitName);

    /**
     * 功能描述: 按商品ID与单位名称查询包装单位
     *
     * @param goodsId  商品ID
     * @param unitName 包装单位名称
     * @return 包装单位实体，可能为 null
     * @author honghui
     * @date 2026/06/30 14:42
     */
    GoodsUnit selectByGoodsIdAndName(@Param("goodsId") String goodsId, @Param("unitName") String unitName);
}
