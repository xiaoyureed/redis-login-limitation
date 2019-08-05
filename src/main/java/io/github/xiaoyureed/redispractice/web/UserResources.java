package io.github.xiaoyureed.redispractice.web;

import io.github.xiaoyureed.redispractice.pojo.entity.User;
import io.github.xiaoyureed.redispractice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author EX-XIAOYU003
 * Date: 2019-7-25
 */
@RestController
public class UserResources {
    @Autowired
    private UserService userService;

    @RequestMapping("/users/{id}")
    public ResponseEntity<User> findAll(@PathVariable("id") String id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }
}
