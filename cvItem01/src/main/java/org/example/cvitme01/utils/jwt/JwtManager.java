package org.example.cvitme01.utils.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT管理器，应用工厂模式统一管理JWT操作
 */
@Component
@RequiredArgsConstructor
public class JwtManager {
    private final JwtStrategy jwtStrategy;
    
    /**
     * 创建JWT令牌
     */
    public String createJwt(UserDetails details, int id, String username) {
        return jwtStrategy.createToken(details, id, username);
    }
    
    /**
     * 解析JWT令牌
     */
    public DecodedJWT resolveJwt(String headerToken) {
        return jwtStrategy.resolveToken(headerToken);
    }
    
    /**
     * 使JWT令牌失效
     */
    public boolean invalidateJwt(String headerToken) {
        return jwtStrategy.invalidateToken(headerToken);
    }
    
    /**
     * 从JWT令牌提取用户信息
     */
    public UserDetails toUser(DecodedJWT jwt) {
        return jwtStrategy.extractUser(jwt);
    }
    
    /**
     * 从JWT令牌提取用户ID
     */
    public Integer toId(DecodedJWT jwt) {
        return jwtStrategy.extractId(jwt);
    }
    
    /**
     * 获取令牌过期时间
     */
    public Date expireTime() {
        return jwtStrategy.getExpireTime();
    }
}
