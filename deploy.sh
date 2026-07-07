#!/usr/bin/env bash
#
# 超市耗材供应商系统 - 腾讯云一键部署脚本
# 适用环境：腾讯云 CVM（推荐 Ubuntu 20.04+ / 22.04 或 TencentOS / CentOS 7+）
# 用法：
#   chmod +x deploy.sh
#   ./deploy.sh
#
# 功能：
#   1. 自动检测并安装 Docker 与 Docker Compose（使用腾讯云镜像源加速）
#   2. 初始化 .env 配置文件
#   3. 构建并启动 后端 + 前端 两个容器（数据库用服务器已有 MySQL）
#   4. 输出访问地址

set -euo pipefail

# ---------- 工具函数 ----------
log() { echo -e "\033[32m[部署]\033[0m $1"; }
warn() { echo -e "\033[33m[警告]\033[0m $1"; }
err() { echo -e "\033[31m[错误]\033[0m $1"; }

# ---------- 必须 root 或 sudo ----------
if [ "$(id -u)" -ne 0 ]; then
  err "请使用 root 用户或 sudo 运行此脚本：sudo ./deploy.sh"
  exit 1
fi

# 脚本所在目录（即项目根目录）
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# ---------- 1. 安装 Docker ----------
install_docker() {
  if command -v docker >/dev/null 2>&1; then
    log "Docker 已安装：$(docker --version)"
    return
  fi
  log "未检测到 Docker，开始安装（使用腾讯云镜像源）..."
  # 使用腾讯云的 Docker 安装镜像，加速国内下载
  curl -fsSL https://mirrors.tencent.com/docker-ce/linux/ubuntu/gpg | apt-key add - 2>/dev/null || true
  # 通用安装脚本（带腾讯云镜像）
  curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun || curl -fsSL https://get.docker.com | bash
  systemctl enable docker
  systemctl start docker
  log "Docker 安装完成：$(docker --version)"
}

# ---------- 2. 校验 Docker Compose ----------
check_compose() {
  if docker compose version >/dev/null 2>&1; then
    COMPOSE="docker compose"
  elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE="docker-compose"
  else
    err "未检测到 Docker Compose，请升级 Docker 到包含 compose 插件的版本"
    exit 1
  fi
  log "使用编排命令：$COMPOSE"
}

# ---------- 3. 配置 Docker 镜像加速 ----------
config_mirror() {
  local daemon=/etc/docker/daemon.json
  if [ ! -f "$daemon" ]; then
    log "配置 Docker 镜像加速器（腾讯云）..."
    mkdir -p /etc/docker
    cat > "$daemon" <<'EOF'
{
  "registry-mirrors": ["https://mirror.ccs.tencentyun.com"]
}
EOF
    systemctl daemon-reload
    systemctl restart docker
  fi
}

# ---------- 4. 初始化环境变量 ----------
init_env() {
  if [ ! -f .env ]; then
    log "初始化 .env 配置文件..."
    cp .env.example .env
    warn "已生成 .env。请确认其中数据库连接（DB_HOST/DB_PORT/DB_NAME/DB_USERNAME/DB_PASSWORD）指向服务器上已有的 MySQL。"
  else
    log ".env 已存在，沿用现有配置。"
  fi
}

# ---------- 4.1 提示创建数据库 ----------
check_database() {
  local db_name
  db_name=$(grep -E '^DB_NAME=' .env | cut -d= -f2)
  db_name=${db_name:-CONSUMABLES}
  warn "请确保已在现有 MySQL 中创建空库 ${db_name}（表结构由后端 Flyway 启动时自动创建）："
  echo "      CREATE DATABASE IF NOT EXISTS ${db_name} DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;"
}

# ---------- 5. 构建并启动 ----------
deploy() {
  log "开始构建并启动容器（首次构建较慢，请耐心等待）..."
  $COMPOSE up -d --build
  log "等待服务启动..."
  sleep 5
  $COMPOSE ps
}

# ---------- 主流程 ----------
install_docker
check_compose
config_mirror
init_env
check_database
deploy

# ---------- 输出访问信息 ----------
WEB_PORT=$(grep -E '^WEB_PORT=' .env | cut -d= -f2 || echo 80)
PUBLIC_IP=$(curl -fsSL https://metadata.tencentyun.com/latest/meta-data/public-ipv4 2>/dev/null || echo "<服务器公网IP>")
echo ""
log "部署完成！"
echo "  访问地址： http://${PUBLIC_IP}:${WEB_PORT}"
echo ""
warn "请在腾讯云控制台「安全组」放通入站端口 ${WEB_PORT}（TCP）。"
warn "查看日志： $COMPOSE logs -f backend"
warn "停止服务： $COMPOSE down"
