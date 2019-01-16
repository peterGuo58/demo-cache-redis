package com.zq.springboot.redis.cache.democacheredis.repository;

import com.zq.springboot.redis.cache.democacheredis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
    User findByUserNameOrEmail(String username, String email);
    List<User> findByNickname(String nickname);
}