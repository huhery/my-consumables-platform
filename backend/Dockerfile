# 多阶段构建：第一阶段用 Maven 构建 jar，第二阶段用精简 JRE 运行
# ============ 构建阶段 ============
FROM maven:3.8-jdk-8 AS builder
WORKDIR /build
# 使用阿里云 Maven 镜像加速依赖下载
COPY settings.xml /root/.m2/settings.xml
# 先拷贝 pom 单独下载依赖，利用 Docker 层缓存加速后续构建
COPY pom.xml .
RUN mvn -q dependency:go-offline -B || true
COPY src ./src
RUN mvn -q clean package -DskipTests -B

# ============ 运行阶段 ============
FROM eclipse-temurin:8-jre
WORKDIR /app
# 拷贝构建产物
COPY --from=builder /build/target/consumables-backend-*.jar app.jar
EXPOSE 8080
# 时区设为上海，JVM 参数可按需调整
ENV TZ=Asia/Shanghai
ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "app.jar"]
