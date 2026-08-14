# Cursor Java 插件检查与安装

> 环境：Windows + Cursor。结论可直接用于日常开发环境的插件评估。

## 1. 环境基础（先确认这两项）

| 项目  | 检查命令        | 合格标准                             |
| ----- | --------------- | ------------------------------------ |
| JDK   | `java -version` | 17（本项目技术栈要求）               |
| Maven | `mvn -version`  | 3.9.x，且 `Java version` 指向 JDK 17 |

```powershell
# JAVA_HOME 示例
D:\APP\Java\jdk-17-temurin
```

## 2. Java 插件清单（共 7 个）

基础三件套 + 两个关键插件 + 两个推荐插件，覆盖「编码 → 构建 → 测试 → 调试 → 运行」全链路。

### 2.1 基础三件套

| 插件 ID                          | 作用                                                            | 缺失影响   |
| -------------------------------- | --------------------------------------------------------------- | ---------- |
| `redhat.java`                    | Java 语言服务器：语法、智能提示、编译诊断；**内置 Lombok 支持** | 无法编码   |
| `vscjava.vscode-java-debug`      | Java 调试器（断点/变量/调用栈）                                 | 无法调试   |
| `vscjava.vscode-java-dependency` | 项目依赖树 / 包视图                                             | 无依赖浏览 |

### 2.2 关键插件（高频操作必备）

| 插件 ID                    | 作用                                                 | 缺失影响                            |
| -------------------------- | ---------------------------------------------------- | ----------------------------------- |
| `vscjava.vscode-java-test` | **JUnit 5 测试运行器**：测试文件旁运行按钮、测试报告 | 无法在 IDE 内跑单元测试，只能命令行 |
| `vscjava.vscode-maven`     | Maven 项目管理：侧边栏视图、刷新依赖、执行生命周期   | pom 变更后无法在 IDE 内操作         |

### 2.3 推荐插件（Spring Boot 开发体验）

| 插件 ID                                | 作用                                                                 | 缺失影响                     |
| -------------------------------------- | -------------------------------------------------------------------- | ---------------------------- |
| `vmware.vscode-spring-boot`            | Spring Boot 工具：`application.yml` 配置补全、`@RequestMapping` 跳转 | yml 无提示、跳转缺失         |
| `vscjava.vscode-spring-boot-dashboard` | Boot 应用一键启动/重启/调试                                          | 需手动 `mvn spring-boot:run` |

> **Lombok 说明**：不需要单独安装 Lombok 插件。`redhat.java` 1.5x 已内置支持，`@Data`/`@Builder` 正常提示。

## 3. 检查方法

```powershell
# 查看已安装扩展目录（Windows）
cmd /c "dir C:\Users\lu\.cursor\extensions /b"

# 查看已注册扩展 ID（更可靠，过滤掉残留目录）
$j = Get-Content "C:\Users\lu\.cursor\extensions\extensions.json" -Raw | ConvertFrom-Json
$j | ForEach-Object { $_.identifier.id } | Sort-Object
```

注意：扩展目录里可能残留同名不同后缀的目录（如 `-universal` 与无后缀版本），**以 `extensions.json` 注册列表为准**。

## 4. 安装方法

### 4.1 直接安装（网络通畅时）

```powershell
cursor --install-extension <扩展ID> --force
# 示例：cursor --install-extension vscjava.vscode-java-test --force
```

### 4.2 市场直连失败 → Open VSX 镜像（踩坑记录）

`cursor --install-extension` 从官方市场下载可能**长时间卡住无输出**（网络受限）。此时改用 Open VSX 镜像：

```powershell
# 1. 查版本与下载地址
curl.exe -s -m 60 "https://open-vsx.org/api/<publisher>/<name>/latest" -o "$env:TEMP\meta.json"

# 2. 解析下载 URL（meta.json 的 files.download 字段）
# 形如：https://open-vsx.org/api/vscjava/vscode-java-test/0.46.0/file/xxx.vsix

# 3. 下载 VSIX 到本地
curl.exe -s -L -m 900 "<下载URL>" -o "<本地路径>.vsix"

# 4. 本地安装（--force 覆盖旧版）
cursor --install-extension "<本地路径>.vsix" --force
```

经验：

- 循环用 PowerShell `Invoke-RestMethod` 请求 open-vsx 会偶发 404（限流），`curl.exe` 单次请求稳定
- 安装成功标志：终端输出 `Extension 'xxx' was successfully installed.`（node 的 `DeprecationWarning` 是噪音，可忽略）
- 安装完成后**重启 Cursor 或执行「开发者：重新加载窗口」**才生效

### 4.3 已装版本参考（2026-08）

| 插件                                 | 版本           |
| ------------------------------------ | -------------- |
| redhat.java                          | 1.55.0         |
| vscjava.vscode-java-debug            | 0.59.0         |
| vscjava.vscode-java-dependency       | 0.27.6         |
| vscjava.vscode-java-test             | **0.46.0**（当前激活版本，带 ASM bundle 冲突风险，见第 6 节） |
| vscjava.vscode-maven                 | 0.45.3         |
| vmware.vscode-spring-boot            | 2.4.2026081300 |
| vscjava.vscode-spring-boot-dashboard | 0.14.0         |

## 5. 安装后使用要点

- `*Test.java` 文件内方法/类旁会出现 ▶ 运行按钮，直接跑 JUnit 5 测试并查看报告
- 侧边栏新增 **Maven** 视图：`clean`/`test`/`package`/`install` 一键执行
- 侧边栏新增 **Spring Boot** 视图：应用一键启动/停止/调试
- `application.yml` 输入 `spring.` 等前缀有配置项补全提示

## 6. Run/Debug 按钮无反应排查（vscode-java-test 0.46.0 ASM bundle 冲突，2026-08 已定位）

### 症状

点编辑器上方 **Run Java / Debug** 按钮无任何输出（无报错弹窗），但 `mvn spring-boot:run` 命令行正常。

### 排查结论（与终端无关）

- **不是 Git Bash 默认终端的锅**：Run/Debug 走 LSP 通道（`vscode.java.startDebugSession`），默认 console 是调试控制台，不经过任何 shell。Git Bash 只影响手动敲命令。
- **按钮实际生效过**：日志中能看到 `launch debug info` → 12 秒后 `abnormal response`，即调试会话创建后语言服务器侧静默失败。

### 根因

`vscjava.vscode-java-test 0.46.0` 的 `package.json` 把 `org.objectweb.asm*_9.10.1.jar` 三件套声明为语言服务器扩展 bundle（jacoco 的依赖），而 `redhat.java 1.55.0` 自己已带同名同版本 ASM。JDT LS 初始化 `loadBundles` 时重复安装 → OSGi 拒绝：

```
Failed to load extension bundles
BundleException: A bundle is already installed with the name "org.objectweb.asm" and version "9.10.1"
```

后果：扩展 bundle 加载失败、项目模型残缺（ss_ws bin 只有 CmsApplication + 测试类）→ 调试器拿不到主类/classpath → Run 静默失败。`ss_ws`（Syntax-only）不执行 `loadBundles` 所以不触发，只有 Standard 模式（`jdt_ws` 存在）会中招。

### 当前状态与备用方案（2026-08）

**当前使用 0.46.0**（`extensions.json` 注册激活），若 Run/Debug 正常则无需处理。若日后按钮失效，按下述备用方案处理：

1. **降级 vscode-java-test 到 0.45.0**（0.45.0 不声明 ASM bundle，已验证 server 目录无 asm jar）。本地 vsix 安装方式见第 4 节，装完 `cursor --install-extension <本地.vsix> --force`。
2. **删除语言服务器缓存工作区**：`...\workspaceStorage\<hash>\redhat.java`（含 `ss_ws` 与 `jdt_ws`），重启 Cursor 后自动重建（约 30 秒）。
3. 验证：新开窗口后 `jdt_ws\.metadata\.log` 无 `Failed to load extension bundles`；Run 按钮可正常启动。

> ⚠️ 若 0.46.0 目录因 Cursor 运行中无法删除（`Device or resource busy`），保留即可——`extensions.json` 改指向 0.45.0 后，重启不会加载旧目录。
