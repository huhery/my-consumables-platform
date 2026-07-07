package com.company.consumables.common.query;

import com.company.consumables.common.constant.Constant;
import lombok.Data;

import java.io.Serializable;

/**
 * 类描述: 分页查询入参基类，提供页码、每页条数及偏移量计算
 *
 * @author honghui
 * @version 1.0
 * @date 2026/06/30
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码，从 1 开始 */
    private Integer pageNum = Constant.DEFAULT_PAGE_NUM;

    /** 每页条数 */
    private Integer pageSize = Constant.DEFAULT_PAGE_SIZE;

    /**
     * 功能描述: 计算 SQL 查询的偏移量
     *
     * @return 偏移量
     * @author honghui
     * @date 2026/06/30 11:12
     */
    public int getOffset() {
        int num = (pageNum == null || pageNum < 1) ? Constant.DEFAULT_PAGE_NUM : pageNum;
        int size = (pageSize == null || pageSize < 1) ? Constant.DEFAULT_PAGE_SIZE : pageSize;
        return (num - 1) * size;
    }

    /**
     * 功能描述: 获取安全的每页条数
     *
     * @return 每页条数
     * @author honghui
     * @date 2026/06/30 11:12
     */
    public int getLimit() {
        return (pageSize == null || pageSize < 1) ? Constant.DEFAULT_PAGE_SIZE : pageSize;
    }
}
