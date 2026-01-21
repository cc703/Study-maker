# 学习任务规划与执行系统 (Learning Task Planner)

## 项目简介

这是一个专为大学生设计的学习任务规划与执行系统，支持多学习方向并行管理、阶段化目标拆解、任务执行与状态跟踪、学习进度统计等功能。采用"墨韵专注"的中国水墨画美学设计，营造沉浸式的学习氛围。

## 技术栈

### 后端

- Java 17
- Spring Boot 3.2.0
- MyBatis 3.0.3
- MySQL 8.0+
- Maven

### 前端

- Vue 3.3
- Vite 5.0
- Element Plus 2.4
- Pinia 2.1
- Vue Router 4.2
- Axios 1.6

### 设计系统

- **墨韵专注**主题：中国水墨画美学
- 字体：思源宋体（Noto Serif SC）+ JetBrains Mono
- 配色：墨蓝 (#1a1d23)、宣纸白 (#f5f2ea)、朱红 (#c45050)、流金 (#d4a855)、苔绿 (#7c8b7d)

## 项目结构

```
learning-planner/
├── backend/                    # 后端项目
│   ├── src/main/java/com/planner/
│   │   ├── common/            # 公共类（Result、PageResult、异常）
│   │   ├── config/            # 配置类（全局异常处理、跨域配置）
│   │   ├── controller/        # 控制器层
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── mapper/            # MyBatis映射接口
│   │   ├── service/           # 服务层接口
│   │   │   └── impl/          # 服务层实现
│   │   ├── vo/                # 视图对象
│   │   └── PlannerApplication.java  # 启动类
│   ├── src/main/resources/
│   │   ├── mapper/            # MyBatis XML映射文件
│   │   └── application.yml    # 配置文件
│   └── pom.xml                # Maven配置
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── router/            # 路由配置（含欢迎页路由）
│   │   ├── store/             # Pinia状态管理
│   │   ├── styles/            # 全局样式（墨韵主题设计系统）
│   │   ├── utils/             # 工具函数
│   │   ├── views/             # 页面组件
│   │   │   ├── welcome/        # 欢迎页（墨韵流淌主题）
│   │   │   ├── dashboard/     # 仪表盘
│   │   │   ├── layout/        # 布局组件（含返回首页入口）
│   │   │   ├── plan/          # 学习方向管理
│   │   │   ├── stage/         # 阶段管理
│   │   │   ├── statistics/    # 统计复盘
│   │   │   ├── task/          # 任务管理
│   │   │   └── memo/          # 备忘录
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
└── database/
    └── init.sql               # 数据库初始化脚本
```

## 环境依赖

### 必需环境

1. **JDK 17+** - [下载地址](https://adoptium.net/)
2. **Maven 3.8+** - [下载地址](https://maven.apache.org/download.cgi)
3. **Node.js 18+** - [下载地址](https://nodejs.org/)
4. **SQL Server 2019+** - [下载地址](https://www.microsoft.com/zh-cn/sql-server/sql-server-downloads)

### 开发工具（推荐）

- IntelliJ IDEA / VS Code
- MySQL Workbench / Navicat

## 快速开始

### 1. 数据库初始化

1. 确保SQL Server服务已启动
2. 使用SQL Server Management Studio (SSMS) 执行初始化脚本：
   - 打开SSMS，连接到你的SQL Server实例
   - 打开 `database/init.sql` 文件
   - 执行脚本（按F5或点击执行按钮）

或者使用sqlcmd命令行：

```bash
sqlcmd -S localhost -U sa -P 你的密码 -i database/init.sql
```

### 2. 后端启动

1. 进入后端目录：

```bash
cd backend
```

2. 修改数据库配置（如需要）：
   编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=learning_planner;encrypt=true;trustServerCertificate=true
    username: sa
    password: 123456  # 修改为你的SQL Server密码
```

3. 编译并启动项目：

```bash
# 使用Maven
mvn clean package -DskipTests
mvn spring-boot:run

# 或直接运行
mvn spring-boot:run
```

4. 后端启动成功后，访问 http://localhost:8080 可以看到项目运行。

### 3. 前端启动

1. 进入前端目录：

```bash
cd frontend
```

2. 安装依赖：

```bash
npm install
```

3. 启动开发服务器：

```bash
npm run dev
```

4. 前端启动成功后，访问 http://localhost:3000

   **首次访问**会自动跳转到欢迎页（http://localhost:3000/welcome）

## 页面说明

### 1. 欢迎页 (`/welcome`)

全新设计的沉浸式欢迎页面，采用"墨韵流淌"主题：

- **流动墨滴背景**：SVG 动态墨滴，营造水墨晕染效果
- **印章装饰**：传统红色印章，增添文化韵味
- **渐入动画**：标题、卡片逐层展现
- **快速入口**：直达仪表盘、任务管理、学习方向、备忘录

### 2. 仪表盘 (`/dashboard`)

今日学习概览：

- 四个统计卡片（今日任务、已完成、进行中、连续学习天数）
- 圆形进度环显示完成率
- 趋势指示器和 streak 徽章
- 今日任务列表（快速开始/完成任务）
- 各学习方向完成率

### 3. 学习方向管理 (`/plan`)

- 新增/编辑/删除学习方向
- 设置优先级（1-5星）
- 设置标识颜色
- 启用/停用状态切换
- 卡片式展示

### 4. 阶段目标管理 (`/stage`)

- 按学习方向筛选阶段
- 设置阶段时间范围
- 跟踪阶段进度
- 设置阶段目标
- 进度条可视化

### 5. 任务管理 (`/task`)

- 多条件筛选任务（状态、优先级、学习方向、日期范围）
- 新增/编辑/删除任务
- 任务状态流转（未开始→进行中→已完成）
- 记录实际用时
- 任务导出（CSV、Excel、周报）

### 6. 统计复盘 (`/statistics`)

- 总体统计数据（学习方向、总任务数、已完成、连续学习天数）
- 各学习方向完成情况
- 本周学习趋势（横向柱状图）
- 超期任务提醒

### 7. 备忘录 (`/memo`)

- 搜索备忘录
- 新增/编辑/删除备忘录
- 颜色标识
- 置顶功能

## API接口文档

### 学习方向 (Plan)

| 方法   | 路径                   | 说明               |
| ------ | ---------------------- | ------------------ |
| GET    | /api/plans             | 获取所有学习方向   |
| GET    | /api/plans/active      | 获取启用的学习方向 |
| GET    | /api/plans/{id}        | 获取学习方向详情   |
| POST   | /api/plans             | 新增学习方向       |
| PUT    | /api/plans             | 更新学习方向       |
| DELETE | /api/plans/{id}        | 删除学习方向       |
| PUT    | /api/plans/{id}/toggle | 切换状态           |

### 阶段 (Stage)

| 方法   | 路径                      | 说明                 |
| ------ | ------------------------- | -------------------- |
| GET    | /api/stages               | 获取所有阶段         |
| GET    | /api/stages/plan/{planId} | 根据学习方向获取阶段 |
| GET    | /api/stages/{id}          | 获取阶段详情         |
| POST   | /api/stages               | 新增阶段             |
| PUT    | /api/stages               | 更新阶段             |
| DELETE | /api/stages/{id}          | 删除阶段             |
| PUT    | /api/stages/{id}/progress | 更新进度             |

### 任务 (Task)

| 方法   | 路径                     | 说明             |
| ------ | ------------------------ | ---------------- |
| GET    | /api/tasks               | 分页查询任务     |
| GET    | /api/tasks/today         | 获取今日任务     |
| GET    | /api/tasks/date/{date}   | 获取指定日期任务 |
| GET    | /api/tasks/overdue       | 获取超期任务     |
| GET    | /api/tasks/{id}          | 获取任务详情     |
| POST   | /api/tasks               | 新增任务         |
| PUT    | /api/tasks               | 更新任务         |
| DELETE | /api/tasks/{id}          | 删除任务         |
| PUT    | /api/tasks/status        | 更新任务状态     |
| PUT    | /api/tasks/{id}/start    | 开始任务         |
| PUT    | /api/tasks/{id}/complete | 完成任务         |

### 统计 (Statistics)

| 方法 | 路径                             | 说明             |
| ---- | -------------------------------- | ---------------- |
| GET  | /api/statistics/dashboard        | 获取仪表盘数据   |
| GET  | /api/statistics/consecutive-days | 获取连续学习天数 |

## 功能说明

### 1. 欢迎页

- 沉浸式水墨美学设计
- 流动墨滴背景动画
- 传统印章装饰
- 快速功能入口
- 根据时间段显示问候语

### 2. 仪表盘

- 今日任务概览（总数、已完成、进行中）
- 连续学习天数统计
- 各学习方向完成率
- 快速开始/完成任务
- 圆形进度环可视化

### 3. 学习方向管理

- 新增/编辑/删除学习方向
- 设置优先级（1-5星）
- 设置标识颜色
- 启用/停用状态切换

### 4. 阶段目标管理

- 按学习方向筛选阶段
- 设置阶段时间范围
- 跟踪阶段进度
- 设置阶段目标

### 5. 任务管理

- 多条件筛选任务
- 新增/编辑/删除任务
- 任务状态流转（未开始→进行中→已完成）
- 记录实际用时
- 分页展示

### 6. 统计与复盘

- 总体统计数据
- 各学习方向完成情况
- 本周学习趋势图（横向柱状图）
- 超期任务提醒

## 常见问题

### Q1: 数据库连接失败

**解决方案：**

1. 确保SQL Server服务已启动
2. 检查SQL Server是否启用了TCP/IP协议（使用SQL Server配置管理器）
3. 确保1433端口已开放
4. 检查用户名和密码是否正确
5. 确保已创建 `learning_planner` 数据库

### Q2: 后端启动报错找不到类

**解决方案：**

```bash
mvn clean install -DskipTests
```

### Q3: 前端启动报错

**解决方案：**

1. 确保Node.js版本 >= 18
2. 删除 `node_modules` 目录，重新执行 `npm install`

### Q4: 前端请求后端接口报跨域错误

**解决方案：**

- 开发环境下，Vite已配置代理，请确保后端运行在8080端口
- 生产环境需要配置Nginx反向代理

### Q5: 任务状态无法更新

**解决方案：**

- 检查任务状态流转逻辑：只有"未开始"状态的任务才能"开始"

### Q6: 访问欢迎页

**解决方案：**

- 直接访问 http://localhost:3000/welcome
- 或点击侧边栏底部的"返回首页"按钮

## 后续规划

- [ ] AI 自动学习计划生成
- [ ] 学习效率分析
- [ ] 蓝桥杯刷题专项模块
- [ ] 微信小程序版本
- [ ] 番茄钟计时功能
- [ ] 数据导出功能

## 联系方式

如有问题或建议，欢迎提交Issue或PR。

---

**墨韵学习 · 专注成长 · 每日精进**
