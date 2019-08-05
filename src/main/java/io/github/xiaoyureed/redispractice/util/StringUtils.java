package io.github.xiaoyureed.redispractice.util;

/**
 * @author EX-XIAOYU003
 * Date: 2019-8-2
 */
public class StringUtils {
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().length() == 0;
    }
}
