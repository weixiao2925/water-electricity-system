package org.example.cvitme01.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cvitme01.utils.Const;
import org.example.cvitme01.utils.JwtUtils;
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


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization=request.getHeader("Authorization");
        DecodedJWT jwt=jwtUtils.resolveJwt(authorization);
        if (jwt!=null) {
            UserDetails user=jwtUtils.toUser(jwt);
            //验证成功授权
            UsernamePasswordAuthenticationToken authentication=
                    new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //将id存进request
            request.setAttribute(Const.ATTR_USER_ID,jwtUtils.toId(jwt));
        }
        //为空时直接放行
        filterChain.doFilter(request, response);
    }
}
