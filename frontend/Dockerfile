# 多阶段构建：第一阶段 Node 构建静态资源，第二阶段 Nginx 托管
# ============ 构建阶段 ============
FROM node:20-alpine AS builder
WORKDIR /build
COPY package.json ./
# 使用国内镜像加速依赖安装
RUN npm install --registry=https://registry.npmmirror.com
COPY . .
RUN npm run build

# ============ 运行阶段 ============
FROM nginx:1.25-alpine
# 拷贝构建产物到 Nginx 静态目录
COPY --from=builder /build/dist /usr/share/nginx/html
# 拷贝 Nginx 配置（处理 history 路由与 /api 反向代理）
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
