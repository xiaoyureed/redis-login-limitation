package io.github.xiaoyureed.redispractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.github.xiaoyureed.redispractice.repository")
public class  RedisPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisPracticeApplication.class, args);
    }

}
