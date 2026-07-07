#!/usr/bin/env bash
#
# 超市耗材供应商系统 - 日常更新脚本
# 用途：改代码后重新构建并重启服务，数据库容器与数据保持不动
#
# 用法：
#   ./update.sh            # 更新前端 + 后端
#   ./update.sh backend    # 只更新后端
#   ./update.sh frontend   # 只更新前端
#   ./update.sh --no-pull  # 跳过 git pull（已手动上传代码时使用）
#
set -euo pipefail

log() { echo -e "\033[32m[更新]\033[0m $1"; }
warn() { echo -e "\033[33m[警告]\033[0m $1"; }

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# ---------- 选择编排命令 ----------
if docker compose version >/dev/null 2>&1; then
  COMPOSE="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE="docker-compose"
else
  echo "未检测到 Docker Compose，请先运行 deploy.sh 完成首次部署。"
  exit 1
fi

# ---------- 解析参数 ----------
SERVICES=""
DO_PULL=1
for arg in "$@"; do
  case "$arg" in
    backend|frontend) SERVICES="$SERVICES $arg" ;;
    --no-pull) DO_PULL=0 ;;
    *) warn "忽略未知参数：$arg" ;;
  esac
done
# 默认更新前后端（不含数据库）
SERVICES="${SERVICES:-backend frontend}"

# ---------- 拉取最新代码 ----------
if [ "$DO_PULL" -eq 1 ] && [ -d .git ]; then
  log "拉取最新代码..."
  git pull --ff-only || warn "git pull 未成功，使用当前代码继续"
else
  log "跳过 git pull，使用当前目录代码"
fi

# ---------- 重新构建并重启指定服务 ----------
log "重新构建并重启服务：$SERVICES（数据库不受影响）"
# shellcheck disable=SC2086
$COMPOSE up -d --build $SERVICES

# ---------- 清理悬空镜像，释放磁盘 ----------
log "清理无用的旧镜像..."
docker image prune -f >/dev/null 2>&1 || true

log "更新完成，当前服务状态："
$COMPOSE ps
