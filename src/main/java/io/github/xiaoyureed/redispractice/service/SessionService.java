package io.github.xiaoyureed.redispractice.service;

import io.github.xiaoyureed.redispractice.exception.BizException;
import io.github.xiaoyureed.redispractice.exception.LoginException;
import io.github.xiaoyureed.redispractice.pojo.dto.LoginReq;
import io.github.xiaoyureed.redispractice.pojo.dto.LoginResp;
import io.github.xiaoyureed.redispractice.pojo.entity.User;
import io.github.xiaoyureed.redispractice.repository.UserRepo;
import io.github.xiaoyureed.redispractice.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * @author EX-XIAOYU003
 * Date: 2019-7-25
 */
@Service
public class SessionService {
    @Autowired
    private UserRepo userRepo;

    public LoginResp login(LoginReq req) {
        final User loginUser = new User();
        BeanUtils.copy(req, loginUser);
        final User exist = userRepo.findOne(Example.of(loginUser)).orElse(null);
        if (exist == null) {
            throw new LoginException(">>> login error, wrong username/pwd");
        }
        final LoginResp result = new LoginResp();
        result.setId(exist.getId());
        return result;
    }
}
