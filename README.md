# 社区快递驿站包裹管理系统 · 核心模块（express-station）

软件项目管理综合实验配套代码。项目管理平台：PingCode（项目标识 `KDYZ`）；代码托管：GitHub。

本仓库只实现核心框架与核心代码（抽象类、子类、接口、核心业务），不包含完整前后端。

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

预期输出 14 行 `PASS`，其中一项为“2万次并发取件请求恰好成功 10000 次，无重复出库”。

## 与 PingCode 关联的提交规范

提交信息、分支名、PR 标题中写 `#KDYZ-编号`，PingCode 会把提交、分支、PR 自动挂到对应工作项的“代码”页签：

```
feat(notify): 短信与微信订阅消息渠道 #KDYZ-28
```

分支命名：`feature/KDYZ-28-pickup-notify`。

## 协作流程

创建分支 → 开发（同一工作项 3 次提交）→ Pull Request → 代码审查 → 合并到 develop → 发布到 main。

`git_workflow.ps1` 自动完成：main/develop 分支 → 货架模块分支 `feature/KDYZ-27-shelf` 提交 → 工作项分支 `feature/KDYZ-28-pickup-notify` 上 3 次提交（中间合并货架分支代码，即共享他人代码）→ 推送 → 创建 PR。
