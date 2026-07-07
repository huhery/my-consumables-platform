package com.company.consumables.basedata.goods.service.impl;

import com.company.consumables.basedata.goods.entity.Goods;
import com.company.consumables.basedata.goods.entity.GoodsUnit;
import com.company.consumables.basedata.goods.mapper.GoodsMapper;
import com.company.consumables.basedata.goods.mapper.GoodsUnitMapper;
import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsQueryVo;
import com.company.consumables.basedata.goods.vo.GoodsSaveVo;
import com.company.consumables.basedata.goods.vo.GoodsUnitSaveVo;
import com.company.consumables.common.constant.Constant;
import com.company.consumables.common.error.ErrorCode;
import com.company.consumables.common.exception.BusinessException;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 类描述: 商品档案与包装单位换算服务实现
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    /** 启用状态 */
    private static final int STATUS_ENABLED = 1;

    private final GoodsMapper goodsMapper;
    private final GoodsUnitMapper goodsUnitMapper;

    /**
     * 功能描述: 新增商品，校验编码唯一
     *
     * @param vo 商品入参
     * @return 商品主键
     * @author honghui
     * @date 2026/06/30 11:40
     */
    @Override
    public String createGoods(GoodsSaveVo vo) {
        if (goodsMapper.countByCode(vo.getSCode(), null) > 0) {
            throw new BusinessException(ErrorCode.GOODS_CODE_DUPLICATE);
        }
        Goods goods = new Goods();
        goods.setSCode(vo.getSCode());
        goods.setSName(vo.getSName());
        goods.setSCategory(ValueUtil.defaultEmpty(vo.getSCategory()));
        goods.setSSpec(ValueUtil.defaultEmpty(vo.getSSpec()));
        goods.setSBaseUnit(vo.getSBaseUnit());
        goods.setIStatus(STATUS_ENABLED);
        goodsMapper.insert(goods);
        return goods.getSId();
    }

    /**
     * 功能描述: 修改商品，校验编码唯一；已被使用时禁改基本单位
     *
     * @param vo 商品入参
     * @author honghui
     * @date 2026/06/30 11:40
     */
    @Override
    public void updateGoods(GoodsSaveVo vo) {
        Goods existing = goodsMapper.selectById(vo.getSId());
        if (existing == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }
        if (goodsMapper.countByCode(vo.getSCode(), vo.getSId()) > 0) {
            throw new BusinessException(ErrorCode.GOODS_CODE_DUPLICATE);
        }
        // 已有库存或流水时，禁止修改基本单位
        boolean baseUnitChanged = !existing.getSBaseUnit().equals(vo.getSBaseUnit());
        if (baseUnitChanged && isGoodsInUse(vo.getSId())) {
            throw new BusinessException(ErrorCode.GOODS_BASE_UNIT_LOCKED);
        }
        existing.setSCode(vo.getSCode());
        existing.setSName(vo.getSName());
        existing.setSCategory(ValueUtil.defaultEmpty(vo.getSCategory()));
        existing.setSSpec(ValueUtil.defaultEmpty(vo.getSSpec()));
        existing.setSBaseUnit(vo.getSBaseUnit());
        goodsMapper.update(existing);
    }

    /**
     * 功能描述: 删除商品，已被库存或流水使用时拒绝，同时清理其包装单位
     *
     * @param sId 商品主键
     * @author honghui
     * @date 2026/06/30 11:40
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoods(String sId) {
        Goods existing = goodsMapper.selectById(sId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }
        if (isGoodsInUse(sId)) {
            throw new BusinessException(ErrorCode.GOODS_IN_USE);
        }
        goodsUnitMapper.deleteByGoodsId(sId);
        goodsMapper.deleteById(sId);
    }

    /**
     * 功能描述: 分页查询商品
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 11:40
     */
    @Override
    public PageResult<Goods> pageGoods(GoodsQueryVo query) {
        long total = goodsMapper.countPage(query);
        List<Goods> list = goodsMapper.selectPage(query);
        return PageResult.of(total, list);
    }

    /**
     * 功能描述: 新增商品包装单位，校验商品存在、换算率大于0、名称不重复
     *
     * @param goodsId 商品ID
     * @param vo      包装单位入参
     * @return 包装单位主键
     * @author honghui
     * @date 2026/06/30 11:40
     */
    @Override
    public String addUnit(String goodsId, GoodsUnitSaveVo vo) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }
        if (vo.getIConvertRate() == null || vo.getIConvertRate() <= 0) {
            throw new BusinessException(ErrorCode.UNIT_RATE_INVALID);
        }
        if (goodsUnitMapper.countByGoodsIdAndName(goodsId, vo.getSUnitName()) > 0) {
            throw new BusinessException(ErrorCode.UNIT_NOT_FOUND, "包装单位名称已存在");
        }
        GoodsUnit unit = new GoodsUnit();
        unit.setSGoodsId(goodsId);
        unit.setSUnitName(vo.getSUnitName());
        unit.setIConvertRate(vo.getIConvertRate());
        goodsUnitMapper.insert(unit);
        return unit.getSId();
    }

    /**
     * 功能描述: 删除包装单位，该商品已有出入库流水时拒绝（避免历史折算口径不一致）
     *
     * @param unitId 包装单位主键
     * @author honghui
     * @date 2026/06/30 11:40
     */
    @Override
    public void deleteUnit(String unitId) {
        GoodsUnit unit = goodsUnitMapper.selectById(unitId);
        if (unit == null) {
            throw new BusinessException(ErrorCode.UNIT_NOT_FOUND);
        }
        if (goodsMapper.countFlowByGoodsId(unit.getSGoodsId()) > 0) {
            throw new BusinessException(ErrorCode.UNIT_RATE_LOCKED);
        }
        goodsUnitMapper.deleteById(unitId);
    }

    /**
     * 功能描述: 查询某商品的全部包装单位
     *
     * @param goodsId 商品ID
     * @return 包装单位列表
     * @author honghui
     * @date 2026/06/30 11:40
     */
    @Override
    public List<GoodsUnit> listUnits(String goodsId) {
        return goodsUnitMapper.selectByGoodsId(goodsId);
    }

    /**
     * 功能描述: 解析某商品指定单位名称对应的换算率。单位为基本单位时返回 1，
     *           为已配置包装单位时返回其换算率，否则抛出业务异常
     *
     * @param goodsId  商品ID
     * @param unitName 单位名称
     * @return 换算率
     * @author honghui
     * @date 2026/06/30 14:45
     */
    @Override
    public int resolveConvertRate(String goodsId, String unitName) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }
        // 基本单位换算率为 1
        if (goods.getSBaseUnit().equals(unitName)) {
            return Constant.BASE_UNIT_RATE;
        }
        GoodsUnit unit = goodsUnitMapper.selectByGoodsIdAndName(goodsId, unitName);
        if (unit == null) {
            throw new BusinessException(ErrorCode.UNIT_NOT_FOUND);
        }
        return unit.getIConvertRate();
    }

    /**
     * 功能描述: 判断商品是否已被库存或流水使用
     *
     * @param goodsId 商品ID
     * @return true 表示已被使用
     * @author honghui
     * @date 2026/06/30 11:40
     */
    private boolean isGoodsInUse(String goodsId) {
        return goodsMapper.countStockByGoodsId(goodsId) > 0
                || goodsMapper.countFlowByGoodsId(goodsId) > 0;
    }
}
