package org.example.cvitme01.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.cvitme01.utils.Const;
import org.example.cvitme01.utils.jwt.JwtManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

/**
 * JWT认证处理器，应用责任链模式优化认证流程
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationHandler {
    private final JwtManager jwtManager;
    private final StringRedisTemplate template;
    
    /**
     * 处理JWT认证
     * @return 处理结果，true表示成功处理，false表示未能处理
     */
    public boolean handleJwtAuthentication(HttpServletRequest request, String authorization) {
        DecodedJWT jwt = jwtManager.resolveJwt(authorization);
        if (jwt == null) {
            return false;
        }
        
        Integer userId = jwtManager.toId(jwt);
        if (template.hasKey(Const.BANNED_BLOCK + userId)) {
            jwtManager.invalidateJwt(authorization);
            return false;
        }
        
        UserDetails userDetails = jwtManager.toUser(jwt);
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.setAttribute(Const.ATTR_USER_ID, userId);
        return true;
    }
}
