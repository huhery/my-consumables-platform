-- 产品-超市耗材供应商系统-第二期财务模块建表脚本
-- 规范：utf8mb4 / InnoDB / TAB_ 前缀 / 字段类型前缀 / 必备审计字段 / 金额用分(int) / 字段非 NULL 有默认值

-- ============================================================
-- 应收账款表
-- ============================================================
CREATE TABLE TAB_RECEIVABLE (
    S_ID               VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_SALE_ID          VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '关联销售单ID',
    S_CUSTOMER_ID      VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '客户ID',
    I_TOTAL_AMOUNT     INT           NOT NULL DEFAULT 0   COMMENT '应收金额（分）',
    I_RECEIVED_AMOUNT  INT           NOT NULL DEFAULT 0   COMMENT '已收金额（分）',
    I_STATUS           TINYINT       NOT NULL DEFAULT 1   COMMENT '状态：1未收清 2已收清',
    DT_CREATE_TIME     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER      VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER      VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_RECEIVABLE_CUSTOMER (S_CUSTOMER_ID),
    KEY IDX_TAB_RECEIVABLE_SALE (S_SALE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-财务-应收账款表';

-- ============================================================
-- 应付账款表
-- ============================================================
CREATE TABLE TAB_PAYABLE (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_PURCHASE_ID   VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '关联进货单ID',
    S_SUPPLIER_ID   VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '供应商ID',
    I_TOTAL_AMOUNT  INT           NOT NULL DEFAULT 0   COMMENT '应付金额（分）',
    I_PAID_AMOUNT   INT           NOT NULL DEFAULT 0   COMMENT '已付金额（分）',
    I_STATUS        TINYINT       NOT NULL DEFAULT 1   COMMENT '状态：1未付清 2已付清',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_PAYABLE_SUPPLIER (S_SUPPLIER_ID),
    KEY IDX_TAB_PAYABLE_PURCHASE (S_PURCHASE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-财务-应付账款表';

-- ============================================================
-- 资金流水表（统一台账）
-- ============================================================
CREATE TABLE TAB_FUND_FLOW (
    S_ID            VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    I_FLOW_TYPE     TINYINT       NOT NULL DEFAULT 0   COMMENT '流水类型：1销售收款 2采购付款 3费用支出 4其他收入',
    I_DIRECTION     TINYINT       NOT NULL DEFAULT 0   COMMENT '方向：1收入 2支出',
    I_AMOUNT        INT           NOT NULL DEFAULT 0   COMMENT '金额（分，正数）',
    S_PARTY_ID      VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '关联对象ID（客户/供应商）',
    S_SOURCE_NO     VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '关联单据ID（销售单/进货单，可空）',
    S_CATEGORY_ID   VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '费用/收入分类ID',
    D_OCCUR_DATE    DATE          NOT NULL DEFAULT '1970-01-01' COMMENT '发生日期',
    S_REMARK        VARCHAR(500)  NOT NULL DEFAULT ''  COMMENT '备注',
    DT_CREATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER   VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_FUND_FLOW_TYPE (I_FLOW_TYPE),
    KEY IDX_TAB_FUND_FLOW_DATE (D_OCCUR_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-财务-资金流水表';

-- ============================================================
-- 费用/收入分类表
-- ============================================================
CREATE TABLE TAB_EXPENSE_CATEGORY (
    S_ID             VARCHAR(50)   NOT NULL DEFAULT ''  COMMENT '主键',
    S_NAME           VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '分类名称',
    I_CATEGORY_TYPE  TINYINT       NOT NULL DEFAULT 1   COMMENT '分类类型：1支出 2收入',
    DT_CREATE_TIME   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    DT_UPDATE_TIME   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    S_CREATE_USER    VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '创建人',
    S_UPDATE_USER    VARCHAR(100)  NOT NULL DEFAULT ''  COMMENT '更新人',
    PRIMARY KEY (S_ID),
    KEY IDX_TAB_EXPENSE_CATEGORY_TYPE (I_CATEGORY_TYPE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品-财务-费用收入分类表';

-- ============================================================
-- 第一期表变更：销售单增加期望送达日期
-- ============================================================
ALTER TABLE TAB_SALE_ORDER
    ADD COLUMN D_EXPECT_DELIVERY DATE NOT NULL DEFAULT '1970-01-01' COMMENT '期望送达日期（1970-01-01视为未填）';

-- ============================================================
-- 预置常用支出分类
-- ============================================================
INSERT INTO TAB_EXPENSE_CATEGORY (S_ID, S_NAME, I_CATEGORY_TYPE, S_CREATE_USER, S_UPDATE_USER) VALUES
('cat_rent',  '房租', 1, 'system', 'system'),
('cat_fuel',  '油费', 1, 'system', 'system'),
('cat_wage',  '工资', 1, 'system', 'system'),
('cat_water', '水电', 1, 'system', 'system');
