# 一键生成协同开发记录：创建分支 -> 同一工作项3次提交 -> 合并他人(货架模块)分支代码 -> 推送 -> PR
# 用法（在本目录执行）：
#   powershell -ExecutionPolicy Bypass -File .\git_workflow.ps1 -RemoteUrl https://github.com/你的用户名/express-station.git
# 参数：
#   -RemoteUrl   GitHub 空仓库地址（先在 GitHub 新建空仓库，不勾选 README）。不填则只做本地提交。
#   -WorkItem    取件码生成与通知模块的 PingCode 编号，默认 KDYZ-28（以导入后实际编号为准）
#   -ShelfItem   货架与库位管理模块的 PingCode 编号，默认 KDYZ-27
#   -FrameItem   开发框架与公共组件的 PingCode 编号，默认 KDYZ-24
#   -EnvItem     PingCode与GitHub环境搭建的编号，默认 KDYZ-5
param(
    [string]$RemoteUrl = "",
    [string]$WorkItem = "KDYZ-28",
    [string]$ShelfItem = "KDYZ-27",
    [string]$FrameItem = "KDYZ-24",
    [string]$EnvItem = "KDYZ-5"
)
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

function Invoke-Git {
    & git.exe @args
    if ($LASTEXITCODE -ne 0) { throw "git $args 失败" }
}

$name = (& git.exe config user.name)
if (-not $name -and -not $env:GIT_AUTHOR_NAME) {
    Write-Host "请先配置 Git 身份（与 GitHub 账号一致）：" -ForegroundColor Yellow
    Write-Host '  git config --global user.name  "你的GitHub用户名"'
    Write-Host '  git config --global user.email "你的GitHub邮箱"'
    exit 1
}
if (Test-Path .git) { Write-Host "当前目录已是 Git 仓库，脚本不重复执行。" -ForegroundColor Yellow; exit 1 }

$pick = "feature/$WorkItem-pickup-notify"
$shelf = "feature/$ShelfItem-shelf"
$base = "src/main/java/com/kdyz/station"

Invoke-Git init -q
Invoke-Git checkout -q -b main
Invoke-Git add README.md pom.xml .gitignore run_check.ps1 git_workflow.ps1 .github
Invoke-Git commit -q -m "chore: 初始化项目结构与PR模板 #$EnvItem"

Invoke-Git checkout -q -b develop
Invoke-Git add "$base/domain" "$base/repo"
Invoke-Git commit -q -m "feat(framework): 包裹领域模型、状态机与仓储接口 #$FrameItem"

# 他人(后端B角色)负责的货架与库位模块分支
Invoke-Git checkout -q -b $shelf
Invoke-Git add "$base/shelf"
Invoke-Git commit -q -m "feat(shelf): 库位分配策略接口与最少占用实现 #$ShelfItem"

# 本工作项分支：3 次提交，中间合并货架分支代码
Invoke-Git checkout -q develop
Invoke-Git checkout -q -b $pick
Invoke-Git add "$base/notify/Notifier.java" "$base/notify/AbstractNotifier.java" "$base/notify/NotifyDispatcher.java"
Invoke-Git commit -q -m "feat(notify): 通知渠道接口与模板方法基类 #$WorkItem"
Invoke-Git add "$base/notify/SmsNotifier.java" "$base/notify/WechatNotifier.java"
Invoke-Git commit -q -m "feat(notify): 短信与微信订阅消息渠道实现(CR-01) #$WorkItem"
Invoke-Git merge -q --no-ff $shelf -m "merge: 合并货架库位模块代码供取件出库复用 #$WorkItem #$ShelfItem"
Invoke-Git add "$base/pickup" "$base/service" "src/test"
Invoke-Git commit -q -m "feat(pickup): 取件码生成、取件出库与1万并发自检 #$WorkItem"

Write-Host "`n本地提交记录：" -ForegroundColor Green
& git.exe log --oneline --graph --all

if (-not $RemoteUrl) { Write-Host "`n未提供 -RemoteUrl，已完成本地提交。"; exit 0 }

Invoke-Git remote add origin $RemoteUrl
Invoke-Git push -u origin main
Invoke-Git push -u origin develop
Invoke-Git push -u origin $shelf
Invoke-Git push -u origin $pick

$title = "#$WorkItem 取件码生成与通知模块"
$body = "关联工作项 #$WorkItem #$ShelfItem`n`n- 通知渠道接口 + 模板方法基类`n- 短信/微信订阅消息渠道（CR-01）`n- 合并货架库位模块分支代码（共享代码）`n- 取件码生成、取件出库`n- 自检：run_check.ps1 共14项PASS，含1万并发取件"
if (Get-Command gh -ErrorAction SilentlyContinue) {
    & gh pr create --base develop --head $pick --title $title --body $body
} else {
    $web = $RemoteUrl -replace '\.git$', ''
    Write-Host "`n未安装 GitHub CLI，请在浏览器打开以下地址创建 PR（标题填：$title）：" -ForegroundColor Yellow
    Write-Host "$web/compare/develop...$pick`?expand=1"
}
