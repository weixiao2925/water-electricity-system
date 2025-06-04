package org.example.cvitme01.utils.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

/**
 * JWT策略接口，定义JWT操作的标准行为
 * 应用策略模式，允许不同的JWT实现
 */
public interface JwtStrategy {
    /**
     * 创建JWT令牌
     */
    String createToken(UserDetails details, int id, String username);
    
    /**
     * 验证并解析JWT令牌
     */
    DecodedJWT resolveToken(String headerToken);
    
    /**
     * 使JWT令牌失效
     */
    boolean invalidateToken(String headerToken);
    
    /**
     * 从JWT令牌中提取用户信息
     */
    UserDetails extractUser(DecodedJWT jwt);
    
    /**
     * 从JWT令牌中提取用户ID
     */
    Integer extractId(DecodedJWT jwt);
    
    /**
     * 获取令牌过期时间
     */
    Date getExpireTime();
}
