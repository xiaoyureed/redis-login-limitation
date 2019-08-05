package io.github.xiaoyureed.redispractice.aop;

import io.github.xiaoyureed.redispractice.exception.BizException;
import io.github.xiaoyureed.redispractice.exception.LoginException;
import io.github.xiaoyureed.redispractice.pojo.BaseResp;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;

/**
 * print request log;
 * print execution time;
 * global exception handing;
 */
// @Component
@Aspect
@Slf4j
@Order(10)// control aop order, first in last out
public class ControllerAOP {

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object handleControllerMethod(ProceedingJoinPoint point) throws Throwable {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest       request    = attributes.getRequest();
        log.info(">>> {} - {} from {}", request.getMethod(), request.getRequestURL().toString(), request.getRemoteAddr());
        final Signature signature = point.getSignature();

        final long     start = System.nanoTime();
        ResponseEntity result;
        try {
            result = (ResponseEntity) point.proceed();
            log.info(">>> {} use time: {}ms", signature, Duration.ofNanos(System.nanoTime() - start).toMillis());
        } catch (Throwable e) {
            result = handleException(e);
        }
        return result;
    }

    private ResponseEntity<BaseResp> handleException(Throwable e) throws Throwable {
        final BaseResp result = new BaseResp();
        result.setCode("1");

        log.error(">>> handle exception", e);
        if (e instanceof LoginException) {
            throw e; // 抛给 RedisLimitAop 处理
        } else if (e instanceof BizException) {
            result.setErrMsg(e.getMessage());
        } else {
            result.setErrMsg("unknown error");
            // email...
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
