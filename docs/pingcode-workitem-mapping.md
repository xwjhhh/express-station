# PingCode 工作项与 GitHub 记录对应表

本文将 PingCode 当前工作项与 GitHub 已有代码记录并列，供项目追踪和验收时查阅。表中“历史引用”保留 GitHub 原记录；它不代表 PingCode 已完成 GitHub 自动集成。

| 功能/记录 | PingCode 实际工作项 | GitHub 历史引用 | GitHub 记录 | 对应说明 |
| --- | --- | --- | --- | --- |
| 货架与库位分配 | KDYZ-27 | `#KDYZ-27` | [货架策略提交](https://github.com/xwjhhh/express-station/commit/8743c24c3318e4949fb87665d0be77a7ee050a69) | 编号与功能记录一致 |
| 取件码生成与通知 | [KDYZ-29](https://ntlj20261101123637087.pingcode.com/pjm/workitems/P9x2YhYV) | `#KDYZ-28` | [三次功能提交](https://github.com/xwjhhh/express-station/commits/feature/KDYZ-28-pickup-notify) | 功能可对应；历史编号不一致，PingCode 实际编号为 KDYZ-29 |
| 取件与通知合并 | [KDYZ-29](https://ntlj20261101123637087.pingcode.com/pjm/workitems/P9x2YhYV) | PR 标题/分支为 `KDYZ-28` | [PR #1，已合并](https://github.com/xwjhhh/express-station/pull/1) | PR 合入 `main`；历史编号保留 |
| 包裹框架与状态模型 | 待按 PingCode 实际工作项核对 | `#KDYZ-24` | [框架提交](https://github.com/xwjhhh/express-station/commit/76efbd6201e555c1277de3d356c0bfbb577461f8) | 仅凭提交编号无法确认对应工作项 |
| 项目初始化结构 | 待按 PingCode 实际工作项核对 | `#KDYZ-5` | [初始化提交](https://github.com/xwjhhh/express-station/commit/b1655c8f) | 仅凭提交编号无法确认对应工作项 |

## 后续提交约定

取件码与通知相关的新分支、提交和 PR 使用 PingCode 当前实际编号 `KDYZ-29`。例如：

```text
feat(pickup): 完成取件通知调整 #KDYZ-29
```

不要为了编号一致而改写已经合并的历史提交。要启用 PingCode 自动显示 GitHub 代码记录，仍需在 PingCode 中完成 GitHub 仓库授权并验证工作项详情的代码关联。

## 核验依据

- PingCode 取件工作项：<https://ntlj20261101123637087.pingcode.com/pjm/workitems/P9x2YhYV>
- GitHub 仓库：<https://github.com/xwjhhh/express-station>
- GitHub PR #1：<https://github.com/xwjhhh/express-station/pull/1>
- 本表依据 2026-11-02 核验的公开提交记录和 PingCode 工作项截图整理。自动集成状态尚未验证。
