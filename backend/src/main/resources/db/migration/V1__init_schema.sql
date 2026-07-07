-- 产品-超市耗材供应商系统-第一期初始化建表脚本
-- 规范：utf8mb4 / InnoDB / TAB_ 前缀 / 字段类型前缀 / 必备审计字段 / 字段非 NULL 有默认值

-- ============================================================
-- 商品档案表
-- ============================================================
CREATE TABLE TAB_GOODS (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_CODE          VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '商品编码',
    S_NAME          VARCHAR(200)  NOT NULL DEFAULT ''  COMMENT '商品名称',
    S_CATEGORY      VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '商品分类',
    S_SPEC          VARCHAR(200)  NOT NULL DEFAULT ''  COMMENT '规格',
    S_BASE_UNIT     VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '基本单位名称',
    I_STATUS        TINYINT       NOT NULL DEFAULT 1   COMMENT '状态：0停用 1启用',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    UNIQUE KEY IDX_TAB_GOODS_S_CODE (S_CODE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-基础数据-商品档案表';

-- ============================================================
-- 商品包装单位换算表
-- ============================================================
CREATE TABLE TAB_GOODS_UNIT (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_GOODS_ID      VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '所属商品ID',
    S_UNIT_NAME     VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '包装单位名称',
    I_CONVERT_RATE  INT           NOT NULL DEFAULT 1   COMMENT '换算率：1包装单位=N基本单位',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_GOODS_UNIT_S_GOODS_ID (S_GOODS_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-基础数据-商品包装单位换算表';

-- ============================================================
-- 库存地点表
-- ============================================================
CREATE TABLE TAB_WAREHOUSE (
    S_ID             VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_CODE           VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '地点编码',
    S_NAME           VARCHAR(200)  NOT NULL DEFAULT ''  COMMENT '地点名称',
    I_LOCATION_TYPE  TINYINT       NOT NULL DEFAULT 1   COMMENT '地点类型：1仓库 2门店',
    I_STATUS         TINYINT       NOT NULL DEFAULT 1   COMMENT '状态：0停用 1启用',
    DT_CREATE_TIME   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER    VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER    VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    UNIQUE KEY IDX_TAB_WAREHOUSE_S_CODE (S_CODE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-基础数据-库存地点表';

-- ============================================================
-- 客户（超市）档案表
-- ============================================================
CREATE TABLE TAB_CUSTOMER (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_NAME          VARCHAR(200)  NOT NULL DEFAULT ''  COMMENT '客户名称',
    S_CONTACT       VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '联系人',
    S_ADDRESS       VARCHAR(500)  NOT NULL DEFAULT ''  COMMENT '收货地址',
    S_PHONE         VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '联系电话',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_CUSTOMER_S_NAME (S_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-基础数据-客户档案表';

-- ============================================================
-- 供应商档案表
-- ============================================================
CREATE TABLE TAB_SUPPLIER (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_NAME          VARCHAR(200)  NOT NULL DEFAULT ''  COMMENT '供应商名称',
    S_CONTACT       VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '联系人',
    S_PHONE         VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '联系电话',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_SUPPLIER_S_NAME (S_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-基础数据-供应商档案表';

-- ============================================================
-- 库存汇总表（商品 × 地点）
-- ============================================================
CREATE TABLE TAB_STOCK (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_GOODS_ID      VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '商品ID',
    S_WAREHOUSE_ID  VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '库存地点ID',
    I_QTY           INT           NOT NULL DEFAULT 0   COMMENT '当前库存数量（基本单位）',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    UNIQUE KEY IDX_TAB_STOCK_GOODS_WH (S_GOODS_ID, S_WAREHOUSE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-库存-库存汇总表';

-- ============================================================
-- 出入库流水表
-- ============================================================
CREATE TABLE TAB_STOCK_FLOW (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_GOODS_ID      VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '商品ID',
    S_WAREHOUSE_ID  VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '库存地点ID',
    I_FLOW_TYPE     TINYINT       NOT NULL DEFAULT 0   COMMENT '流水类型：1采购入库 2批发出库 3门店散卖出库 4手工调整',
    I_CHANGE_QTY    INT           NOT NULL DEFAULT 0   COMMENT '变动数量（基本单位，入库正出库负）',
    S_SOURCE_NO     VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '关联单据ID',
    S_REMARK        VARCHAR(500)  NOT NULL DEFAULT ''  COMMENT '备注',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_STOCK_FLOW_GOODS_WH (S_GOODS_ID, S_WAREHOUSE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-库存-出入库流水表';

-- ============================================================
-- 进货单主表
-- ============================================================
CREATE TABLE TAB_PURCHASE (
    S_ID              VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_PURCHASE_NO     VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '进货单号',
    S_SUPPLIER_ID     VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '供应商ID',
    S_WAREHOUSE_ID    VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '入库地点ID',
    I_TOTAL_AMOUNT    INT           NOT NULL DEFAULT 0   COMMENT '总金额（分）',
    DT_PURCHASE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '进货时间',
    DT_CREATE_TIME    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER     VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER     VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    UNIQUE KEY IDX_TAB_PURCHASE_S_PURCHASE_NO (S_PURCHASE_NO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-进货-进货单主表';

-- ============================================================
-- 进货单明细表
-- ============================================================
CREATE TABLE TAB_PURCHASE_ITEM (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_PURCHASE_ID   VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '进货单主表ID',
    S_GOODS_ID      VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '商品ID',
    I_QTY_BASE      INT           NOT NULL DEFAULT 0   COMMENT '折算后基本单位数量',
    S_INPUT_UNIT    VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '录入时单位名称',
    I_INPUT_QTY     INT           NOT NULL DEFAULT 0   COMMENT '录入时数量',
    I_PRICE         INT           NOT NULL DEFAULT 0   COMMENT '进价（分，按基本单位计）',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_PURCHASE_ITEM_S_PURCHASE_ID (S_PURCHASE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-进货-进货单明细表';

-- ============================================================
-- 销售单主表
-- ============================================================
CREATE TABLE TAB_SALE_ORDER (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_SALE_NO       VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '销售单号',
    I_SALE_TYPE     TINYINT       NOT NULL DEFAULT 1   COMMENT '销售类型：1批发出货 2门店散卖',
    S_CUSTOMER_ID   VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '客户ID（散卖为空）',
    S_WAREHOUSE_ID  VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '出库地点ID',
    I_STATUS        TINYINT       NOT NULL DEFAULT 1   COMMENT '单据状态：1待发货 2已完成',
    I_TOTAL_AMOUNT  INT           NOT NULL DEFAULT 0   COMMENT '总金额（分）',
    DT_SHIP_TIME    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发货时间',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    UNIQUE KEY IDX_TAB_SALE_ORDER_S_SALE_NO (S_SALE_NO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-出货-销售单主表';

-- ============================================================
-- 销售单明细表
-- ============================================================
CREATE TABLE TAB_SALE_ORDER_ITEM (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_SALE_ID       VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '销售单主表ID',
    S_GOODS_ID      VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '商品ID',
    I_QTY_BASE      INT           NOT NULL DEFAULT 0   COMMENT '折算后基本单位数量',
    S_INPUT_UNIT    VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '录入时单位名称',
    I_INPUT_QTY     INT           NOT NULL DEFAULT 0   COMMENT '录入时数量',
    I_PRICE         INT           NOT NULL DEFAULT 0   COMMENT '售价（分，按基本单位计）',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_SALE_ORDER_ITEM_S_SALE_ID (S_SALE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-出货-销售单明细表';
