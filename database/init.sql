-- ========================================
-- 学习任务规划与执行系统 - SQL Server 数据库初始化脚本
-- ========================================
-- 1. 创建数据库
IF NOT EXISTS (
    SELECT *
    FROM sys.databases
    WHERE name = 'learning_planner'
) BEGIN CREATE DATABASE learning_planner;
END
GO USE learning_planner;
GO -- ========================================
    -- 2. 删除已存在的表（按依赖顺序删除）
    -- ========================================
    IF OBJECT_ID('dbo.task_log', 'U') IS NOT NULL DROP TABLE dbo.task_log;
IF OBJECT_ID('dbo.task', 'U') IS NOT NULL DROP TABLE dbo.task;
IF OBJECT_ID('dbo.stage', 'U') IS NOT NULL DROP TABLE dbo.stage;
IF OBJECT_ID('dbo.study_record', 'U') IS NOT NULL DROP TABLE dbo.study_record;
IF OBJECT_ID('dbo.memo', 'U') IS NOT NULL DROP TABLE dbo.memo;
IF OBJECT_ID('dbo.[plan]', 'U') IS NOT NULL DROP TABLE dbo.[plan];
IF OBJECT_ID('dbo.[user]', 'U') IS NOT NULL DROP TABLE dbo.[user];
GO -- ========================================
    -- 3. 用户表
    -- ========================================
    CREATE TABLE [user] (
        id BIGINT IDENTITY(1, 1) PRIMARY KEY,
        username NVARCHAR(50) NOT NULL,
        password NVARCHAR(255) NOT NULL,
        nickname NVARCHAR(50) NULL,
        email NVARCHAR(100) NULL,
        avatar NVARCHAR(255) NULL,
        status TINYINT NOT NULL DEFAULT 1,
        -- 0-禁用,1-启用
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT uk_username UNIQUE (username)
    );
GO -- 插入默认用户
INSERT INTO [user] (username, password, nickname)
VALUES ('admin', '123456', N'默认用户');
GO -- ========================================
    -- 4. 学习方向表（计划表）修改为 [plan]
    -- ========================================
    CREATE TABLE [plan] (
        id BIGINT IDENTITY(1, 1) PRIMARY KEY,
        user_id BIGINT NOT NULL DEFAULT 1,
        plan_name NVARCHAR(100) NOT NULL,
        description NVARCHAR(500) NULL,
        priority TINYINT NOT NULL DEFAULT 3,
        -- 1-5,5最高
        color NVARCHAR(20) DEFAULT '#409EFF',
        status TINYINT NOT NULL DEFAULT 1,
        -- 0-停用,1-启用
        sort_order INT NOT NULL DEFAULT 0,
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE()
    );
GO -- 索引
    CREATE INDEX idx_plan_user_id ON [plan](user_id);
CREATE INDEX idx_plan_status ON [plan](status);
GO -- 插入示例学习方向
INSERT INTO [plan] (plan_name, description, priority, color)
VALUES (N'考研备考', N'考研数学、英语、政治、专业课复习', 5, '#F56C6C'),
    (N'教资备考', N'教师资格证笔试与面试准备', 4, '#E6A23C'),
    (N'蓝桥杯', N'蓝桥杯算法竞赛准备', 4, '#409EFF'),
    (N'项目学习', N'实战项目开发与技术栈学习', 3, '#67C23A'),
    (N'简历优化', N'简历内容完善与面试准备', 3, '#909399');
GO -- ========================================
    -- 5. 阶段目标表
    -- ========================================
    CREATE TABLE stage (
        id BIGINT IDENTITY(1, 1) PRIMARY KEY,
        plan_id BIGINT NOT NULL,
        stage_name NVARCHAR(100) NOT NULL,
        description NVARCHAR(500) NULL,
        start_date DATE NULL,
        end_date DATE NULL,
        target NVARCHAR(500) NULL,
        status TINYINT NOT NULL DEFAULT 0,
        -- 0-未开始,1-进行中,2-已完成
        progress INT NOT NULL DEFAULT 0,
        -- 完成进度 0-100
        sort_order INT NOT NULL DEFAULT 0,
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE()
    );
GO -- 索引
    CREATE INDEX idx_stage_plan_id ON stage(plan_id);
CREATE INDEX idx_stage_status ON stage(status);
GO -- 插入示例阶段
INSERT INTO stage (
        plan_id,
        stage_name,
        description,
        start_date,
        end_date,
        target,
        status
    )
VALUES (
        1,
        N'数学第一轮复习',
        N'高数、线代、概率论基础知识梳理',
        '2026-01-01',
        '2026-03-31',
        N'完成所有基础知识点学习',
        1
    ),
    (
        1,
        N'数学第二轮复习',
        N'重难点强化与真题练习',
        '2026-04-01',
        '2026-06-30',
        N'掌握重点题型解题方法',
        0
    ),
    (
        3,
        N'基础算法阶段',
        N'排序、搜索、动态规划基础',
        '2026-01-01',
        '2026-02-28',
        N'掌握基础算法模板',
        1
    ),
    (
        3,
        N'进阶算法阶段',
        N'图论、数论、高级数据结构',
        '2026-03-01',
        '2026-04-15',
        N'能够解决中等难度题目',
        0
    );
GO -- ========================================
    -- 6. 任务表
    -- ========================================
    CREATE TABLE task (
        id BIGINT IDENTITY(1, 1) PRIMARY KEY,
        task_name NVARCHAR(200) NOT NULL,
        plan_id BIGINT NOT NULL,
        stage_id BIGINT NULL,
        task_type NVARCHAR(20) NOT NULL DEFAULT 'STUDY',
        -- STUDY,PRACTICE,REVIEW,MOCK
        priority TINYINT NOT NULL DEFAULT 3,
        estimated_time INT NULL,
        deadline DATE NULL,
        status NVARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
        -- NOT_STARTED,IN_PROGRESS,COMPLETED,DELAYED
        actual_time INT NULL,
        remark NVARCHAR(500) NULL,
        scheduled_date DATE NULL,
        completed_time DATETIME2 NULL,
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE()
    );
GO -- 索引
    CREATE INDEX idx_task_plan_id ON task(plan_id);
CREATE INDEX idx_task_stage_id ON task(stage_id);
CREATE INDEX idx_task_status ON task(status);
CREATE INDEX idx_task_deadline ON task(deadline);
CREATE INDEX idx_task_scheduled_date ON task(scheduled_date);
GO -- 插入示例任务
INSERT INTO task (
        task_name,
        plan_id,
        stage_id,
        task_type,
        priority,
        estimated_time,
        deadline,
        scheduled_date,
        status
    )
VALUES (
        N'高数第一章：函数与极限',
        1,
        1,
        'STUDY',
        5,
        120,
        '2026-01-25',
        '2026-01-21',
        'IN_PROGRESS'
    ),
    (
        N'线性代数：行列式计算',
        1,
        1,
        'STUDY',
        4,
        90,
        '2026-01-26',
        '2026-01-21',
        'NOT_STARTED'
    ),
    (
        N'二分查找算法练习',
        3,
        3,
        'PRACTICE',
        4,
        60,
        '2026-01-22',
        '2026-01-21',
        'NOT_STARTED'
    ),
    (
        N'动态规划入门',
        3,
        3,
        'STUDY',
        5,
        120,
        '2026-01-28',
        '2026-01-22',
        'NOT_STARTED'
    ),
    (
        N'教资科目一真题练习',
        2,
        NULL,
        'PRACTICE',
        3,
        90,
        '2026-01-30',
        '2026-01-23',
        'NOT_STARTED'
    );
GO -- ========================================
    -- 7. 任务状态流转记录表
    -- ========================================
    CREATE TABLE task_log (
        id BIGINT IDENTITY(1, 1) PRIMARY KEY,
        task_id BIGINT NOT NULL,
        from_status NVARCHAR(20) NULL,
        to_status NVARCHAR(20) NOT NULL,
        remark NVARCHAR(500) NULL,
        operation_time DATETIME2 NOT NULL DEFAULT GETDATE()
    );
GO CREATE INDEX idx_task_log_task_id ON task_log(task_id);
CREATE INDEX idx_task_log_operation_time ON task_log(operation_time);
GO -- ========================================
    -- 8. 学习记录表
    -- ========================================
    CREATE TABLE study_record (
        id BIGINT IDENTITY(1, 1) PRIMARY KEY,
        user_id BIGINT NOT NULL DEFAULT 1,
        record_date DATE NOT NULL,
        total_tasks INT NOT NULL DEFAULT 0,
        completed_tasks INT NOT NULL DEFAULT 0,
        total_time INT NOT NULL DEFAULT 0,
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT uk_user_date UNIQUE (user_id, record_date)
    );
GO CREATE INDEX idx_study_record_date ON study_record(record_date);
GO -- ========================================
    -- 9. 备忘录表
    -- ========================================
    CREATE TABLE memo (
        id BIGINT IDENTITY(1, 1) PRIMARY KEY,
        user_id BIGINT NOT NULL DEFAULT 1,
        title NVARCHAR(100) NOT NULL,
        content NVARCHAR(4000) NULL,
        color NVARCHAR(20) NULL,
        pinned TINYINT NOT NULL DEFAULT 0,
        create_time DATETIME2 NOT NULL DEFAULT GETDATE(),
        update_time DATETIME2 NOT NULL DEFAULT GETDATE()
    );
GO CREATE INDEX idx_memo_user_id ON memo(user_id);
CREATE INDEX idx_memo_pinned ON memo(pinned);
GO -- 插入示例备忘录
INSERT INTO memo (title, content, color, pinned)
VALUES (
        N'每日复盘模板',
        N'1. 今日完成\n2. 遇到问题\n3. 明日计划',
        '#FFF7D1',
        1
    ),
    (N'错题记录', N'整理今天的错题与解法思路。', '#E8F4FF', 0);
GO PRINT N'数据库初始化完成！';
GO