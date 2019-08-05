package io.github.xiaoyureed.redispractice.aop;

import io.github.xiaoyureed.redispractice.pojo.BaseResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * hibernate validation 异常处理; 由于异常发生时
 * 还没有进入controller中的方法, 所以没法在aop中处理, 暂时在这里处理
 */
// @RestControllerAdvice
@Slf4j
public class HibernateValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResp> handleValidateException(MethodArgumentNotValidException e) {
        final List<ObjectError> errors = e.getBindingResult().getAllErrors();
        final List<String> collect = errors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        final BaseResp result = new BaseResp();
        result.setErrMsg(collect.toString());
        result.setCode("1");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
