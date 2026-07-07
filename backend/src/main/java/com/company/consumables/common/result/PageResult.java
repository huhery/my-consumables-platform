package com.company.consumables.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 类描述: 分页查询统一返回结构
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 当前页数据列表 */
    private List<T> list;

    /**
     * 功能描述: 构造分页结果
     *
     * @param total 总记录数
     * @param list  数据列表
     * @param <T>   数据类型
     * @return 分页结果
     * @author honghui
     * @date 2026/06/30 11:10
     */
    public static <T> PageResult<T> of(long total, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setList(list);
        return result;
    }
}
