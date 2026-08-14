package com.readant.cms.common;

import com.readant.cms.entity.OperationLog;
import com.readant.cms.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogOperationAspect {

    private final OperationLogMapper operationLogMapper;

    @Around("@annotation(logOperation)")
    public Object around(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

            Long targetId = null;
            String path = request != null ? request.getRequestURI() : "";
            String[] segments = path.split("/");
            for (int i = segments.length - 1; i >= 0; i--) {
                try {
                    targetId = Long.parseLong(segments[i]);
                    break;
                } catch (NumberFormatException ignored) {
                }
            }

            OperationLog logEntry = new OperationLog();
            logEntry.setModule(logOperation.module());
            logEntry.setAction(logOperation.action());
            logEntry.setTargetId(targetId);
            logEntry.setIp(request != null ? request.getRemoteAddr() : null);

            operationLogMapper.insert(logEntry);
            log.info("操作日志: module={}, action={}, targetId={}", logOperation.module(), logOperation.action(), targetId);
        } catch (Exception e) {
            log.warn("记录操作日志失败", e);
        }

        return result;
    }
}
