# 09 Maven 基础：自动化的"施工队长"

## 通俗理解

Maven 就像项目的**施工队长 + 仓库管理员**。它负责：
1. **下载依赖**：自动去仓库（本地 `.m2` + 中央仓库）拉取项目用到的库
2. **构建**：编译、打包、运行、测试，一条命令搞定
3. **管理版本**：告诉你每个库用的什么版本

`pom.xml` 就是施工队长手里的**任务清单**。

## 核心概念

| 概念 | 说明 |
|------|------|
| `pom.xml` | 项目配置文件，声明依赖、插件、构建方式 |
| 坐标（GAV） | `groupId` + `artifactId` + `version` 唯一确定一个库 |
| 依赖（dependency） | 项目引入的外部库 |
| 生命周期 | validate → compile → test → package → install |
| 仓库 | 本地 `.m2/repository`，找不到再去中央仓库 |

## pom.xml 长什么样

```xml
<project>
  <!-- 坐标：唯一定位这个项目 -->
  <groupId>com.readant</groupId>
  <artifactId>cms</artifactId>
  <version>1.0.0</version>

  <!-- 继承 Spring Boot 父工程，统一管理版本 -->
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.x</version>
  </parent>

  <!-- 依赖：项目要用哪些库 -->
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>com.baomidou</groupId>
      <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    </dependency>
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>
  </dependencies>
</project>
```

## 常用命令（在 `bj-01-cms/` 目录下）

```bash
mvn compile          # 编译
mvn test             # 跑测试
mvn package          # 打 jar 包
mvn spring-boot:run  # 启动应用
```

## 设计意图

- 不用手工下载 jar 包、不用手动配 classpath
- 声明式：`pom.xml` 里写"我要用什么"，Maven 自动搞定版本和依赖树
- 版本统一：继承父工程 `spring-boot-starter-parent`，避免各库版本冲突

## 常见坑

- ❌ 忘了某个依赖，编译报 `ClassNotFoundException` → 去 pom.xml 加依赖
- ❌ 首次构建慢 → 因为要下载大量依赖到本地 `.m2`，属正常现象
- ❌ 本地 `.m2` 缓存损坏 → 报奇怪的构建错误，可清掉对应目录重新下
- ⚠️ 依赖标了 `optional=true`（如 lombok）→ 只在编译时需要，不会传给依赖方

## 自测题

1. `pom.xml` 里的 GAV（groupId/artifactId/version）作用是什么？
2. `mvn spring-boot:run` 和 `mvn package` 分别做什么？
3. 为什么 `spring-boot-starter-*` 这种依赖通常不需要写 version？（提示：父工程统一管理）

## 对照实战

打开 `bj-01-cms/pom.xml`，看看引入了哪些 starter 依赖，哪些是框架、哪些是工具库。认一下 `spring-boot-starter-web`、`mybatis-plus`、`lombok`、`flyway` 分别负责什么。