package com.company.consumables.basedata.goods.controller;

import com.company.consumables.basedata.goods.entity.Goods;
import com.company.consumables.basedata.goods.entity.GoodsUnit;
import com.company.consumables.basedata.goods.service.GoodsService;
import com.company.consumables.basedata.goods.vo.GoodsQueryVo;
import com.company.consumables.basedata.goods.vo.GoodsSaveVo;
import com.company.consumables.basedata.goods.vo.GoodsUnitSaveVo;
import com.company.consumables.common.result.PageResult;
import com.company.consumables.common.result.RestApiResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 类描述: 商品档案与包装单位 REST 接口
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
@Validated
public class GoodsController {

    private final GoodsService goodsService;

    /**
     * 功能描述: 新增商品
     *
     * @param vo 商品入参
     * @return 商品主键
     * @author honghui
     * @date 2026/06/30 11:45
     */
    @PostMapping
    public RestApiResultVo<String> create(@RequestBody @Valid GoodsSaveVo vo) {
        return RestApiResultVo.ok(goodsService.createGoods(vo));
    }

    /**
     * 功能描述: 修改商品
     *
     * @param id 商品主键
     * @param vo 商品入参
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 11:45
     */
    @PutMapping("/{id}")
    public RestApiResultVo<Void> update(@PathVariable("id") String id, @RequestBody @Valid GoodsSaveVo vo) {
        vo.setSId(id);
        goodsService.updateGoods(vo);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 删除商品
     *
     * @param id 商品主键
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 11:45
     */
    @DeleteMapping("/{id}")
    public RestApiResultVo<Void> delete(@PathVariable("id") String id) {
        goodsService.deleteGoods(id);
        return RestApiResultVo.ok();
    }

    /**
     * 功能描述: 分页查询商品
     *
     * @param query 查询条件
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 11:45
     */
    @GetMapping("/page")
    public RestApiResultVo<PageResult<Goods>> page(GoodsQueryVo query) {
        return RestApiResultVo.ok(goodsService.pageGoods(query));
    }

    /**
     * 功能描述: 查询某商品的全部包装单位
     *
     * @param id 商品主键
     * @return 包装单位列表
     * @author honghui
     * @date 2026/06/30 11:45
     */
    @GetMapping("/{id}/units")
    public RestApiResultVo<List<GoodsUnit>> listUnits(@PathVariable("id") String id) {
        return RestApiResultVo.ok(goodsService.listUnits(id));
    }

    /**
     * 功能描述: 新增商品包装单位
     *
     * @param id 商品主键
     * @param vo 包装单位入参
     * @return 包装单位主键
     * @author honghui
     * @date 2026/06/30 11:45
     */
    @PostMapping("/{id}/units")
    public RestApiResultVo<String> addUnit(@PathVariable("id") String id, @RequestBody @Valid GoodsUnitSaveVo vo) {
        return RestApiResultVo.ok(goodsService.addUnit(id, vo));
    }

    /**
     * 功能描述: 删除商品包装单位
     *
     * @param unitId 包装单位主键
     * @return 空结果
     * @author honghui
     * @date 2026/06/30 11:45
     */
    @DeleteMapping("/units/{unitId}")
    public RestApiResultVo<Void> deleteUnit(@PathVariable("unitId") String unitId) {
        goodsService.deleteUnit(unitId);
        return RestApiResultVo.ok();
    }
}
