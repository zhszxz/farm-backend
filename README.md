# farm-backend · 农副产品平台后端

Vue 3 + Spring Boot 3 + MySQL 8 项目的后端服务。

## 技术栈

- Spring Boot 3.2.5 / Java 17
- MyBatis-Plus 3.5.9
- MySQL 8（数据库 `farm_platform`）
- JWT（jjwt）+ BCrypt（spring-security-crypto）
- Lombok

## 目录结构

```
src/main/java/com/farm
├── FarmApplication.java      # 启动类（@MapperScan）
├── common/                   # 统一响应 Result / 错误码 / 全局异常
├── config/                   # MyBatis-Plus 分页 / Web拦截器与CORS
├── security/                 # JWT、登录上下文、认证拦截器
├── entity/                   # 14 张表实体
├── mapper/                   # MyBatis-Plus Mapper
├── dto/                      # 请求/响应 DTO
├── service/                  # 业务逻辑（AuthService）
└── controller/               # REST 控制器
```

## 快速开始

### 1. 初始化数据库

已提供脚本 `../farm_platform_schema.sql`，执行：

```bash
mysql -u root -p < ../farm_platform_schema.sql
```

### 2. 修改数据库配置

编辑 `src/main/resources/application.yml` 中的 datasource 用户名/密码（当前为本地 `root/739627`）。

### 3. 启动

```bash
mvn spring-boot:run
```

或打包运行：

```bash
mvn clean package -DskipTests
java -jar target/farm-backend-0.0.1-SNAPSHOT.jar
```

### 4. 验证

```bash
curl http://localhost:8080/api/health
# {"code":0,"message":"ok","data":{"status":"UP","db":"connected"}}

curl http://localhost:8080/api/categories
```

## 已实现接口

**认证与用户**

| 接口 | 说明 |
| --- | --- |
| `POST /api/auth/sms-code` | 发送验证码（开发模式打印到日志） |
| `POST /api/auth/register` | 注册 |
| `POST /api/auth/login/password` | 密码登录 |
| `POST /api/auth/login/sms` | 验证码登录 |
| `GET /api/user/me` | 当前用户信息（需 JWT） |
| `POST /api/shop/apply` | 申请成为商家（需 JWT） |
| `GET /api/shop/my-apply` | 我的入驻状态（需 JWT） |

**商家工作台（需商家角色，接口均从 JWT 解析店铺，防越权）**

| 接口 | 说明 |
| --- | --- |
| `GET/PUT /api/merchant/shop` | 店铺信息查询/更新 |
| `GET/POST/PUT/DELETE /api/merchant/links` | 拼多多链接管理 |
| `GET/POST/PUT/DELETE /api/merchant/products` | 产品管理 + `PUT /{id}/status` 上下架 |
| `GET/POST/PUT/DELETE /api/merchant/sales` | 销售记录 + `POST /sales/import` Excel 导入 |
| `GET/POST/PUT/DELETE /api/merchant/productions` | 产量记录 |
| `GET/POST/PUT/DELETE /api/merchant/costs` | 成本记录 |
| `GET /api/merchant/click-stats` | 跳转统计 |

**数据报表（商家看自己，管理员可看全平台）**

| 接口 | 说明 |
| --- | --- |
| `GET /api/reports/profit` | 利润核算（按年） |
| `GET /api/reports/trend` | 单品跨年趋势（产量/销量/销售额） |
| `GET /api/reports/product-compare` | 某年各产品销售额对比 |
| `GET /api/reports/channel-share` | 某年渠道占比 |
| `GET /api/reports/category-share` | 某年大类占比 |
| `GET /api/reports/top` | 销售额 Top N 排行 |
| `GET /api/reports/export` | 利润报表导出 Excel |

**前台展示（公开）**

| 接口 | 说明 |
| --- | --- |
| `GET /api/health` | 健康检查（含 DB 连通性） |
| `GET /api/categories` | 分类列表 |
| `GET /api/home/banner` | 首页轮播（推荐位上架产品） |
| `GET /api/home/recommend` | 应季推荐 |
| `GET /api/products` | 产品列表（小类/大类/关键字/季节筛选） |
| `GET /api/products/{id}` | 产品详情（含商家与拼多多链接） |
| `GET /api/shops/{id}` | 商家主页 |
| `GET /api/shops/{id}/products` | 商家上架产品 |
| `POST /api/clicks` | 记录跳转（引流统计） |

**收藏留言（需登录）**

| 接口 | 说明 |
| --- | --- |
| `POST /api/favorites` | 收藏 |
| `DELETE /api/favorites/{targetType}/{targetId}` | 取消收藏 |
| `GET /api/user/favorites` | 我的收藏 |
| `POST /api/messages` | 留言 |
| `GET /api/messages/sent` | 我发出的留言 |
| `GET /api/messages/received` | 收到的留言（商家） |
| `POST /api/messages/{id}/reply` | 回复留言（商家） |

**后台管理（仅管理员）**

| 接口 | 说明 |
| --- | --- |
| `GET /api/admin/overview` | 平台整体统计 |
| `GET /api/admin/shops` + `PUT /{id}/audit` | 商家列表 + 审核 |
| `GET /api/admin/users` + `PUT /{id}/status` | 用户列表 + 禁用/启用 |
| `GET /api/admin/products` + `PUT /{id}/status` | 产品列表 + 状态变更 |
| `GET/POST/PUT/DELETE /api/admin/categories` | 分类管理 |
| `GET/POST/PUT/DELETE /api/admin/channels` | 渠道字典 |
| `GET/POST/PUT/DELETE /api/admin/cost-types` | 成本类型字典 |
| `GET/POST/PUT/DELETE /api/admin/dicts` | 通用字典 |
| `GET /api/admin/click-stats` | 全平台跳转统计 |

## 待办（开发计划）

- 短信验证码当前为**内存存储 + 日志打印**，上线前替换为 Redis + 短信服务
- 密码当前硬编码在 yml，上线前改为环境变量注入
- 销售记录/产品列表当前返回全量，量大后可加分页

## 关联文档

- 需求：`../农产品平台需求分析.md`
- 数据库：`../数据库设计.md`、`../farm_platform_schema.sql`
- 接口：`../接口设计.md`
- 原型：`../UI原型设计.md`、`../原型-线框图.html`
