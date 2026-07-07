package com.company.consumables.basedata.goods.mapper;

import com.company.consumables.basedata.goods.entity.Goods;
import com.company.consumables.basedata.goods.vo.GoodsQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类描述: 商品档案 Mapper
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
public interface GoodsMapper {

    /**
     * 功能描述: 新增商品
     *
     * @param goods 商品实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 11:20
     */
    int insert(Goods goods);

    /**
     * 功能描述: 更新商品
     *
     * @param goods 商品实体
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 11:20
     */
    int update(Goods goods);

    /**
     * 功能描述: 按主键物理删除商品
     *
     * @param sId 主键
     * @return 影响行数
     * @author honghui
     * @date 2026/06/30 11:20
     */
    int deleteById(@Param("sId") String sId);

    /**
     * 功能描述: 按主键查询商品
     *
     * @param sId 主键
     * @return 商品实体，可能为 null
     * @author honghui
     * @date 2026/06/30 11:20
     */
    Goods selectById(@Param("sId") String sId);

    /**
     * 功能描述: 按商品编码统计数量（用于编码唯一校验，排除指定主键）
     *
     * @param sCode      商品编码
     * @param excludeId  需排除的主键（新增时传 null）
     * @return 数量
     * @author honghui
     * @date 2026/06/30 11:20
     */
    int countByCode(@Param("sCode") String sCode, @Param("excludeId") String excludeId);

    /**
     * 功能描述: 分页查询商品列表
     *
     * @param query 查询条件
     * @return 商品列表
     * @author honghui
     * @date 2026/06/30 11:20
     */
    List<Goods> selectPage(GoodsQueryVo query);

    /**
     * 功能描述: 统计满足条件的商品总数
     *
     * @param query 查询条件
     * @return 总数
     * @author honghui
     * @date 2026/06/30 11:20
     */
    long countPage(GoodsQueryVo query);

    /**
     * 功能描述: 统计该商品在库存表中的记录数（用于使用中校验）
     *
     * @param goodsId 商品ID
     * @return 数量
     * @author honghui
     * @date 2026/06/30 11:20
     */
    int countStockByGoodsId(@Param("goodsId") String goodsId);

    /**
     * 功能描述: 统计该商品在出入库流水中的记录数（用于使用中校验）
     *
     * @param goodsId 商品ID
     * @return 数量
     * @author honghui
     * @date 2026/06/30 11:20
     */
    int countFlowByGoodsId(@Param("goodsId") String goodsId);
}
