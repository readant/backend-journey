# Cursor 与 PowerShell 不兼容问题排查与解决

> 环境：Windows 11 + Cursor。本文沉淀「为什么 PowerShell 老出问题、怎么一次配好」，结论可直接用于日常开发。

## 0. 本机开发环境约束（强制，2026-08 落地）

| 项 | 值 | 说明 |
| ---- | ---- | ---- |
| 全局默认终端 | **PowerShell 7**（`C:\Program Files\PowerShell\7\pwsh.exe -NoLogo`） | Cursor 用户级 `settings.json` 已配置（含显式 profile），**唯一终端，无备选** |
| PowerShell 5.1 | 系统自带，**禁止 `-NoProfile`** | 只有加载 profile 后才是 UTF-8（65001）；`-NoProfile` 会跳过全部修复 |
| PowerShell 7 | `C:\Program Files\PowerShell\7\pwsh.exe`（7.6.4，MSI 完整版） | **默认终端**，`&&`/`||` 原生支持，编码默认 UTF-8 |
| Git Bash | ~~已移除~~（曾配 `D:\APP\Git\bin\bash.exe --login`） | 2026-08 因出错过多从 Cursor 终端配置中**彻底移除**，不再作备选 |
| 真实 Git | `D:\APP\Git`（2.55.0） | PATH 中 `D:\Program Files\Git\cmd` 残留已移除 |
| 代理 | `http://127.0.0.1:18080` | GitHub 大文件经代理下载不稳定，参考第 7 节 |

**命令生成 8 条强制规则**：

1. 优先生成 PowerShell 7 写法（默认终端就是 pwsh，支持 `&&`/`||`、UTF-8 默认）
2. 命令分行执行，不大量使用 `&&`/`||`；确需串行仅限 pwsh 内使用（PS5.1 不识别）
3. 需要 PowerShell 能力时显式调用 `pwsh.exe`，不调用 `powershell.exe`
4. 禁止给 `powershell.exe` 加 `-NoProfile`（会跳过 profile 里的 UTF-8 修复）
5. 下载命令必须写 `curl.exe`（带 `.exe` 强制原生，避开别名劫持）
6. 脚本文件必须 UTF-8 编码，禁止 CRLF 行尾
7. Git 相关操作在 pwsh 内直接调 `git`，不引入其他 shell（避免跨 shell 交替的路径解析问题）
8. 文件路径用 Windows 格式（`C:\xxx`），不生成 WSL/mnt 路径

**profile 实际位置（重点坑）**：本机 Documents 被重定向到 E 盘，profile 真实路径是 `E:\Users\lu\Documents\WindowsPowerShell\Microsoft.PowerShell_profile.ps1`（5.1）与 `E:\Users\lu\Documents\PowerShell\Microsoft.PowerShell_profile.ps1`（7）。写 profile 前先 `echo $PROFILE` 确认真实路径，不要按 `C:\Users\lu\Documents\` 猜。

## 1. 问题现象清单（对号入座）

| 症状 | 典型报错 | 根因 |
| ---- | ---- | ---- |
| 命令里带 `&` 就报错 | `"&" 是保留字符` / `表达式或语句中包含意外的标记"&"` | bash 的 `&&` 在 PS 5.1 不合法 |
| 中文全变乱码 | `����λ��`、`ä¸æ`、问号 | PS 5.1 输出代码页是 936（GBK），Cursor 按 UTF-8 显示 |
| 脚本无法运行 | `无法加载文件 xxx.ps1，因为在此系统上禁止运行脚本` | 执行策略不是 RemoteSigned |
| curl 参数报错 | `Invoke-WebRequest : 找不到接受实际参数"-s"的位置形式参数` | `curl` 在 PS 里是 `Invoke-WebRequest` 的别名 |
| 环境变量写法报错 | `export` 不是可识别的命令 | bash 语法（`export FOO=bar`）与 PS 语法（`$env:FOO="bar"`）不同 |
| 换行续行报错 | 反引号 `` ` `` 不是 bash 的 `\` | 续行符语法不同 |

## 2. 根因：Windows PowerShell 5.1 是「老 bash 思维」的天敌

Windows 11 自带的是 **Windows PowerShell 5.1**（不是 PowerShell 7），它同时踩了三个坑：

1. **语法不兼容**：5.1 不支持 `&&`/`||`（PowerShell 7 才支持）；环境变量、引号、续行、通配符语义全部和 bash 不同。Cursor 的教程、文档、AI 生成的命令基本都是 bash 写法，粘贴进 PS 5.1 必然报错。
2. **编码不兼容**：5.1 默认输出编码是系统 ANSI（中文系统 = 936/GBK），而 Cursor 终端和现代工具默认 UTF-8，中文输出必乱。
3. **别名劫持**：PS 把 `curl`/`wget` 指向 `Invoke-WebRequest`，`ls`/`cat` 等也有自己的语义，抄网上的命令直接变形。

一句话：**PS 5.1 是给 Windows 管理员用的，不是给开发者跑命令用的**。在 Cursor 里应当避开它。

## 3. 解决方案（按推荐优先级）

### 方案 A（首选 ✅ 已落地）：默认终端切到 PowerShell 7

PS 7 支持 `&&`/`||`、原生 `curl.exe` 透传，编码默认 UTF-8，语法比 PS 5.1 现代，是 Cursor 里的日常终端。

1. **Cursor 用户级设置**（全局生效，所有项目共享）——编辑 `C:\Users\lu\AppData\Roaming\Cursor\User\settings.json`：

```json
{
  "terminal.integrated.defaultProfile.windows": "PowerShell",
  "terminal.integrated.profiles.windows": {
    "PowerShell": {
      "path": "C:\\Program Files\\PowerShell\\7\\pwsh.exe",
      "args": ["-NoLogo"],
      "icon": "terminal-powershell"
    }
  }
}
```

> ⚠️ **历史坑（2026-08）**：`git-bash.exe` 是窗口启动器，总会弹出独立窗口且不接集成终端管道，命令会被「吞掉」。Git Bash 已整体移出 Cursor 终端配置，**不再提供备选 profile**。

2. 若 `Ctrl+Shift+\`` 新建终端不是 PowerShell，点终端面板右上角下拉箭头 → 选择 profile → 选「PowerShell」；或执行命令面板（`Ctrl+Shift+P`）→「终端: 选择默认配置文件」→ 选 PowerShell。
3. 验证：新开终端提示符是 `PS C:\>`，执行 `$PSVersionTable.PSVersion` 输出 7.6.4。

> ✅ **本机已落地（2026-08）**：配置在 Cursor **用户级** settings.json，全局生效，单一配置源；项目级 `.vscode/settings.json` 不再重复配置终端。PowerShell 7 为**唯一终端**，Git Bash 已彻底移除，无备选。

### 方案 B（必须）：修好 PS 5.1 的编码与执行策略（如果还要用 PS）

```powershell
# 1. 允许本地脚本运行（一劳永逸）
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# 2. 当前会话切 UTF-8 输出（每次新开终端都要执行，或写入 $PROFILE）
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 > $null
```

持久化：把第 2 条写入配置文件，新终端自动生效：

```powershell
# 查看当前 profile 真实路径（Documents 可能被重定向，不要按 C:\Users\... 猜）
echo $PROFILE
# 用记事本编辑，粘贴上述 UTF-8 两行后保存
notepad $PROFILE
```

> ⚠️ **坑**：本机 Documents 重定向到 `E:\Users\lu\Documents\`，profile 若写在 `C:\Users\lu\Documents\` 下不会自动加载（当时只手动点源验证过，自动加载验证是 False）。写完后必须**新开终端**验证 `[Console]::OutputEncoding.CodePage` 输出 65001。

### 方案 C（命令写法规避）：在 PS 里抄命令时的翻译口诀

| bash 写法 | PS 5.1 等价写法 |
| ---- | ---- |
| `cmd1 && cmd2` | `cmd1; if ($?) { cmd2 }`（或直接换行分步执行） |
| `export FOO=bar` | `$env:FOO = "bar"` |
| `FOO=bar mvn test`（内联环境变量） | `$env:FOO="bar"; mvn test` |
| `curl -s -L url` | `curl.exe -s -L url`（带 .exe 强制原生） |
| `cat file` | `Get-Content file`（或 `type file`） |
| 路径 `/c/xxx` | `C:\xxx` |

## 4. 排查速查（一条命令定位根源）

```powershell
# 版本：5.1 = 自带老版（问题高发），7.x = 新版
$PSVersionTable.PSVersion

# 编码：936 = GBK（中文必乱码），65001 = UTF-8（正常）
[Console]::OutputEncoding.CodePage

# 执行策略：必须至少 CurrentUser 是 RemoteSigned
Get-ExecutionPolicy -List

# 别名劫持：看到 Alias Invoke-WebRequest 就是被劫持了
Get-Command curl
```

## 5. 本机环境诊断存档（2026-08 落地后）

| 项目 | 值 | 结论 |
| ---- | ---- | ---- |
| Windows | 11（10.0.26100） | — |
| PowerShell 5.1 | 5.1.26100.1，profile 已配 UTF-8 | ✅ 加载 profile 后 65001 |
| PowerShell 7 | **7.6.4（MSI 完整版，`C:\Program Files\PowerShell\7\pwsh.exe`）** | ✅ `&&` 可用，profile 生效 |
| 输出代码页 | 65001（UTF-8，两版 PS profile 自动加载后） | ✅ 中文不乱码 |
| 执行策略 | LocalMachine = RemoteSigned | ✅ 正常 |
| curl 别名 | `curl` → `Invoke-WebRequest`；`curl.exe` → 原生 | 命令里必须写 `curl.exe` |
| Git | 仅 `D:\APP\Git` 2.55.0（PATH 已清理残留） | ✅ 单一 Git |
| 默认终端 | Cursor **用户级**配置 = **PowerShell 7**（`C:\Program Files\PowerShell\7\pwsh.exe -NoLogo`），**唯一终端，无备选** | ✅ 全局生效 |
| profile 位置 | `E:\Users\lu\Documents\{WindowsPowerShell,PowerShell}\`（Documents 重定向到 E 盘） | ⚠️ 按 `$PROFILE` 查，勿按 C 盘猜 |

## 6. 遗留问题

- **AI 终端与手动终端不一致**：Cursor 的 AI 工具执行命令用的是系统默认 shell（本机为 PowerShell 7），与用户手动开的终端可能不是同一个 shell，遇到「AI 能跑、自己敲就报错」先确认终端 profile 是哪个。
- **GitHub 大文件经代理不稳定**：本次安装 PS7 时 winget 直接走通了（110MB 秒下），但 curl 经代理下载 GitHub 文件多次中断（exit 28/56），代理不支持断点续传。需要时优先 winget/浏览器，其次 `curl.exe -L -x http://127.0.0.1:18080`。

> ✅ **已澄清（2026-08）**：曾怀疑「切换 Git Bash 默认终端导致 Java Run/Debug 按钮失效」，结论是**无关**。Run 按钮走 LSP 通道（不经过 shell），失效根因是 `vscode-java-test 0.46.0` 的 ASM bundle 与 redhat.java 冲突（详见 `java-plugins.md` 第 6 节）。

> ✅ **已更新（2026-08）**：默认终端从 Git Bash 调整为 **PowerShell 7**（`pwsh.exe -NoLogo`，7.6.4，UTF-8 默认），Git Bash **彻底移除、不再作备选**。原因：PS7 支持 `&&`/`||`、无别名劫持（`curl.exe` 原生）、编码默认 UTF-8；Git Bash 与 PS 混用出错过多，统一单一 shell 消除跨 shell 的语法/路径/编码问题。

## 7. 安装 PowerShell 7 实战记录（2026-08）

目标：7.6.4 MSI 完整版（110MB，`115515392` 字节）。

| 路径 | 结果 | 说明 |
| ---- | ---- | ---- |
| winget 默认装 | ❌ 卡下载 | 6 分钟无进度强杀；`--proxy` 需管理员开启设置，无权限 |
| curl 经代理下载 GitHub MSI | ❌ 超时/中断 | exit 28/56；代理不支持断点续传（HTTP=000） |
| gh-proxy.com 镜像分段下载 | ❌ 太慢 | 仅 ~2.4KB/s，20 段需 13 小时 |
| 微软官方 CDN 直链 | ❌ 未找到 | release 元数据中无 CDN URL，资产仅 GitHub 域名 |
| **winget 重试（最终成功）** | ✅ | 网络恢复后 110MB 秒下。**先卸载误装的 MSIX 版，再用 `--installer-type wix` 重装 MSI 版** |

教训：

1. **7.6.0+ winget 默认装 MSIX 沙箱版**：profile 不加载、`-NoLogo` 不可用，必须 `--installer-type wix` 才是完整 MSI。装完必须验证 `Test-Path 'C:\Program Files\PowerShell\7\pwsh.exe'` 为 True。
2. **winget 下载慢≠失败**：之前卡 6 分钟是网络问题，网络恢复后同一命令秒下。下载问题优先重试 winget，不要急着换镜像。
3. **PS7 执行策略与 PS5.1 独立**：PS7 有自己的注册表执行策略键，MSI 安装不继承 PS5.1 设置。本机 LocalMachine=RemoteSigned 恰好满足，若发现 profile 不加载先查 `pwsh.exe -NoLogo -Command 'Get-ExecutionPolicy -List'`。