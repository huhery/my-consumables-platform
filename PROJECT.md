# 超市耗材供应商 SaaS 平台 · 项目总纲

> 本文档是项目的单一权威说明。任何人（或 AI）拿到本文档，无需额外背景即可理解项目全貌、在此基础上迭代升级、部署运维。
>
> 最后更新：2026-07（第三期多租户改造完成 + Jackson 序列化修复 + 商家有效期/续期 + JOIN 别名修复）

---

## 目录

1. 项目概述
2. 演进历程（三期）
3. 技术栈与架构
4. 代码目录结构
5. 数据模型（全表清单）
6. 核心设计决策与不变量
7. 功能清单（按模块）
8. 关键业务流程
9. 部署与运维
10. 本地开发与测试
11. 迭代升级指南
12. 已知边界与后续规划
13. 相关文档索引
14. 后续路线图（第四期及以后 · 规划中）

---

## 1. 项目概述

**是什么**：面向**超市耗材供应商**的进销存 + 财务管理系统，已改造为**多商家 SaaS 平台**。

**业务背景**：耗材供应商向超市批发供货（购物袋、保鲜膜、收银纸、清洁用品等），核心动作是 **进货入库 → 多地点库存 → 出货（批发/门店散卖） → 收付款对账**。

**当前形态**：
- 多个商家（租户）共用一套平台，数据互相隔离
- 平台管理员开通/管理商家；每个商家登录后管理自己的进销存与财务
- 纯 Web 管理后台（前后端分离），架构已为将来 App/小程序预留

**核心原则**：可持续迭代，不伤筋动骨。地基级能力（多租户隔离、账号角色、鉴权入口、分库路径、UUID 主键、JWT 多端）已一次性预埋，后续多为局部新增。

---

## 2. 演进历程（三期）

| 期次 | 主题 | 规格目录 | 交付 |
|------|------|----------|------|
| 第一期 | 业务主线：基础数据 + 库存 + 进货 + 出货 | `.kiro/specs/supermarket-supplies-system/` | 商品/单位换算、多仓库+门店、进货入库、批发发货、门店散卖、库存流水 |
| 第二期 | 财务：应收应付 + 资金流水 + 费用 + 报表 | `.kiro/specs/payment-management/` | 应收/收款、应付/付款、费用记账、资金流水、送货提醒、经营报表 |
| 第三期 | 多租户 SaaS 化 | `.kiro/specs/multi-tenancy/` | 租户隔离、JWT 认证、账号角色、平台管理后台 |

每期都有完整的 `requirements.md`（需求，EARS 格式）、`design.md`（设计）、`tasks.md`（任务清单）三件套，是理解各期细节的权威来源。

---

## 3. 技术栈与架构

### 技术栈

| 层 | 选型 | 说明 |
|----|------|------|
| 后端框架 | Spring Boot 2.7.18 | 适配 JDK 8 |
| 语言 | Java 8 | 受本地环境约束选定 |
| 持久层 | MyBatis 3（手写 XML） | 不使用 MyBatis-Plus |
| 数据库 | MySQL 8（utf8mb4） | InnoDB |
| 迁移 | Flyway 8.5 + flyway-mysql | V1/V2/V3 版本化脚本 |
| 认证 | JWT（jjwt 0.11.5）+ BCrypt | 无状态，多端通用 |
| SQL 改写 | JSqlParser 4.6 | 租户隔离自动加条件 |
| 前端 | Vue 3 + Vite | 组合式 API |
| 前端 UI | Element Plus | |
| 前端状态/路由/请求 | Pinia / Vue Router 4 / Axios | |
| 部署 | Docker Compose | MySQL + 后端 + 前端(Nginx) |

### 架构总览

```
                    公网 :8090
                       │
              ┌────────▼─────────┐
              │ frontend(Nginx)  │  Vue 静态资源 + /api 反向代理
              └────────┬─────────┘
                       │ REST + JSON（Header: Authorization: Bearer JWT）
              ┌────────▼─────────────────────────────────┐
              │ backend(Spring Boot :8080)                │
              │  JwtAuthFilter → 鉴权 → Controller         │
              │   → Service → Mapper                       │
              │   ↘ 审计拦截器（填充租户/审计字段）          │
              │   ↘ 租户SQL拦截器（自动加 S_TENANT_ID 条件） │
              └────────┬─────────────────────────────────┘
                       │
              ┌────────▼─────────┐
              │   mysql :3306    │  仅容器内访问，数据卷持久化
              └──────────────────┘
```

### 请求处理链（多租户核心）

```
请求(带JWT) → JwtAuthFilter 解析令牌 → 建立 TenantContext/UserContext(ThreadLocal)
  → Controller → Service → Mapper
      · 审计拦截器：INSERT 自动填充 S_TENANT_ID + 审计字段
      · 租户SQL拦截器：SELECT/UPDATE/DELETE 自动追加 S_TENANT_ID 条件（平台表白名单豁免）
  → 响应后清理 ThreadLocal
```

---

## 4. 代码目录结构

```
my-consumables-platform/
├── backend/                         Spring Boot 后端
│   ├── src/main/java/com/company/consumables/
│   │   ├── ConsumablesApplication.java   启动类
│   │   ├── common/                  公共能力
│   │   │   ├── result/              RestApiResultVo、PageResult
│   │   │   ├── query/               PageQuery
│   │   │   ├── exception/           BusinessException、全局异常处理器
│   │   │   ├── error/               ErrorCode、错误码加载器
│   │   │   ├── constant/            Constant
│   │   │   ├── enums/               各类枚举（流水/单据/角色/状态等）
│   │   │   ├── entity/              BaseEntity（含审计+租户字段）
│   │   │   ├── util/                BizNoGenerator、ValueUtil
│   │   │   ├── context/             UserContext
│   │   │   ├── audit/               AuditFieldInterceptor（审计+租户填充）
│   │   │   ├── tenant/              TenantContext、TenantSqlInterceptor（隔离核心）
│   │   │   └── security/            JwtUtil、JwtAuthFilter、SecurityConfig、平台管理员初始化
│   │   ├── basedata/                商品/单位、库存地点、客户、供应商
│   │   ├── stock/                   库存账、出入库流水
│   │   ├── purchase/                进货
│   │   ├── sale/                    出货（批发+门店散卖）
│   │   ├── finance/                 财务（应收/应付/资金流水/费用/报表）
│   │   └── platform/                平台侧（租户管理、账号/登录认证）
│   ├── src/main/resources/
│   │   ├── application.yml          主配置
│   │   ├── error-code.properties    错误码（中文 Unicode 转义）
│   │   ├── db/migration/            Flyway 脚本 V1/V2/V3
│   │   └── mapper/                  MyBatis XML（按模块分目录）
│   ├── src/test/                    单元/集成测试 + H2 test-schema.sql
│   ├── Dockerfile / settings.xml / pom.xml
│   └── .dockerignore
├── frontend/                        Vue 3 前端
│   ├── src/
│   │   ├── main.js / App.vue
│   │   ├── router/                  路由 + 守卫
│   │   ├── store/                   Pinia（auth 会话）
│   │   ├── api/                     各模块接口封装 + request(拦截器)
│   │   ├── layout/                  MainLayout（菜单+顶栏）
│   │   ├── utils/                   money(分↔元)、dict(字典)
│   │   └── views/                   各业务页面 + 登录页 + 平台管理
│   ├── Dockerfile / nginx.conf / vite.config.js / package.json
│   └── .dockerignore
├── .kiro/specs/                     三期规格文档（需求/设计/任务）
├── docker-compose.yml               三容器编排
├── deploy.sh                        首次一键部署脚本
├── update.sh                        日常更新脚本
├── .env.example                     环境变量模板
├── README.md                        使用说明
├── DEPLOY.md                        部署指南
├── VERIFY.md                        端到端验证清单
└── PROJECT.md                       本文档（项目总纲）
```

---

## 5. 数据模型（全表清单）

**通用规范**（所有表遵循）：
- 命名：`TAB_` 前缀、全大写、字段类型前缀（`S_`字符串 / `I_`数字 / `D_`日期 / `DT_`时间）
- 必备审计字段：`S_ID`(UUID主键)、`DT_CREATE_TIME`、`DT_UPDATE_TIME`、`S_CREATE_USER`、`S_UPDATE_USER`
- 所有业务表含 `S_TENANT_ID`（租户隔离）；平台表不含
- 金额一律用 `int` 存"分"；字段非 NULL 有默认值；物理删除；utf8mb4

| 表名 | 归属 | 说明 | 租户隔离 |
|------|------|------|:---:|
| TAB_GOODS | 基础数据 | 商品档案（含基本单位） | ✅ |
| TAB_GOODS_UNIT | 基础数据 | 商品包装单位换算（箱→个换算率） | ✅ |
| TAB_WAREHOUSE | 基础数据 | 库存地点（仓库/门店） | ✅ |
| TAB_CUSTOMER | 基础数据 | 客户（超市） | ✅ |
| TAB_SUPPLIER | 基础数据 | 供应商 | ✅ |
| TAB_STOCK | 库存 | 库存汇总（商品×地点，基本单位） | ✅ |
| TAB_STOCK_FLOW | 库存 | 出入库流水（库存真相来源） | ✅ |
| TAB_PURCHASE | 进货 | 进货单主表 | ✅ |
| TAB_PURCHASE_ITEM | 进货 | 进货单明细 | ✅ |
| TAB_SALE_ORDER | 出货 | 销售单主表（批发/散卖，含期望送达日期） | ✅ |
| TAB_SALE_ORDER_ITEM | 出货 | 销售单明细 | ✅ |
| TAB_RECEIVABLE | 财务 | 应收账款 | ✅ |
| TAB_PAYABLE | 财务 | 应付账款 | ✅ |
| TAB_FUND_FLOW | 财务 | 资金流水（收款/付款/费用/收入统一台账） | ✅ |
| TAB_EXPENSE_CATEGORY | 财务 | 费用/收入分类 | ✅ |
| TAB_TENANT | 平台 | 租户（商家，含到期日期、AI开关 I_AI_ENABLED） | ❌平台表 |
| TAB_ACCOUNT | 平台 | 账号（一租户N账号+角色） | ❌平台表 |
| TAB_AI_CHAT_LOG | AI | AI 问答日志（可选记录） | ✅ |

**唯一性约束**（均为租户内唯一，组合唯一索引 `S_TENANT_ID + 字段`）：
- 商品编码、库存地点编码、进货单号、销售单号
- 账号登录名为**全局唯一**（平台表，跨租户）

**建表脚本位置**：`backend/src/main/resources/db/migration/`
- `V1__init_schema.sql`：第一期 11 张业务表
- `V2__finance_schema.sql`：第二期 4 张财务表 + 销售单加期望送达日期
- `V3__multitenancy_schema.sql`：租户/账号表 + 15 张业务表加租户字段 + 唯一性改造
- `V4__tenant_expire.sql`：租户表增加到期日期字段 D_EXPIRE_DATE
- `V5__ai_assistant.sql`：租户表增加 AI 开关 I_AI_ENABLED + 创建 AI 问答日志表 TAB_AI_CHAT_LOG

---

## 6. 核心设计决策与不变量

### 关键设计决策

1. **库存靠流水驱动**：库存数 = 出入库流水累加。不直接改库存数字，`TAB_STOCK` 是冗余汇总，`TAB_STOCK_FLOW` 是真相来源。→ 可追溯、可对账，加新库存业务只需加流水类型。
2. **多单位统一按基本单位记账**：包装单位（箱）只在录单时出现，Service 层折算为基本单位（个）落库。→ 库存口径唯一。
3. **批发/散卖共用出库内核**：`StockService.deductStock` 统一校验+扣减+写流水。
4. **金额用分（int）**：避免浮点误差，前端展示/录入用元，`utils/money.js` 统一换算。
5. **统一资金流水底座**：收款/付款/费用/收入都进 `TAB_FUND_FLOW`；应收应付独立台账，收付款时双写（冲减 + 流水）。
6. **多租户框架层隔离**：JSqlParser 拦截器自动为业务表 SQL 加租户条件，平台表白名单豁免。**绝不靠手写租户条件**。
7. **一级缓存作用域=STATEMENT**：防止同会话跨租户查询命中缓存串数据（关键安全配置）。
8. **审计+租户自动填充**：MyBatis 拦截器在 INSERT 时自动填充 UUID 主键、审计字段、租户ID，业务代码零感知。
9. **JWT 无状态认证**：多端通用（Web 现用，App/小程序可复用），不依赖 Cookie/Session。
10. **Jackson 按字段名直接序列化**：全局配置 `JacksonConfig`（FIELD 可见性，关闭 GETTER 推断），避免 Lombok getter 命名（如 `getSName`→Jackson 误推为 `SName`）与属性推断不兼容。所有实体/VO 的 JSON key 与 Java 字段名一致。
11. **租户拦截器带主表别名**：追加 `WHERE 主表别名.S_TENANT_ID = ?`，避免 JOIN 场景列名歧义。
12. **商家有效期管理**：`TAB_TENANT.D_EXPIRE_DATE` 到期日期字段；开通商家时设有效期（默认 1 年）；登录校验到期（过期拒登提示续费）；平台管理员可续期。
13. **AI 智能问数（只读、不碰 SQL）**：商家用自然语言查经营数据。大模型只做"意图识别 + 参数抽取"，输出被约束为"选一个预设意图 + 填参数"的 JSON；取数走既有 Service（自动经租户隔离拦截器），**绝不把模型输出当 SQL 执行**。意图用 Spring `List<IntentHandler>` 注入实现"注册即生效"；大模型全配置化（OpenAI 兼容协议，换厂商零改代码）；AI 开关按商家控制（`I_AI_ENABLED`）作为增值点；映射不到即拒答、大模型不可用即降级，绝不影响系统其他功能。

### 系统不变量（测试守护）

- **库存一致性**：任意商品×地点的库存 = 其全部流水累加之和
- **单位折算守恒**：落库基本单位数量 = 录入数量 × 换算率
- **防超卖**：库存不足时出库被拒（条件 UPDATE + 预校验）
- **应收/应付守恒**：客户欠款 = 应收未收之和；供应商应付 = 应付未付之和
- **超额拒绝**：收付款金额 > 未收/未付则拒绝
- **门店约束**：仅门店类型地点可散卖
- **租户隔离**：任一租户无法读取/修改其他租户数据
- **租户内唯一**：编码/单号在租户内唯一，跨租户可重复

### 测试现状

后端共 **46 个测试全部通过**：
- 第一期：商品(8) + 库存(5) + 进货(1) + 出货(5)
- 第二期：财务(5)
- 第三期：租户隔离(2) + 认证与租户管理(4)
- 第七期 AI：意图路由(3) + 编排服务(5) + 大模型客户端(3) + AI开关与隔离(3)
- 易用性优化：工作台聚合(2)

测试用 H2 内存库（`test-schema.sql`），`@ActiveProfiles("test")`。既有测试通过 `@BeforeEach` 设置默认租户上下文适配隔离。

---

## 7. 功能清单（按模块）

### 平台侧（平台管理员 / 超级管理员）

> **超级管理员**即"平台管理员"（角色 `RoleType.PLATFORM_ADMIN`），是平台运营方的最高权限账号。
> 默认账号在后端**首次启动时自动创建**（由 `PlatformAdminInitializer` 执行），登录名与初始密码通过 `application.yml` 配置：
>
> ```yaml
> consumables:
>   platform-admin:
>     login-name: admin       # 默认登录名
>     password: admin123      # 默认初始密码（生产务必修改！）
> ```
>
> 超级管理员登录后进入"商家管理"界面，可管理所有商家，看不到业务数据（不归属任何租户）。

- 商家开通（建租户 + 初始商家账号 + 有效期 + AI 开关）、启用/停用、续期、AI 开关、商家分页查询

### 基础数据（商家）
- 商品档案 CRUD + 编码唯一 + 使用中禁改基本单位/禁删
- 商品包装单位换算配置（换算率>0、已有流水禁改）
- 库存地点（仓库/门店）CRUD、客户 CRUD、供应商 CRUD

### 库存（商家）
- 库存查询、出入库流水查询（类型标签着色）、手工库存调整

### 进货（商家）
- 进货单录入（多单位折算、保存即入库、生成应付）、进货单列表/详情

### 出货（商家）
- 批发出货：录单（待发货）→ 发货（扣库存、防超卖、生成应收）
- 门店散卖：仅门店、按基本单位、保存即出库
- 销售单列表/详情

### 财务（商家）
- 销售收款（按单/按总欠、部分收款、超额拒绝）+ 欠款汇总
- 采购付款（镜像）+ 付款汇总
- 费用/收入记账 + 分类管理
- 资金流水台账
- 送货提醒（待发货批发单，按期望送达排序）
- 经营报表：资金汇总、费用分类汇总

### AI 智能问数（商家，需平台开通 AI）
- 自然语言问数：销售额、客户欠款排行、供应商应付、库存概览、收支概览、待送货提醒
- 大模型只识别意图不碰 SQL；取数复用既有 Service 自动隔离；映射不到拒答、失败降级
- 前端"智能问数"入口仅对已开通 AI 的商家显示

### 易用性优化（面向小微商户，电脑端）
- **工作台首页**：登录即见今日收支、别人欠我的钱（+最多客户）、我欠供应商的钱（+最多供应商）、要补货商品、近几天要送的货、本月销售额；由 `GET /api/dashboard/summary` 一次聚合返回（只读、复用既有 Service 自动隔离）
- **任务导向导航 + 大白话话术**：菜单按动作重组（工作台/卖货/进货/收钱付钱/货/送货提醒/看账本/基础资料），术语口语化（应收→别人欠我的钱、资金流水→收支明细等）
- **批发一步发货**：卖给超市开单默认"货已送出"，开单即发货；取消勾选则存待发货
- **适老化**：大字体、大按钮、金额配色（收入绿/支出欠款红）

### 认证（全局）
- 登录（JWT）、退出、登录态守卫、按角色分流界面

---

## 8. API 清单（统一前缀 /api，统一返回 RestApiResultVo）

**认证**：`POST /auth/login`、`POST /auth/logout`
**平台管理**（仅平台管理员）：`POST /platform/tenant`、`GET /platform/tenant/page`、`POST /platform/tenant/{id}/enable|disable`、`POST /platform/tenant/{id}/renew?years=N`（续期）、`POST /platform/tenant/{id}/ai?enabled=true|false`（AI 开关）
**AI 智能问数**（商家，需开通 AI）：`POST /ai/ask`（自然语言问数）、`GET /ai/status`（是否开通）
**工作台**（商家）：`GET /dashboard/summary`（首页聚合数据）
**商品**：`POST|PUT|DELETE /goods`、`GET /goods/page`、`GET|POST /goods/{id}/units`、`DELETE /goods/units/{unitId}`
**库存地点/客户/供应商**：`/warehouse`、`/customer`、`/supplier` 各 CRUD + `/page`
**库存**：`GET /stock/page`、`GET /stock/flow/page`、`POST /stock/adjust`
**进货**：`POST /purchase`、`GET /purchase/page`、`GET /purchase/{id}`
**出货**：`POST /sale/wholesale`、`POST /sale/{id}/ship`、`POST /sale/store`、`GET /sale/page`、`GET /sale/{id}`
**应收/收款**：`GET /receivable/page|summary`、`GET /receivable/customer/{id}`、`POST /receipt`
**应付/付款**：`GET /payable/page|summary`、`GET /payable/supplier/{id}`、`POST /payment`
**费用/分类/资金**：`POST /expense`、`POST /income`、`/expense-category` CRUD、`GET /fund-flow/page`
**送货/报表**：`GET /delivery-reminder`、`GET /report/fund-summary`、`GET /report/expense-summary`

---

## 9. 关键业务流程

### 进货入库
```
录进货单(供应商/入库地点/明细) → 校验 → 生成进货单号 → 单位折算为基本单位
  → 写主表+明细 → StockService.increaseStock(写入库流水+加库存汇总)
  → 计算并回填总金额 → 生成应付账款
```

### 批发出货（两步）
```
录单：选客户/出库地点/明细(可填期望送达) → 保存为"待发货"(不扣库存)
发货：校验待发货 → 全明细库存预校验 → 逐条 deductStock(扣库存+出库流水)
  → 状态转"已完成" → 生成应收账款
```

### 门店散卖
```
选门店(校验地点类型=门店) → 按基本单位录入 → 保存即出库(deductStock) → 状态"已完成"
```

### 销售收款
```
登记收款(客户/金额/日期，可选关联销售单)
  → 关联单据：校验≤未收(否则超额拒绝) → 冲减该单已收/未收
  → 写"销售收款"资金流水
```

### 开通商家（平台管理员）
```
POST /platform/tenant(商家名/初始登录名/密码)
  → 校验登录名全局唯一 → 建 TAB_TENANT(启用) → 建 TAB_ACCOUNT(商家管理员,BCrypt密码) [同一事务]
```

### 登录
```
POST /auth/login → 按登录名查账号 → BCrypt 校验密码 → 校验账号/租户启用
  → 签发 JWT(userId/tenantId/role) → 前端存 localStorage，后续请求头携带
```

---

## 10. 部署与运维

### 首次部署（腾讯云 CVM / 任意 Linux）
```bash
# 上传项目后
cd my-consumables-platform
chmod +x deploy.sh
sudo ./deploy.sh        # 自动装Docker、配镜像加速、初始化.env、构建启动
```
访问 `http://<公网IP>:8090`（安全组放通该端口）。默认平台管理员 `admin` / `admin123`。

### 日常更新（改代码后）
```bash
sudo ./update.sh            # 前后端都更新
sudo ./update.sh backend    # 只更新后端
sudo ./update.sh frontend   # 只更新前端
sudo ./update.sh --no-pull  # 手动传代码时跳过 git pull
```
数据库容器与数据不受影响；Flyway 自动执行新增迁移脚本。

### 配置（.env 与 application.yml）
```
# .env（部署级）
DB_PASSWORD / DB_NAME / WEB_PORT(默认8090)

# application.yml（应用级，生产务必改）
consumables.jwt.secret            JWT密钥（改强密钥）
consumables.jwt.expire-millis     令牌有效期（默认7天）
consumables.platform-admin.password  平台管理员初始密码（改掉 admin123）

# AI 智能问数（可选，环境变量注入；不配置则 AI 走降级不影响其他功能）
AI_ENABLED           平台 AI 总开关（默认 false）
AI_LLM_BASE_URL      OpenAI 兼容地址（如通义 https://dashscope.aliyuncs.com/compatible-mode/v1）
AI_LLM_API_KEY       大模型密钥（禁止硬编码）
AI_LLM_MODEL         模型名（默认 qwen-plus）
AI_LOG_ENABLED       是否记录问答日志（默认 false）
```

### 环境要求
- CVM 2核4G 起（构建期前端 Vite + 后端 Maven 峰值占内存；2G 需加 swap）
- 系统盘 40G+；MySQL 端口仅容器内访问不暴露公网

### 数据备份
```bash
docker exec consumables-mysql mysqldump -uroot -p<DB_PASSWORD> CONSUMABLES > backup_$(date +%Y%m%d).sql
```

### 部署架构说明
- 三容器：`consumables-mysql` / `consumables-backend` / `consumables-frontend`
- 前端 Nginx 托管静态资源 + `/api` 反代后端；数据卷 `mysql-data` 持久化
- 详见 `DEPLOY.md`

---

## 11. 本地开发与测试

### 后端
```bash
cd backend
mvn spring-boot:run       # 启动（需本地 MySQL，配置见 application.yml）
mvn test                  # 运行全部测试（用 H2 内存库，无需 MySQL）
```
> 环境：JDK 8 + Maven 3.6+。构建慢可用 `settings.xml` 的阿里云镜像。

### 前端
```bash
cd frontend
npm install --registry=https://registry.npmmirror.com   # 国内镜像
npm run dev               # 开发服务器(5173)，/api 代理到 localhost:8080
npm run build             # 生产构建
```

### 新增业务表的开发约定（重要）
1. 实体继承 `BaseEntity`（自动获得主键+审计+租户字段）
2. Mapper XML 的 resultMap 加 `S_TENANT_ID` 映射、insert 加 `S_TENANT_ID` 列与 `#{sTenantId}`
3. 查询**无需手写**租户条件——租户拦截器自动加
4. 若为平台级表（不隔离），在 `TenantSqlInterceptor.IGNORE_TABLES` 加白名单
5. 新增 Flyway 脚本 `V{n}__xxx.sql`，同步更新 `test-schema.sql`
6. 错误码写入 `error-code.properties`（中文 Unicode 转义）+ `ErrorCode.java`
7. 金额字段用 int 存分；类/方法加中文 Javadoc（@author honghui）

---

## 12. 迭代升级指南

### 低成本迭代（局部新增，不动核心）
- **加业务模块**（如退货）：新增 package + 表 + Flyway 脚本，复用库存内核/资金流水/租户隔离
- **加报表**：新增聚合查询即可
- **加库存/资金业务类型**：加枚举值 + 流水类型
- **加字段**：Flyway ALTER + Mapper 补字段

### 中成本迭代（往预埋结构填）
- **多员工账号 + 细粒度权限**：账号表已支持一租户N账号+角色，鉴权走统一入口。加账号管理界面 + 在鉴权拦截器加规则即可，不重构
- **商家自助注册**：复用开通商家逻辑，加注册接口
- **App/小程序**：复用全部 `/api` 接口（JWT + 请求头已就绪），仅新增端界面

### 高成本迭代（架构级，谨慎评估）
- **分库/分Schema**（商家上千）：租户ID方案已铺路，按租户迁移数据
- **计费订阅**：新增计费模块 + 租户套餐字段

### 迭代流程建议
沿用三期的规格驱动方式：`需求(requirements.md) → 设计(design.md) → 任务(tasks.md) → 实现`，规格放 `.kiro/specs/<feature>/`。每个改动后跑 `mvn test` 回归。

---

## 13. 已知边界与后续规划

### 当前不做（明确边界）
- 退货（销售/采购退货）
- 商家自助注册、多员工账号管理界面、细粒度权限
- 分库、计费订阅
- 手机 App / 小程序
- 财务凭证/报税（属代账领域）
- 客户分级报价/账期、配送路线规划

### 建议的后续期次
详见 **第 14 节 后续路线图**（第四期退货、第五期多员工账号+权限、第六期移动端及更远期规划）。

### 待验证事项
- 本地已通过编译 + 44 个测试；**真机端到端联调**（登录→开通商家→隔离验证→AI 问数）请按 `VERIFY.md` 执行
- 生产上线前务必修改：`jwt.secret`、平台管理员密码、`DB_PASSWORD`

---

## 13. 相关文档索引

| 文档 | 内容 |
|------|------|
| `PROJECT.md` | 本文档，项目总纲 |
| `README.md` | 使用说明与功能概览 |
| `DEPLOY.md` | 详细部署指南 |
| `VERIFY.md` | 端到端验证清单 |
| `.kiro/specs/*/requirements.md` | 各期需求（EARS 格式） |
| `.kiro/specs/*/design.md` | 各期技术设计 |
| `.kiro/specs/*/tasks.md` | 各期任务清单 |

---

## 14. 后续路线图（第四期及以后 · 规划中）

> ⚠️ 本章为**规划**，尚未实现。每一期在动手前都应走前三期同样的流程：
> `头脑风暴/需求(requirements.md) → 设计(design.md) → 任务(tasks.md) → 实现 → 测试`，
> 规格存放于 `.kiro/specs/<feature>/`。下列范围、方向、工作量为**评估性质**，落地时以确认后的规格为准。
>
> 排序依据：业务价值 × 用户呼声 × 依赖关系。建议按顺序推进，但可根据实际经营反馈调整。

### 第四期（建议）：退货管理

**业务价值**：批发/门店实际经营中退货必然发生，且退货会反向影响库存与应收应付，是财务准确性的闭环缺口。秒账等同类产品均含此模块。

**范围建议**：
- 销售退货：超市退回货物 → 库存加回 → 冲减对应应收（或生成退款）
- 采购退货：退给供应商 → 库存扣减 → 冲减对应应付（或生成应退款）

**关键待确认问题**（需求阶段拍板）：
- 退货是否必须关联原销售单/进货单？还是允许无单退货？
- 退货是否按原价？部分退还是整单退？
- 已收款的单退货，如何处理已收金额（退款流水 / 挂客户预收）？
- 退货是否走审核？

**与现有结构的对接**：
- 复用库存内核：退货 = 反向的 increase/deduct（销售退货→increaseStock，采购退货→deductStock 的反向）
- 复用资金流水：退款记一条流水类型（需新增"销售退款/采购退款"枚举）
- 复用租户隔离：新表加 `S_TENANT_ID` 即自动隔离
- 新增表建议：`TAB_SALE_RETURN(_ITEM)`、`TAB_PURCHASE_RETURN(_ITEM)`

**粗略工作量**：中（1 个完整期次，后端为主 + 少量前端页面）。属"局部新增"，不动核心。

---

### 第五期（建议）：多员工账号 + 细粒度权限

**业务价值**：SaaS 商家几乎必然提出"给业务员开子账号，但不让看利润/欠款"。账号模型与鉴权入口在第三期已**预埋**，本期是"往预埋结构里填"，非重构。

**范围建议**：
- 商家管理员在自己租户下新增/停用员工账号
- 角色扩展：在 `RoleType` 加"业务员""仓管"等
- 细粒度权限：按角色控制菜单与接口（如业务员不可见财务模块）

**与现有结构的对接**：
- `TAB_ACCOUNT` 已支持一租户 N 账号 + `I_ROLE` 字段 → 直接用
- 鉴权走统一入口（`JwtAuthFilter` + 鉴权拦截器）→ 在此加角色-权限规则
- 前端菜单已有按角色显隐机制（平台/商家）→ 扩展为按细粒度角色

**关键待确认**：权限粒度到菜单级还是接口级/数据级？是否需要自定义角色？

**粗略工作量**：中。结构已备，主要是权限规则 + 账号管理界面 + 菜单权限控制。

---

### 第六期（建议）：移动端（App / 小程序）

**业务价值**：老板/业务员外出时用手机开单、查库存、查欠款，是高频移动场景。

**范围建议**：先做只读+高频操作（查库存、查欠款、录销售单），再逐步补全。

**与现有结构的对接**：
- 后端**全部 `/api` 接口可直接复用**（JWT + 请求头 + 纯 REST/JSON，第三期已为此预留）
- 无需改后端，仅新增移动端界面 + 登录态存储
- 技术选型建议：uni-app（一套码出小程序+App）或微信原生小程序
- 可能新增少量移动专属接口（扫码入库、微信登录）

**粗略工作量**：中高（纯新增前端工程，后端几乎不动）。

---

### 更远期（按需评估，非近期）

| 方向 | 触发条件 | 说明 |
|------|----------|------|
| 商家自助注册 | 要做规模化获客 | 复用开通商家逻辑 + 注册/审核流 |
| 计费与订阅 | 平台开始收费 | 新增计费模块 + 租户套餐字段 |
| 分库/分 Schema | 商家数上千、单库吃力 | 租户ID方案已铺路，按租户迁移 |
| 客户分级报价/账期 | 客户要求差异化定价 | 商品-客户价格表 + 账期字段 |
| 配送路线/司机管理 | 自建配送团队 | 独立物流模块 |
| 数据看板/BI | 经营分析需求 | 聚合报表 + 图表 |
| 财务凭证/报税对接 | 需财税合规 | 通常对接第三方代账，不自建 |

---

### 路线图执行原则（重要）

1. **一次一期，每期可用**：每期走完整规格→实现→测试→可上线，不并行开多个大改。
2. **先规格后编码**：动手前必须有确认的 requirements/design/tasks，避免返工。
3. **每期回归测试**：新增后跑 `mvn test`，确保既有测试不破（尤其租户隔离不变量）。
4. **守住不变量**：库存=流水累加、应收应付守恒、租户隔离——任何新期次不得破坏。
5. **优先"局部新增"**：能用现有内核（库存/资金流水/隔离）复用的，不另起炉灶。
6. **地基已固化**：多租户、账号角色、鉴权、分库路径、多端已预埋，后续期次几乎都属可控迭代，不应再出现"伤筋动骨"级改造（分库除外，但路径已备）。
