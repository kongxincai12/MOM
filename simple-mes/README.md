# 简易 MES 后端服务 (simple-mes-server)

一个基于主项目技术栈的**最小可运行 MES 后端**，只提供 REST 接口，覆盖 MES 核心业务闭环。

## 技术栈
- Java 8 + Spring Boot 2.7.3
- MyBatis-Plus 3.5.7（`BaseMapper` + 分页插件）
- Oracle（ojdbc8 + orai18n）+ Druid 连接池
- Lombok
- 统一响应体 `Result<T>`（code / message / data）

## 包结构
```
com.simplemes
├── SimpleMesApplication        启动类（@MapperScan）
├── common                      Result / BusinessException / GlobalExceptionHandler
├── config                      MybatisPlusConfig（Oracle 分页）
├── workorder                   生产工单：CRUD + 状态流转（下发/开工/完工/关闭）
├── production                  生产报工：人工/设备报工，累加工单完成数量
├── material                    物料主数据 + 库存 + 出入库流水（入库/出库/领料/退料）
├── equipment                   设备台账 + 状态维护
└── quality                     报废 / 返工 / 质量报表（良率、报废率聚合）
```
每个模块统一分层：`entity / mapper / service / service.impl / controller`。

## 快速开始
1. 建表：执行 [`sql/oracle_ddl.sql`](sql/oracle_ddl.sql) 初始化 8 张表。
2. 配置：修改 `src/main/resources/application.properties` 中的 Oracle 连接（url / username / password）。
3. 启动：
   ```bash
   # 在本目录下
   ..\mvnw.cmd -f pom.xml spring-boot:run
   ```
   默认端口 `9901`。

## 接口一览（前缀见各 Controller）
| 模块 | 基础路径 | 主要接口 |
| --- | --- | --- |
| 生产工单 | `/api/work-order` | `POST` 建单、`PUT` 修改、`GET /page`、`GET /{id}`、`DELETE /{id}`；`POST /{id}/release`、`/start`、`/finish`、`/close` 状态流转 |
| 生产报工 | `/api/production-report` | `POST` 报工（校验工单已开工并累加完成数）、`GET /page` |
| 物料库存 | `/api/material` | 物料 `POST/PUT/DELETE/GET /page`；库存 `GET /stock/page`、`POST /stock/operate`（IN/OUT/ISSUE/RETURN）、`GET /stock/record/page` |
| 设备管理 | `/api/equipment` | `POST/PUT/DELETE`、`GET /{id}`、`GET /page`、`POST /{id}/status` |
| 质量管理 | `/api/quality` | 报废 `POST /scrap`、`GET /scrap/page`；返工 `POST /rework`、`POST /rework/{id}/status`、`GET /rework/page`；报表 `GET /report` |

## 典型业务流（示例）
1. `POST /api/work-order` 建单 → `POST /api/work-order/{id}/release` → `/start`。
2. `POST /api/production-report` 报工（合格数累加到工单 `finishQty`）。
3. `POST /api/material/stock/operate`（`ISSUE` 领料 / `RETURN` 退料）。
4. `POST /api/quality/scrap` 报废、`POST /api/quality/rework` 返工。
5. `GET /api/quality/report?orderNo=xxx` 查看良率 / 报废率。

## 说明
- 主键为 32 位 UUID（MyBatis-Plus `IdType.ASSIGN_UUID`），无需序列。
- 本项目为独立 Maven 工程，与主项目 `MOM` 互不影响，可单独导入 IDEA 运行。
- 未内置鉴权，可按主项目 JWT 方式扩展。
