package io.github.xiaoyureed.redispractice.exception;

/**
 * @author xiaoyu
 * date: 2019/8/4
 */
public class LoginException extends BizException {
    public LoginException(String message) {
        super(message);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginException(Throwable cause) {
        super(cause);
    }
}
