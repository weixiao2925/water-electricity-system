package org.example.cvitme01.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cvitme01.utils.Const;
import org.example.cvitme01.utils.JwtUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//自定义filter（验证用户携带Authorization请求头，要是没有携带或者jwt不正确，将进行拦截）
@Component
@RequiredArgsConstructor
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final StringRedisTemplate template;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Authorization的请求头的信息进行拦截
        String authorization =request.getHeader("Authorization");
        DecodedJWT jwt= jwtUtils.resolveJwt(authorization);
        //要是不为空，就进行验证成功授权
        if (jwt !=null){
            UserDetails user=jwtUtils.toUser(jwt);
            if (!template.hasKey(Const.BANNED_BLOCK + jwtUtils.toId(jwt))){
                /*
                 *  创建一个UsernamePasswordAuthenticationToken实例。UsernamePasswordAuthenticationToken是Authentication接口的实现
                 *  ，用来保存当前用户的认证信息。它接收了三个参数：
                 *  principal：通常是UserDetails，代表了验证的用户。
                 *  credentials：通常是密码，但在这里使用JWT作为认证方式，所以密码不需要，用null代替。
                 *  authorities：用户的权限集，是UserDetails获取的。
                 */
                logger.error(jwt.getClaims());
                UsernamePasswordAuthenticationToken authentication=
                        new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //携带验证成功的信息到SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);
                //将id作为请求的属性保存起来(会存在request里面)
                request.setAttribute(Const.ATTR_USER_ID,jwtUtils.toId(jwt));
            } else {
                jwtUtils.invalidateJwt(authorization);
            }

        }
        //为空就丢给下一个过滤器处理
        filterChain.doFilter(request,response);
    }

}
