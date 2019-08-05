package io.github.xiaoyureed.redispractice.repository;

import io.github.xiaoyureed.redispractice.pojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CrudRepository<User, Integer>
 * @author EX-XIAOYU003
 * Date: 2019-7-25
 */
public interface UserRepo extends JpaRepository<User, Long> {
}
