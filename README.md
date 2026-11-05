# 社区快递驿站包裹管理系统 · 核心模块（express-station）

软件项目管理综合实验配套代码。项目管理平台：PingCode（项目标识 `KDYZ`）；代码托管：GitHub。

本仓库只实现核心框架与核心代码（抽象类、子类、接口、核心业务），不包含完整前后端。

## 项目概览

| 项目项 | 内容 |
| --- | --- |
| 项目 | 社区快递驿站包裹管理系统 |
| 项目管理 | PingCode 项目 `KDYZ`，按阶段管理任务、里程碑和缺陷 |
| 代码仓库 | [xwjhhh/express-station](https://github.com/xwjhhh/express-station) |
| 仓库分支 | `main`（集成）、`develop`（开发）及功能分支 |
| 实验协作方式 | 本人实际操作，按虚拟角色分工记录协作流程 |
| PingCode 项目规模 | 6 个阶段、38 个任务、6 个里程碑、8 个缺陷；核查时 27 项已完成 |

## 核心模块与功能

| 模块 | 主要实现 | 可展示的行为 |
| --- | --- | --- |
| `domain` | 包裹抽象类、普通/生鲜/大件类型、包裹状态、库区模型 | 按包裹类型分配库区，维护包裹状态 |
| `shelf` | 货架分配策略接口、最少占用优先实现 | 根据库位占用情况分配货架 |
| `pickup` | 取件码生成接口、库位编码与随机码、原子去重 | 生成唯一取件码，取件后回收 |
| `notify` | 通知接口、模板方法、短信与微信订阅消息分发 | 已订阅用户走微信，其余用户走短信 |
| `repo` | 仓储接口与内存实现 | 隔离业务服务与数据存储实现 |
| `service` | 入库、取件出库、滞留扫描服务 | 完成核心业务流程与状态更新 |

## PingCode 与 GitHub 演示对应

| 验收内容 | PingCode 工作项 | GitHub 证据 | 展示重点 |
| --- | --- | --- | --- |
| 货架与库位分配 | KDYZ-27 | [货架策略提交](https://github.com/xwjhhh/express-station/commit/8743c24c3318e4949fb87665d0be77a7ee050a69) | 工作项与策略接口、最少占用实现 |
| 取件码、通知与取件出库 | [KDYZ-29](https://ntlj20261101123637087.pingcode.com/pjm/workitems/P9x2YhYV) | [功能提交历史](https://github.com/xwjhhh/express-station/commits/feature/KDYZ-28-pickup-notify) | 依次展示通知接口、短信/微信渠道、取件码与出库逻辑 |
| 功能合并 | [KDYZ-29](https://ntlj20261101123637087.pingcode.com/pjm/workitems/P9x2YhYV) | [PR #1（已合并）](https://github.com/xwjhhh/express-station/pull/1) | 展示 PR、变更文件和合入 `main` 的记录 |
| 工作项映射说明 | KDYZ 项目 | [PingCode 工作项与 GitHub 记录对应表](docs/pingcode-workitem-mapping.md) | 查看历史编号差异及逐项核对依据 |

> 历史取件分支、提交和 PR 使用 `KDYZ-28`，PingCode 中对应的实际取件通知工作项是 `KDYZ-29`。保留历史原文，不将两个编号说成完全一致；映射表用于人工追踪，不代表已验证 PingCode 自动同步。

## 验收入口

| 展示顺序 | 打开位置 | 用途 |
| --- | --- | --- |
| 1 | 本仓库首页 README | 说明项目范围、模块功能、管理平台及代码入口 |
| 2 | [PingCode 项目 KDYZ](https://ntlj20261101123637087.pingcode.com/) | 展示阶段、任务、里程碑、缺陷及工作项字段 |
| 3 | [PR #1](https://github.com/xwjhhh/express-station/pull/1) | 展示功能变更、文件差异和合并状态 |
| 4 | [取件通知提交历史](https://github.com/xwjhhh/express-station/commits/feature/KDYZ-28-pickup-notify) | 展示功能分步实现的三次提交 |
| 5 | [货架策略提交](https://github.com/xwjhhh/express-station/commit/8743c24c3318e4949fb87665d0be77a7ee050a69) | 展示货架模块实现及代码差异 |
| 6 | 本地运行自检脚本 | 展示 14 项功能与并发自检结果 |

## 结构

```
src/main/java/com/kdyz/station
├── domain   Parcel(抽象类) / ParcelTypes(普通、生鲜、大件子类) / ParcelStatus(状态机) / ShelfZone
├── notify   Notifier(接口) / AbstractNotifier(模板方法) / SmsNotifier / WechatNotifier / NotifyDispatcher
├── pickup   PickupCodeGenerator(接口) / SlotPickupCodeGenerator(库位+随机码，原子去重)
├── shelf    ShelfAllocationStrategy(策略接口) / LeastLoadedShelfStrategy(最少占用优先)
├── repo     ParcelRepository(接口) / InMemoryParcelRepository
└── service  StationService(入库、取件出库、滞留扫描)
src/test/java/com/kdyz/station/StationCheck.java   功能用例 + 1万并发取件用例
```

## 运行自检

需要 JDK 17+，不需要 Maven：

```powershell
powershell -ExecutionPolicy Bypass -File .\run_check.ps1
```

预期输出 14 行 `PASS`，覆盖包裹类型与库区分配、取件码格式、通知渠道、异常输入、滞留处理和并发取件。并发用例预置 10000 个包裹，每个取件码同时提交两次请求，共 20000 次请求，预期恰好 10000 次成功且没有重复出库。

## 与 PingCode 关联的提交规范

提交信息、分支名、PR说明应引用平台的实际工作项编号；自动关联还需要正确的集成授权和账号绑定，并在任务中验证。本次未验证自动代码页签。

2026年11月2日核查：GitHub历史取件分支及以下提交使用`KDYZ-28`，PingCode实际取件任务为`KDYZ-29`，存在编号错位。以下为历史消息示例，不作为后续编号依据：

```
feat(notify): 短信与微信订阅消息渠道 #KDYZ-28
```

真实历史分支：`feature/KDYZ-28-pickup-notify`。

## 协作流程

建议流程：创建分支 → 开发（同一工作项3次提交）→ Pull Request → 实际代码审查 → 合并。

实际PR #1已合入main，当前无审查记录。真实仓库：https://github.com/xwjhhh/express-station 。三次功能提交为231a2d9、deb7b7f、21064f7，货架分支合并为e830767。模块合并不能单独证明共享同学代码，仍需真实同学来源和复用说明。

`git_workflow.ps1`为首次建库方案，可创建分支、制作提交、合并模块并推送/创建PR；当前仓库已有历史，不要重跑制造重复记录。脚本默认编号是旧方案，后续必须逐项核对平台实际编号。
