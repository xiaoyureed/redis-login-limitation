package io.github.xiaoyureed.redispractice.exception;

/**
 * @author xiaoyu
 * date: 2019/8/4
 */
public class SystemException extends RuntimeException {
    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }
}
