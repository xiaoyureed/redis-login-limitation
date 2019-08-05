package io.github.xiaoyureed.redispractice.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author EX-XIAOYU003
 * Date: 2019-7-25
 */
@Data
@Entity(name = "user") // managed with hibernate
public class User {
    /**
     * https://blog.csdn.net/tree_java/article/details/71158122
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String pwd;
}
