# 腾讯云一键部署指南

本系统通过 Docker Compose 部署 **后端 + 前端(Nginx) 两个容器**；**数据库使用服务器上已有的 MySQL**（不由本编排启动 MySQL 容器）。表结构由后端启动时 Flyway 自动创建。

## 一、准备工作

1. **购买腾讯云 CVM**
   - 操作系统：推荐 Ubuntu 22.04 LTS（或 TencentOS Server / CentOS 7+）
   - 配置：建议 2 核 4G 及以上（前端 Vite + 后端 Maven 构建期占内存）
   - 系统盘：40G 及以上

2. **开放安全组端口**

   在腾讯云控制台 → CVM → 安全组，放通入站规则：
   - `8090`（TCP，或你在 `.env` 中设置的 `WEB_PORT`）：前端 Web 访问
   - `22`（TCP）：SSH 登录

3. **在已有 MySQL 中创建空库**

   本系统连接服务器上已有的 MySQL（Docker 容器，3306 已映射到宿主机）。部署前先建空库（表结构无需手动建，Flyway 自动创建）：

   ```bash
   mysql -uroot -pHH123456 -e "CREATE DATABASE IF NOT EXISTS CONSUMABLES DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;"
   ```

---

## 二、一键部署

1. **上传项目到服务器**（任选其一）：

   ```bash
   # 方式一：git clone
   git clone <你的仓库地址> my-consumables-platform
   cd my-consumables-platform

   # 方式二：本地打包上传
   # scp -r ./my-consumables-platform root@<服务器IP>:/root/
   ```

2. **执行一键部署脚本**：

   ```bash
   cd my-consumables-platform
   chmod +x deploy.sh
   sudo ./deploy.sh
   ```

   脚本自动完成：
   - 检测并安装 Docker（腾讯云镜像加速）
   - 配置 Docker 镜像加速器
   - 生成 `.env` 配置（首次）
   - 提示确认已创建空库
   - 构建并启动后端 + 前端两个容器

3. **访问系统**：

   ```
   http://<服务器公网IP>:8090
   ```

   默认平台管理员：`admin` / `admin123`（**生产务必改**，见配置说明）。

---

## 三、配置说明

编辑 `.env`（由 `.env.example` 复制，首次部署自动生成）：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_HOST` | 数据库地址（容器通过此访问宿主机 MySQL） | `host.docker.internal` |
| `DB_PORT` | 数据库端口 | `3306` |
| `DB_NAME` | 数据库名 | `CONSUMABLES` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `HH123456` |
| `WEB_PORT` | 前端对外访问端口 | `8090` |

`application.yml` 中还有两个生产务必修改的配置（修改后重新构建后端）：

```yaml
consumables:
  jwt:
    secret: <改为至少32字符的强密钥>
  platform-admin:
    password: <改为强密码>
```

修改 `.env` 后执行 `docker compose up -d --build backend` 生效。

---

## 四、日常更新（改代码后）

`deploy.sh` 用于**首次部署**（装 Docker、配镜像、初始化），后续改代码只用 `update.sh`。

```bash
sudo ./update.sh            # 更新前端 + 后端
sudo ./update.sh backend    # 只更新后端
sudo ./update.sh frontend   # 只更新前端
sudo ./update.sh --no-pull  # 跳过 git pull（手动传代码时）
```

后端更新时 Flyway 会自动执行新增迁移脚本（如新一期的建表），无需手动操作。

---

## 五、常用运维命令

```bash
# 查看容器状态
docker compose ps

# 查看后端日志（含 Flyway 建表日志）
docker compose logs -f backend

# 查看前端日志
docker compose logs -f frontend

# 重启全部
docker compose restart

# 停止并删除容器（不影响数据库，因为数据库由外部 MySQL 管理）
docker compose down
```

---

## 六、数据备份

数据存储在已有 MySQL 中，由其自行管理。定期备份：

```bash
# 连接已有 MySQL 容器备份（容器名以实际为准）
docker exec <已有MySQL容器名> mysqldump -uroot -pHH123456 CONSUMABLES > backup_$(date +%Y%m%d).sql
```

---

## 七、架构说明

```
         公网 8090
              |
   [frontend - Nginx]    静态资源 + /api 反向代理
              |  容器内网
   [backend - Spring Boot :8080]
              |  host.docker.internal:3306
   [服务器宿主机上已有的 MySQL 容器（3306 已映射宿主机）]
```

- 后端通过 `host.docker.internal:3306` 连宿主机上的 MySQL，compose 已配置 `host-gateway`，无需额外网络配置。
- 表结构由 Flyway 在首次启动时自动建（`V1 -> V2 -> V3`），只需预先建好空库。
- 前端 Nginx 将 `/api` 请求反向代理到后端容器，规避跨域。
- 本编排不维护 MySQL 数据卷，数据安全由现有 MySQL 负责。
