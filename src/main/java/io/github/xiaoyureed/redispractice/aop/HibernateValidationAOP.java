// package io.github.xiaoyureed.redispractice.aop;
//
// import io.github.xiaoyureed.redispractice.pojo.BaseResp;
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Pointcut;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.annotation.Order;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
//
// import javax.validation.ConstraintViolation;
// import javax.validation.Validator;
// import java.util.Set;
//
// @Aspect
// @Component
// @Order(8)
// public class HibernateValidationAOP {
//     @Autowired
//     private Validator validator;
//
//     @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
//     public void pointcut(){}
//
//     @Around("pointcut()")
//     public Object validate(ProceedingJoinPoint joinPoint) throws Throwable {
//         final Object[] args = joinPoint.getArgs();
//         for (Object arg: args) {
//             if (arg != null) {
//                 final Set<ConstraintViolation<Object>> validations = validator.validate(arg);
//                 if (validations.size() > 0) {
//                     final ConstraintViolation<Object> validation   = validations.iterator().next();
//                     final String                      validationMessage      = validation.getMessage();
//                     final String                      name = validation.getPropertyPath().iterator().next().getName();
//
//                     final BaseResp baseResp = new BaseResp();
//                     baseResp.setCode("1");
//                     baseResp.setErrMsg(String.format("[%s] %s", name, validationMessage));
//                     return new ResponseEntity<>(baseResp, HttpStatus.OK);
//                 }
//             }
//         }
//
//         return joinPoint.proceed();
//     }
// }
