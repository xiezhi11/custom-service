# 售后工单处理系统

## 项目简介

售后工单处理系统是一个用于演示售后工单从提交到处理完成基本流程的中小型管理系统。系统支持客户、客服、处理人三类角色，实现了工单创建、分派、接单、处理、确认、退回、关闭等完整流程。

## 技术栈

- **后端**: Spring Boot 2.7.x + Spring Data JPA + H2 Database
- **前端**: Vue 3 + Vite + Element Plus
- **数据库**: H2（内存数据库，数据持久化到内存中）

## 核心流程

### 正常流程
1. **客户** 创建工单 → 状态：待分派
2. **客服** 分派处理人 → 状态：已分派
3. **处理人** 接单 → 状态：处理中
4. **处理人** 提交处理结果 → 状态：待确认
5. **客户** 确认完成 → 状态：已完成

### 退回流程
1. **客户** 创建工单 → 状态：待分派
2. **客服** 分派处理人 → 状态：已分派
3. **处理人** 接单 → 状态：处理中
4. **处理人** 提交处理结果 → 状态：待确认
5. **客户** 退回（填写退回原因） → 状态：已退回
6. **处理人** 重新提交处理结果 → 状态：待确认
7. **客户** 确认完成 → 状态：已完成

### 关闭工单
- 客服可以关闭待分派、已分派、处理中、已退回状态的工单
- 已完成和已关闭工单不能继续操作

## 主要目录结构

```
custom-service/
├── backend/                          # 后端项目
│   ├── src/main/java/com/example/workorder/
│   │   ├── WorkorderApplication.java    # 启动类
│   ├── config/                       # 配置类
│   │   ├── CorsConfig.java           # 跨域配置
│   │   └── DataInitializer.java      # 数据初始化
│   ├── controller/                   # 控制层
│   │   └── WorkOrderController.java  # 工单接口
│   ├── dto/                          # 数据传输对象
│   │   ├── CreateWorkOrderDTO.java
│   │   ├── AssignWorkOrderDTO.java
│   │   ├── SubmitResultDTO.java
│   │   ├── ReturnWorkOrderDTO.java
│   │   └── CloseWorkOrderDTO.java
│   ├── entity/                       # 实体类
│   │   ├── WorkOrder.java            # 工单实体
│   │   └── OperationLog.java         # 操作记录实体
│   ├── enums/                        # 枚举类
│   │   ├── WorkOrderStatus.java      # 工单状态
│   │   ├── ProblemType.java          # 问题类型
│   │   ├── Priority.java             # 优先级
│   │   └── RoleType.java             # 角色类型
│   ├── exception/                    # 异常处理
│   │   └── GlobalExceptionHandler.java
│   ├── repository/                   # 数据访问层
│   │   ├── WorkOrderRepository.java
│   │   └── OperationLogRepository.java
│   ├── service/                      # 业务逻辑层
│   │   ├── WorkOrderService.java
│   │   └── impl/
│   │       └── WorkOrderServiceImpl.java
│   └── resources/
│       └── application.yml           # 应用配置
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── api/                      # API 调用
│   │   │   ├── axios-config.js       # Axios 配置
│   │   │   └── workOrderApi.js       # 工单 API
│   │   ├── components/               # 组件
│   │   │   ├── UserSelector.vue      # 角色/用户选择器
│   │   │   ├── StatisticsPanel.vue   # 统计面板
│   │   │   ├── SearchForm.vue        # 搜索表单
│   │   │   ├── WorkOrderList.vue     # 工单列表
│   │   │   ├── WorkOrderDetailDialog.vue    # 工单详情弹窗
│   │   │   ├── CreateWorkOrderDialog.vue    # 创建工单弹窗
│   │   │   ├── AssignDialog.vue      # 分派弹窗
│   │   │   ├── SubmitResultDialog.vue# 提交结果弹窗
│   │   │   ├── ReturnDialog.vue      # 退回弹窗
│   │   │   └── CloseDialog.vue       # 关闭弹窗
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## 关键代码模块

### 后端核心模块

1. **WorkOrderServiceImpl** - 工单业务逻辑核心
   - 工单状态流转控制
   - 权限校验（客户只能看自己的工单，处理人只能看分派给自己的工单）
   - 操作记录自动记录

2. **WorkOrderController** - REST API 接口
   - 提供工单的 CRUD 和状态变更接口
   - 统一的参数校验

3. **DataInitializer** - 数据初始化
   - 系统启动时自动创建测试数据
   - 预置 7 张不同状态的工单用于演示

### 前端核心模块

1. **App.vue** - 主应用组件
   - 角色和用户状态管理
   - 工单数据加载和刷新
   - 全局事件处理

2. **WorkOrderList.vue** - 工单列表
   - 根据角色显示对应操作按钮
   - 状态标签显示

3. **WorkOrderDetailDialog.vue** - 工单详情
   - 显示工单完整信息
   - 时间线展示处理记录

## 后端启动命令

```bash
cd backend

# 方式一：使用 Maven 启动
mvn spring-boot:run

# 方式二：先打包再启动
mvn clean package
java -jar target/workorder-system-1.0.0.jar
```

## 前端启动命令

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

## 访问地址

| 项目 | 地址 |
|------|------|
| 前端应用 | http://localhost:3000 |
| 后端接口 | http://localhost:8080/api |
| H2 控制台 | http://localhost:8080/api/h2-console |

### H2 控制台配置
- JDBC URL: `jdbc:h2:mem:workorder`
- 用户名: `sa`
- 密码: （空）

## 接口地址

### 工单相关接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/workorders` | 创建工单 |
| GET | `/api/workorders` | 查询工单列表 |
| GET | `/api/workorders/{id}` | 获取工单详情 |
| PUT | `/api/workorders/{id}/assign` | 分派工单 |
| PUT | `/api/workorders/{id}/accept` | 接单 |
| PUT | `/api/workorders/{id}/submit-result` | 提交处理结果 |
| PUT | `/api/workorders/{id}/confirm` | 确认完成 |
| PUT | `/api/workorders/{id}/return` | 退回工单 |
| PUT | `/api/workorders/{id}/close` | 关闭工单 |
| GET | `/api/workorders/{id}/logs` | 获取操作记录 |
| GET | `/api/workorders/statistics` | 获取统计数据 |
| GET | `/api/workorders/enums` | 获取枚举值 |

## 如何切换或模拟角色

系统通过页面顶部的选择器来切换角色和用户，无需登录。

### 预置测试账号

| 角色 | 可选用户 |
|------|---------|
| 客户 | 张三、李四、王五、赵六、钱七、孙八、周九 |
| 客服 | 客服1、客服2 |
| 处理人 | 处理人A、处理人B、处理人C |

### 操作说明

1. **选择角色**: 在页面右上角第一个下拉框选择角色（客户/客服/处理人）
2. **选择用户**: 在第二个下拉框选择对应用户
3. **切换角色**: 直接重新选择角色即可，系统会自动刷新数据

### 权限说明

- **客户**: 只能查看和操作自己创建的工单，可以创建工单、确认完成、退回
- **处理人**: 只能查看和操作分派给自己的工单，可以接单、提交处理结果
- **客服**: 可以查看全部工单，可以分派处理人、关闭无效工单

## 工单字段说明

| 字段 | 说明 |
|------|------|
| 工单编号 | 系统自动生成，格式：WO + 日期 + 序号，如 WO202605180001 |
| 客户名称 | 创建工单的客户姓名 |
| 联系方式 | 客户的联系电话 |
| 问题类型 | 设备故障、系统异常、其他 |
| 问题描述 | 对问题的详细描述 |
| 优先级 | 高、中、低 |
| 处理人 | 负责处理该工单的人员 |
| 处理状态 | 待分派、已分派、处理中、待确认、已完成、已退回、已关闭 |
| 处理结果 | 处理人填写的处理方案 |
| 创建时间 | 工单创建的时间 |
| 分派时间 | 客服分派处理人的时间 |
| 接单时间 | 处理人接单的时间 |
| 完成时间 | 客户确认完成的时间 |
| 退回原因 | 客户退回时填写的原因 |
| 关闭原因 | 客服关闭时填写的原因 |

## 演示流程建议

### 流程一：正常处理流程
1. 选择角色"客户"，用户"张三"
2. 创建一张新工单
3. 切换角色为"客服"，用户"客服1"
4. 找到刚创建的工单，点击"分派"，选择处理人"处理人A"
5. 切换角色为"处理人"，用户"处理人A"
6. 找到分派给自己的工单，点击"接单"
7. 点击"提交结果"，填写处理结果
8. 切换回角色"客户"，用户"张三"
9. 找到待确认的工单，点击"确认完成"

### 流程二：退回流程
1. 重复流程一的前 7 步（到处理人提交结果）
2. 切换回角色"客户"，用户"张三"
3. 找到待确认的工单，点击"退回"，填写退回原因
4. 切换角色为"处理人"，用户"处理人A"
5. 找到已退回的工单，点击"提交结果"，重新填写处理结果
6. 切换回角色"客户"，用户"张三"
7. 点击"确认完成"
