# 07 异常处理：错误的安全网

## 通俗理解

异常（Exception）就像程序运行时的**意外事件**。Java 用 `try-catch-finally` 织一张安全网：把可能出错的代码放进 `try`，出错时跳到 `catch` 处理，`finally` 里放"无论是否出错都要做的收尾"。

## 代码示例

```java
public class ExceptionDemo {
    public static void main(String[] args) {
        try {
            int result = divide(10, 0);   // 可能抛异常
            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("除数不能为 0：" + e.getMessage());
        } finally {
            System.out.println("这段总会执行");  // 清理资源、收尾
        }
    }

    static int divide(int a, int b) {
        return a / b;   // b=0 会抛 ArithmeticException
    }
}
```

## 受检异常 vs 非受检异常（关键区别）

| | 受检异常（Checked） | 非受检异常（Runtime） |
|---|---|---|
| 编译时 | 必须处理（try 或 throws 声明） | 不必强制处理 |
| 父类 | `Exception`（除 RuntimeException） | `RuntimeException` 及其子类 |
| 例子 | `IOException`、`SQLException` | `NullPointerException`、`ArithmeticException` |
| 处理 | 强制 `try-catch` 或方法 `throws` | 可选，但运行时可能崩 |

```java
// 受检异常：方法必须声明 throws，或调用处 try-catch
public void readFile(String path) throws IOException {
    // ...读文件
}

// 非受检异常：不强制声明
public void process(int x) {
    if (x < 0) throw new IllegalArgumentException("x 不能为负");
}
```

## 业务异常模式（实战关键）

项目里通常自定义一个业务异常，配合 `throw` 主动抛出，由全局处理器统一捕获：

```java
// 自定义业务异常（继承 RuntimeException，属于非受检）
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

// 业务逻辑里主动抛
public void login(String name, String pwd) {
    if (name == null || name.isBlank()) {
        throw new BusinessException("用户名不能为空");
    }
}
```

**优势**：业务代码不用到处 try-catch，异常向上抛，最后由 `@ControllerAdvice` 统一转成标准返回体。实战见 `GlobalExceptionHandler.java`。

## 常见坑

- ❌ 捕获了异常却什么都不做（空 catch）→ 吞掉错误，难以排查
- ❌ 用异常做正常流程控制 → 应该用 if 判断
- ✅ 业务校验失败用 `throw new BusinessException(...)`，让全局统一处理
- ✅ 打印异常要用 `log.error("xxx", e)` 带上堆栈，而不是 `e.getMessage()`

## 自测题

1. 受检异常和非受检异常的核心区别是什么？
2. `finally` 里的代码什么时候会执行？
3. 为什么项目里自定义 `BusinessException` 继承 `RuntimeException` 而不是 `Exception`？（提示：非受检可以不用到处声明 throws）

## 对照实战

打开 `bj-01-cms/.../common/BusinessException.java` 和 `GlobalExceptionHandler.java`，看业务异常是怎么被全局统一捕获并转成 `R` 返回体的。