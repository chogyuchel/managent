# 智能学习辅助系统

一个前后端分离的培训机构管理系统，提供部门、员工、班级、学员的增删改查，学员数据统计，操作日志记录以及基于阿里云 OSS 的文件上传。

## 功能概览

| 模块 | 说明 |
| --- | --- |
| 部门管理 | 部门增删改查，部门下存在员工时禁止删除 |
| 员工管理 | 员工增删改查、批量删除、按姓名/性别/入职日期筛选、工作经历维护 |
| 班级管理 | 班级增删改查，班级下存在学员时禁止删除，班主任/学科关联 |
| 学员管理 | 学员增删改查、按学历/班级筛选、违纪处理、批量删除 |
| 数据统计 | 员工职位分布、班级人数统计（ECharts 图表） |
| 操作日志 | 自动记录登录后所有增删改操作的类名、方法、参数、耗时 |
| 文件上传 | 基于阿里云 OSS 的图片/文件上传 |

## 技术栈

**后端**：Spring Boot 4.0.9、MyBatis 4.0.1、MySQL、PageHelper、JWT（jjwt 0.9.1）、阿里云 OSS、Lombok、JDK 17

**前端**：Vue 3.5、Vite 6、Vue Router 4、Element Plus 2.9、Axios、ECharts 5.5

## 项目结构

```
managent/
├── back/                          # 前端（Vue 3 + Vite）
│   ├── src/
│   │   ├── api/                   # Axios 封装与接口定义
│   │   ├── components/            # 通用组件（分页、页头、空状态）
│   │   ├── layout/                # 后台布局
│   │   ├── router/                # 路由与登录守卫
│   │   ├── utils/                 # 工具函数
│   │   └── views/                 # 页面：登录、部门、员工、班级、学员、统计、日志
│   └── vite.config.js             # 开发代理 /api → localhost:8080
├── sql/
│   └── init.sql                   # 建表语句与演示数据
└── web-ai/
    └── web-management/            # 后端（Spring Boot）
        └── src/main/
            ├── java/com/example/
            │   ├── controller/    # 接口层
            │   ├── service/       # 业务层（接口 + impl）
            │   ├── mapper/        # 数据访问层（注解 SQL）
            │   ├── pojo/          # 实体、查询参数、返回 VO
            │   ├── aop/           # 操作日志切面
            │   ├── interceptor/   # JWT 拦截器
            │   └── utils/         # JWT、ThreadLocal、OSS 工具
            └── resources/
                ├── application.yml
                └── com/example/mapper/   # 动态 SQL 的 XML 映射
```

## 环境要求

- JDK 17
- Maven 3.9+（项目未提供 Maven Wrapper）
- Node.js 18+ 与 npm
- MySQL 8.x

## 快速开始

### 1. 准备数据库

仓库提供了完整的初始化脚本 `sql/init.sql`，包含 6 张业务表（`dept`、`emp`、`emp_expr`、`clazz`、`student`、`operate_log`）以及演示数据：

```bash
mysql -uroot -p < sql/init.sql
```

脚本会创建数据库 `management` 并重建表结构，请勿在有生产数据的库上执行。

> ⚠️ 脚本含 `DROP TABLE IF EXISTS`，重复执行会清空原有数据。

演示账号：`admin` / `123456`（其余员工账号见 `emp` 表，密码均为 `123456`）。

### 2. 配置并启动后端

修改 `web-ai/web-management/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/management
    username: root
    password: your-password
```

文件上传依赖阿里云 OSS，需先设置环境变量：

```bash
export OSS_ACCESS_KEY_ID=your-access-key-id
export OSS_ACCESS_KEY_SECRET=your-access-key-secret
```

启动：

```bash
cd web-ai/web-management
mvn clean compile
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
cd back
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`，开发环境通过 Vite 代理把 `/api` 转发到后端。如需修改后端地址，可设置环境变量 `VITE_PROXY_TARGET`。

### 4. 登录

使用 `emp` 表中已有的账号登录。登录成功后接口返回 JWT，前端在后续请求的 `token` 请求头中携带该令牌，有效期 12 小时。

## 接口约定

- 统一响应格式：

```json
{ "code": 1, "msg": "success", "data": null }
```

`code` 为 `1` 表示成功，`0` 表示失败。

- 分页响应格式：

```json
{ "total": 100, "rows": [] }
```

- 除 `POST /login` 外，所有接口都需携带请求头 `token`；缺失或解析失败返回 HTTP 401。
- 分页参数统一为 `page`（默认 1）与 `pageSize`（默认 10）。

## 常用命令

```bash
# 后端（在 web-ai/web-management 目录）
mvn clean compile
mvn spring-boot:run
mvn clean package -DskipTests

# 前端（在 back 目录）
npm run dev
npm run build
```

## 注意事项

- `application.yml` 中的数据库账号密码为本地开发示例，部署前请自行修改。
- `JwtUtils` 中的签名密钥为演示用硬编码值，生产环境请改为从配置或环境变量读取。
