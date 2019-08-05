package io.github.xiaoyureed.redispractice.aop;

import io.github.xiaoyureed.redispractice.anno.RedisLimit;
import io.github.xiaoyureed.redispractice.exception.LoginException;
import io.github.xiaoyureed.redispractice.pojo.BaseResp;
import io.github.xiaoyureed.redispractice.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
// Ensure that current advice is outer compared with ControllerAOP
// so we can handling login limitation Exception in this aop advice.
@Order(9)
@Slf4j
public class RedisLimitAOP {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(io.github.xiaoyureed.redispractice.anno.RedisLimit)")
    public Object handleLimit(ProceedingJoinPoint joinPoint) {
        MethodSignature  methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method     method          = methodSignature.getMethod();
        final RedisLimit redisLimitAnno  = method.getAnnotation(RedisLimit.class);// 貌似可以直接在方法参数中注入 todo

        final String identifier = redisLimitAnno.identifier();
        final long   watch      = redisLimitAnno.watch();
        final int    times      = redisLimitAnno.times();
        final long   lock       = redisLimitAnno.lock();
        // final ServletRequestAttributes att             = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // final HttpServletRequest       request         = att.getRequest();
        // final String                   identifierValue = request.getParameter(identifier);

        String identifierValue = null;
        try {
            final Object arg           = joinPoint.getArgs()[0];
            final Field  declaredField = arg.getClass().getDeclaredField(identifier);
            declaredField.setAccessible(true);
            identifierValue = (String) declaredField.get(arg);
        } catch (NoSuchFieldException e) {
            log.error(">>> invalid identifier [{}], cannot find this field in request params", identifier);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(identifierValue)) {
            log.error(">>> the value of RedisLimit.identifier cannot be blank, invalid identifier: {}", identifier);
        }

        // check User locked
        final ValueOperations<String, String> ssOps = stringRedisTemplate.opsForValue();
        final String                          flag  = ssOps.get(identifierValue);
        if (flag != null && "lock".contentEquals(flag)) {
            final BaseResp result = new BaseResp();
            result.setErrMsg("user locked");
            result.setCode("1");
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        ResponseEntity result;
        try {
            result = (ResponseEntity) joinPoint.proceed();
        } catch (Throwable e) {
            result = handleLoginException(e, identifierValue, watch, times, lock);
        }
        return result;
    }

    private ResponseEntity handleLoginException(Throwable e, String identifierValue, long watch, int times, long lock) {
        final BaseResp result = new BaseResp();
        result.setCode("1");
        if (e instanceof LoginException) {
            log.info(">>> handle login exception...");
            final ValueOperations<String, String> ssOps = stringRedisTemplate.opsForValue();
            Boolean                               exist = stringRedisTemplate.hasKey(identifierValue);
            // key doesn't exist, so it is the first login failure
            if (exist == null || !exist) {
                ssOps.set(identifierValue, "1", watch, TimeUnit.SECONDS);
                result.setErrMsg(e.getMessage());
                return new ResponseEntity<>(result, HttpStatus.OK);
            }

            String count = ssOps.get(identifierValue);
            // has been reached the limitation
            if (Integer.parseInt(count) + 1 == times) {
                log.info(">>> [{}] has been reached the limitation and will be locked for {}s", identifierValue, lock);
                ssOps.set(identifierValue, "lock", lock, TimeUnit.SECONDS);
                result.setErrMsg("user locked");
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            ssOps.increment(identifierValue);
            result.setErrMsg(e.getMessage() + "; you have try " + ssOps.get(identifierValue) + "times.");
        }
        log.error(">>> RedisLimitAOP cannot handle {}", e.getClass().getName());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
