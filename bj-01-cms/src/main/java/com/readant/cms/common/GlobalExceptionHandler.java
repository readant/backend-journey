package com.readant.cms.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器 —— 代码里的\"失物招领处\"
 *
 * @RestControllerAdvice 注解的意思是：所有 Controller 抛出的异常，都会先经过这里处理。
 * 这样 Controller 就不用每个方法都写 try-catch 了。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（我们主动抛出的、可预料的错误）
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK) // HTTP 状态码还是 200，具体错误通过 R.code 区分
    public R<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验失败（@Valid 触发的校验错误）
     *
     * 比如前端没传必填字段，Spring 会自动抛出这个异常，我们在这里统一格式化错误信息。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 只取第一个错误信息返回，避免给前端返回一长串
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败: {}", message);
        return R.error(400, message);
    }

    /**
     * 处理"路径不存在"
     *
     * Spring 6.1 起，访问未映射的路径会抛 NoResourceFoundException。若不单独接住，
     * 它会落进下面的 Exception 兜底，变成 500「服务器内部错误」——明明是客户端敲错了地址，
     * 却报成服务端故障，排查方向会被带偏。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Void> handleNotFound(NoResourceFoundException e) {
        log.warn("请求路径不存在: {}", e.getResourcePath());
        return R.error(404, "接口不存在");
    }

    /**
     * 处理未预期的系统异常（兜底处理）
     *
     * 如果上面的异常处理器都没匹配到，就会走到这里。这是最后一道防线。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleUnknownException(Exception e) {
        log.error("系统异常", e);
        return R.error(500, "服务器内部错误，请稍后重试");
    }
}
