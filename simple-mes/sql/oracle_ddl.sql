-- ============================================================
-- 简易 MES 后端服务 - Oracle 建表脚本
-- 说明：手动执行本脚本初始化表结构，应用不自动建表。
-- 主键统一使用 VARCHAR2(64)，由应用层生成 32 位 UUID（无横线）。
-- ============================================================

-- ------------------------------------------------------------
-- 1. 生产工单
-- ------------------------------------------------------------
CREATE TABLE SMES_WORK_ORDER (
    ID            VARCHAR2(64)  PRIMARY KEY,
    ORDER_NO      VARCHAR2(64)  NOT NULL,           -- 工单号（业务唯一）
    PRODUCT_CODE  VARCHAR2(64)  NOT NULL,           -- 产品物料编码
    PRODUCT_NAME  VARCHAR2(200),                    -- 产品名称
    PLAN_QTY      NUMBER(18,4)  DEFAULT 0,          -- 计划数量
    FINISH_QTY    NUMBER(18,4)  DEFAULT 0,          -- 已完成（合格）数量
    LINE_CODE     VARCHAR2(64),                     -- 产线
    STATUS        VARCHAR2(20)  DEFAULT 'CREATED',   -- CREATED/RELEASED/STARTED/FINISHED/CLOSED
    START_DATE    DATE,
    END_DATE      DATE,
    REMARK        VARCHAR2(500),
    CREATE_TIME   DATE          DEFAULT SYSDATE,
    UPDATE_TIME   DATE
);
CREATE UNIQUE INDEX UX_SMES_WORK_ORDER_NO ON SMES_WORK_ORDER (ORDER_NO);

-- ------------------------------------------------------------
-- 2. 生产报工（人工 / 设备）
-- ------------------------------------------------------------
CREATE TABLE SMES_PRODUCTION_REPORT (
    ID            VARCHAR2(64)  PRIMARY KEY,
    ORDER_NO      VARCHAR2(64)  NOT NULL,           -- 工单号
    REPORT_TYPE   VARCHAR2(20)  DEFAULT 'MANUAL',    -- MANUAL/EQUIPMENT
    EMPLOYEE_ID   VARCHAR2(64),                     -- 报工工号
    EQUIPMENT_ID  VARCHAR2(64),                     -- 设备ID（设备报工时）
    QUALIFIED_QTY NUMBER(18,4)  DEFAULT 0,          -- 合格数量
    WORK_HOURS    NUMBER(10,2)  DEFAULT 0,          -- 工时
    SHIFT         VARCHAR2(20),                     -- 班次 DAY/NIGHT
    REPORT_TIME   DATE          DEFAULT SYSDATE,
    CREATE_TIME   DATE          DEFAULT SYSDATE
);
CREATE INDEX IX_SMES_PROD_REPORT_ORDER ON SMES_PRODUCTION_REPORT (ORDER_NO);

-- ------------------------------------------------------------
-- 3. 物料主数据
-- ------------------------------------------------------------
CREATE TABLE SMES_MATERIAL (
    ID            VARCHAR2(64)  PRIMARY KEY,
    MATERIAL_CODE VARCHAR2(64)  NOT NULL,           -- 物料编码
    MATERIAL_NAME VARCHAR2(200),
    SPEC          VARCHAR2(200),                    -- 规格
    UNIT          VARCHAR2(20),                     -- 单位
    CATEGORY      VARCHAR2(20)  DEFAULT 'RAW',      -- RAW原材料/SEMI半成品/FINISH成品
    CREATE_TIME   DATE          DEFAULT SYSDATE,
    UPDATE_TIME   DATE
);
CREATE UNIQUE INDEX UX_SMES_MATERIAL_CODE ON SMES_MATERIAL (MATERIAL_CODE);

-- ------------------------------------------------------------
-- 4. 物料库存
-- ------------------------------------------------------------
CREATE TABLE SMES_MATERIAL_STOCK (
    ID            VARCHAR2(64)  PRIMARY KEY,
    MATERIAL_CODE VARCHAR2(64)  NOT NULL,
    WAREHOUSE     VARCHAR2(64)  DEFAULT 'WH01',     -- 仓库
    QTY           NUMBER(18,4)  DEFAULT 0,          -- 库存数量
    UNIT          VARCHAR2(20),
    UPDATE_TIME   DATE          DEFAULT SYSDATE
);
CREATE UNIQUE INDEX UX_SMES_STOCK_MAT_WH ON SMES_MATERIAL_STOCK (MATERIAL_CODE, WAREHOUSE);

-- ------------------------------------------------------------
-- 5. 出入库流水（领料 / 退料 / 入库 / 出库）
-- ------------------------------------------------------------
CREATE TABLE SMES_STOCK_RECORD (
    ID            VARCHAR2(64)  PRIMARY KEY,
    MATERIAL_CODE VARCHAR2(64)  NOT NULL,
    WAREHOUSE     VARCHAR2(64)  DEFAULT 'WH01',
    BIZ_TYPE      VARCHAR2(20)  NOT NULL,           -- IN入库/OUT出库/ISSUE领料/RETURN退料
    QTY           NUMBER(18,4)  DEFAULT 0,
    ORDER_NO      VARCHAR2(64),                     -- 关联工单（领/退料时）
    OPERATOR      VARCHAR2(64),
    CREATE_TIME   DATE          DEFAULT SYSDATE
);
CREATE INDEX IX_SMES_STOCK_RECORD_MAT ON SMES_STOCK_RECORD (MATERIAL_CODE);

-- ------------------------------------------------------------
-- 6. 设备台账
-- ------------------------------------------------------------
CREATE TABLE SMES_EQUIPMENT (
    ID          VARCHAR2(64) PRIMARY KEY,
    EQP_CODE    VARCHAR2(64) NOT NULL,              -- 设备编号
    EQP_NAME    VARCHAR2(200),
    EQP_TYPE_ID VARCHAR2(64),                       -- 设备类型
    LOCATION    VARCHAR2(100),                      -- 位置/线体
    STATUS      VARCHAR2(20) DEFAULT 'IDLE',        -- RUNNING运行/IDLE空闲/MAINTENANCE维修
    CREATE_TIME DATE         DEFAULT SYSDATE,
    UPDATE_TIME DATE
);
CREATE UNIQUE INDEX UX_SMES_EQP_CODE ON SMES_EQUIPMENT (EQP_CODE);

-- ------------------------------------------------------------
-- 7. 报废记录
-- ------------------------------------------------------------
CREATE TABLE SMES_SCRAP_RECORD (
    ID           VARCHAR2(64) PRIMARY KEY,
    ORDER_NO     VARCHAR2(64) NOT NULL,
    PRODUCT_CODE VARCHAR2(64),
    QTY          NUMBER(18,4) DEFAULT 0,            -- 报废数量
    DEFECT_CODE  VARCHAR2(64),                      -- 缺陷代码
    REASON       VARCHAR2(500),                     -- 报废原因
    OPERATOR     VARCHAR2(64),
    SCRAP_TIME   DATE         DEFAULT SYSDATE,
    CREATE_TIME  DATE         DEFAULT SYSDATE
);
CREATE INDEX IX_SMES_SCRAP_ORDER ON SMES_SCRAP_RECORD (ORDER_NO);

-- ------------------------------------------------------------
-- 8. 返工记录
-- ------------------------------------------------------------
CREATE TABLE SMES_REWORK_RECORD (
    ID           VARCHAR2(64) PRIMARY KEY,
    ORDER_NO     VARCHAR2(64) NOT NULL,
    PRODUCT_CODE VARCHAR2(64),
    QTY          NUMBER(18,4) DEFAULT 0,            -- 返工数量
    REASON       VARCHAR2(500),                     -- 返工原因
    STATUS       VARCHAR2(20) DEFAULT 'PENDING',    -- PENDING待返工/DOING返工中/DONE已完成
    OPERATOR     VARCHAR2(64),
    REWORK_DATE  DATE         DEFAULT SYSDATE,
    CREATE_TIME  DATE         DEFAULT SYSDATE,
    UPDATE_TIME  DATE
);
CREATE INDEX IX_SMES_REWORK_ORDER ON SMES_REWORK_RECORD (ORDER_NO);

-- 执行完毕退出 sqlplus
exit;
