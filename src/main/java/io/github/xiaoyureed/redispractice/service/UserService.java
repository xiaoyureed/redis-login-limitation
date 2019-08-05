package io.github.xiaoyureed.redispractice.service;

import io.github.xiaoyureed.redispractice.pojo.entity.User;
import io.github.xiaoyureed.redispractice.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author EX-XIAOYU003
 * Date: 2019-7-25
 */
@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User findById(String id) {
        return userRepo.findById(Long.parseLong(id)).orElse(null);
    }
}
