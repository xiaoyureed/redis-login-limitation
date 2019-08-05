package io.github.xiaoyureed.redispractice.util;

import org.springframework.cglib.beans.BeanCopier;

/**
 * @author EX-XIAOYU003
 * Date: 2019-7-25
 */
public class BeanUtils {

    public static void copy(Object source, Object target, String... properties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, properties);
    }

    // public static <S, T> void copy(S source, T target) {
    //     final BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
    //     beanCopier.copy(source, target, null);
    // }
}
