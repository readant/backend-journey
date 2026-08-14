# Spotless 代码格式化（palantir 风格）

> 环境：Windows + Maven + Java 17。本仓库 Java 代码格式的唯一权威是 **Spotless + palantir-java-format**，一切格式争议以 `mvn spotless:apply` 的输出为准。

## 1. 是什么

Spotless 是 Maven/Gradle 通用代码格式化插件，本仓库用它统一全部 Java 代码风格，解决两个问题：

- **AI 生成代码风格随机**（缩进、空行、换行各写各的）
- **IDE 保存格式化与提交代码不一致**（产生无意义的格式 diff）

格式化引擎选 **palantir-java-format**（Palantir 开源，风格接近 Google Java Format，缩进 4 空格、行宽 120）。

## 2. 配置（已就位，`bj-01-cms/pom.xml`）

在 `<build><plugins>` 中追加（当前版本 2026-08）：

```xml
<plugin>
    <groupId>com.diffplug.spotless</groupId>
    <artifactId>spotless-maven-plugin</artifactId>
    <version>3.9.0</version>
    <configuration>
        <java>
            <includes>
                <include>src/main/java/**/*.java</include>
                <include>src/test/java/**/*.java</include>
            </includes>
            <palantirJavaFormat>
                <version>2.97.0</version>
            </palantirJavaFormat>
        </java>
    </configuration>
</plugin>
```

配套约束（都已入库）：

- `.editorconfig`（仓库根）：缩进 4 空格、LF 行尾、UTF-8、文件末尾补换行、去行尾空格
- `.gitattributes`：`* text=auto eol=lf`，保证 Git 侧行尾统一
- `.cursor/rules/coding-java.mdc` 第 6 节「格式规范」：提交前必须 apply/check

## 3. 常用命令

在 `bj-01-cms` 目录下执行：

```bash
mvn spotless:apply    # 一键格式化全部 Java 文件（写文件）
mvn spotless:check    # 只校验不修改，不通过则 BUILD FAILURE
```

- 提交前跑 `apply`，确认 `check` 通过后再提交
- 首次 `apply` 需联网下载插件与格式化引擎，网络不通会失败，重试即可
- 全量格式化只影响排版，不改逻辑；验证：`mvn test` 应全部通过

## 4. 与 IDE 保存格式化（Cursor formatOnSave）的关系

| 场景 | 行为 |
|------|------|
| Cursor 保存自动格式化开启 | 只遵守 `.editorconfig` 的通用约束（缩进/LF/去行尾空格），**不等于** palantir 完整输出，import 排序、换行位置可能有差异 |
| 提交前 `mvn spotless:apply` | **唯一权威**，把任何差异拉回 palantir 标准 |

结论：保存格式化开着不冲突，但它不是 palantir。真正的格式基线是提交前的 `mvn spotless:apply`。

如果保存格式化造成大量噪音干扰看 diff，可对 Java 单独关闭：

```jsonc
// Cursor 设置 settings.json
"[java]": {
    "editor.formatOnSave": false
}
```

## 5. 踩坑记录

- **CRLF 假改动**：`.gitattributes` 在文件入库后才添加时，工作区显示大量 `M` 但内容没变。用 `git add --renormalize` 一次性纠正，之后再无噪音。若 blob 已是 LF，renormalize 后直接干净、无提交可做。
- **版本适配 JDK**：palantir-java-format 需与运行 Maven 的 JDK 兼容（本仓库 JDK 17，palantir 2.97.0 正常）。换 JDK 大版本时先确认格式化引擎版本支持。
- **插件下载失败**：首次 apply 需从 Maven Central 拉依赖，网络不稳定时重试；可用 `mvn dependency:get` 单独确认插件版本可用。

## 6. 版本参考（2026-08）

| 组件 | 版本 |
|------|------|
| spotless-maven-plugin | 3.9.0 |
| palantir-java-format | 2.97.0 |
| Maven / JDK | 3.9.9 / 17 (Temurin) |