package io.github.xiaoyureed.redispractice.web;

import io.github.xiaoyureed.redispractice.anno.RedisLimit;
import io.github.xiaoyureed.redispractice.pojo.dto.LoginReq;
import io.github.xiaoyureed.redispractice.pojo.dto.LoginResp;
import io.github.xiaoyureed.redispractice.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
