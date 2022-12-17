package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.User;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);
    private final RedisTemplate<String, User> userRedisTemplate;

    public void setUser(User user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}, {}", key, user);
        // TTL 설정 필요
        // 일정시간 지나면 해당 데이터 만료
        userRedisTemplate.opsForValue()
                         .set(key, user, USER_CACHE_TTL);
    }

    public Optional<User> getUser(String userName) {

        String key = getKey(userName);
        User user = userRedisTemplate.opsForValue()
                                     .get(key);
        log.info("Get User to Redis {}, {}", key, user);
        return Optional.ofNullable(user);
    }

    // key 설정
    // filter에서 유저 체크를 캐싱으로 대체
    // 해당 부분에서 유저를 찾는 것은 username으로 검색
    // prefix를 통해 구분
    private String getKey(String userName) {
        return "USER:" + userName;
    }

}
