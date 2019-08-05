---
title: redis-login-limitation
tags: [redis]
---

<!--more-->

<div align="center">
利用 redis 实现登陆次数限制, 注解 + aop, 核心代码很简单.
</div>

# 基本思路

比如希望达到的要求是这样: 在 1min 内登陆异常次数达到5次, 锁定该用户 1h

那么登陆请求的参数中, 会有一个参数唯一标识一个 user, 比如 邮箱/手机号/userName

用这个参数作为key存入redis, 对应的value为登陆错误的次数, string 类型, 并设置过期时间为 1min. 当获取到的 value == "4" , 说明当前请求为第 5 次登陆异常, 锁定.

所谓的锁定, 就是将对应的value设置为某个标识符, 比如"lock", 并设置过期时间为 1h

# 核心代码

定义一个注解, 用来标识需要登陆次数校验的方法

```java
package io.github.xiaoyureed.redispractice.anno;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLimit {
    /**
     * 标识参数名, 必须是请求参数中的一个
     */
    String identifier();

    /**
     * 在多长时间内监控, 如希望在 60s 内尝试
     * 次数限制为5次, 那么 watch=60; unit: s
     */
    long watch();

    /**
     * 锁定时长, unit: s
     */
    long lock();

    /**
     * 错误的尝试次数
     */
    int times();
}

```

编写切面, 在目标方法前后进行校验, 处理...

```java
package io.github.xiaoyureed.redispractice.aop;

@Component
@Aspect
// Ensure that current advice is outer compared with ControllerAOP
// so we can handling login limitation Exception in this aop advice.
//@Order(9)
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

```

这样使用:

```java
package io.github.xiaoyureed.redispractice.web;

@RestController
public class SessionResources {

    @Autowired
    private SessionService sessionService;

    /**
     * 1 min 之内尝试超过5次, 锁定 user 1h
     */
    @RedisLimit(identifier = "name", watch = 30, times = 5, lock = 10)
    @RequestMapping(value = "/session", method = RequestMethod.POST)
    public ResponseEntity<LoginResp> login(@Validated @RequestBody LoginReq req) {
        return new ResponseEntity<>(sessionService.login(req), HttpStatus.OK);
    }
}

```

# references

https://github.com/xiaoyureed/redis-login-limitation
