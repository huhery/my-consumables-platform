# 超市耗材供应商系统（第一期）

面向超市耗材供应商的内部后台管理系统。第一期打通主线：**进货入库 → 多地点库存 → 出货（批发 / 门店散卖）**。

## 技术栈

| 层 | 选型 |
|----|------|
| 后端 | Spring Boot 2.7 + MyBatis 3 + MySQL 8 + Flyway（JDK 8） |
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios |

前后端置于同一仓库（monorepo）：`backend/` 后端，`frontend/` 前端。第一期为一人公司内部使用，**免登录**。

## 目录结构

```
my-consumables-platform/
├── backend/      Spring Boot 后端
├── frontend/     Vue 3 管理后台
└── .kiro/        规格文档（需求 / 设计 / 任务）
```

## 一、数据库初始化

1. 创建数据库（字符集 utf8mb4）：

   ```sql
   CREATE DATABASE CONSUMABLES DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 表结构由 **Flyway** 在后端启动时自动创建（脚本位于 `backend/src/main/resources/db/migration/V1__init_schema.sql`），无需手工建表。

3. 按实际环境修改 `backend/src/main/resources/application.yml` 中的数据源配置：

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/CONSUMABLES?...
       username: root
       password: root
   ```

## 二、启动后端

```bash
cd backend
mvn spring-boot:run
```

默认端口 `8080`。启动后 Flyway 自动建表。

运行测试：

```bash
cd backend
mvn test
```

## 三、启动前端

```bash
cd frontend
npm install
npm run dev
```

默认端口 `5173`，开发期通过 Vite 代理将 `/api` 转发到后端 `http://localhost:8080`。

> 注：若 `npm install` 因网络代理返回 403，可临时清空代理环境变量并指定镜像：
> ```bash
> npm install --registry=https://registry.npmmirror.com
> ```

生产构建：

```bash
cd frontend
npm run build
```

## 四、核心业务流与不变量

业务主线：

```
整箱采购入库 ──→ [多仓库 / 门店库存] ──→ 整箱批发出库（发超市）
                       │
                       └──→ 门店拆箱散卖出库（按个 / 散件）
```

系统始终维持的核心不变量（已由集成测试覆盖）：

1. **库存一致性**：任意商品在任意地点的库存数量 = 其全部出入库流水累加之和。
2. **单位折算守恒**：落库基本单位数量 = 录入数量 × 换算率。
3. **防超卖**：出库时库存不足直接拒绝（数据库条件更新保证）。
4. **门店约束**：仅门店类型地点可散卖。

## 五、功能模块（第一期）

- 基础数据：商品档案（含包装单位换算）、库存地点（仓库 / 门店）、客户、供应商
- 库存管理：库存查询、出入库流水、手工调整
- 进货：进货单录入（保存即入库）
- 出货：批发出货（录单 → 发货扣库存）、门店散卖（保存即出库）

## 六、第二期功能：应收应付与资金管理

第二期在同一套系统内扩展了"钱"的管理，新增 `finance` 模块：

- **应收账款**：批发发货后自动生成应收；支持销售收款（可关联单据按单冲减，也可不关联冲减客户总欠款）、部分与多次收款；销售欠款汇总（按客户）。
- **应付账款**：进货后自动生成应付；采购付款与应收镜像；采购付款汇总（按供应商）。
- **资金流水**：销售收款、采购付款、费用支出、其他收入统一台账。
- **费用/收入记账**：按分类记账，预置房租/油费/工资/水电分类，支持分类汇总。
- **送货提醒**：待发货批发单清单，按期望送达日期排序（批发录单可填期望送达日期）。
- **经营报表**：资金汇总（收入/支出/净额 + 应收未收 + 应付未付）、费用分类汇总。

核心不变量（已由测试覆盖）：应收守恒（客户欠款 = 应收未收之和）、应付守恒、收付款必生成资金流水、超额收付款拒绝、门店散卖不挂账。

数据库表结构由 Flyway `V2__finance_schema.sql` 在启动时自动增量创建，无需手工建表；升级部署时执行 `./update.sh backend` 即可自动迁移。

## 七、多租户（SaaS 平台）

系统已改造为多商家 SaaS 平台，支持多个商家共用一套平台且数据互相隔离：

- **登录认证**：JWT 无状态令牌 + BCrypt 密码加密。所有业务接口需登录后携带令牌访问；登录接口匿名。
- **数据隔离**：所有业务表含 S_TENANT_ID，由 MyBatis 拦截器（JSqlParser）在框架层自动过滤/填充，商家只能访问自己的数据。租户内唯一（不同商家可用相同商品编码）。
- **角色**：平台管理员（管理商家）与商家管理员（使用业务功能）。账号模型预埋"一租户多账号+角色"，当前每商家一个账号。
- **平台管理后台**：平台管理员登录后进入商家管理界面，可开通/停用商家。
- **默认平台管理员**：首次启动自动创建（登录名 `admin`，初始密码 `admin123`，可通过配置 `consumables.platform-admin.*` 修改；**生产务必改密码**）。
- **多端预留**：认证基于 JWT + 请求头、接口纯 REST/JSON、不依赖 Cookie/Session，将来 App/小程序可直接复用全部后端接口。

### 登录使用流程

1. 平台管理员用 `admin` 登录 → 进入商家管理 → 开通商家（填商家名 + 初始登录名 + 密码）。
2. 商家用分配的账号登录 → 进入业务界面，只能看到自己的数据。

配置项（application.yml）：

```yaml
consumables:
  jwt:
    secret: <生产请改为强密钥>
    expire-millis: 604800000   # 令牌有效期，默认7天
  platform-admin:
    login-name: admin
    password: admin123          # 生产务必修改
```

## 八、不在当前范围

退货（销售/采购退货）、商家自助注册、多员工账号管理界面与细粒度权限、分库、计费订阅、手机 App、财务凭证/报税、配送路线规划。
