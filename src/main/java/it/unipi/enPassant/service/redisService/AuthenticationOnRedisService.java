package it.unipi.enPassant.service.redisService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationOnRedisService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  public void storeToken(String token, Object value) {
    redisTemplate.opsForValue().set(token, value);
  }

  public void deleteToken(String token, Object value) {
    redisTemplate.delete(token);
  }
}
