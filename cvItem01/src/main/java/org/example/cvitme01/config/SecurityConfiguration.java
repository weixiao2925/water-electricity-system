package org.example.cvitme01.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.dto.Account;
import org.example.cvitme01.entity.vo.response.AuthorizeVO;
import org.example.cvitme01.filter.JwtAuthorizeFilter;
import org.example.cvitme01.service.AccountService;
import org.example.cvitme01.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AccountService accountService;
    private final JwtUtils jwtUtils;
    private final JwtAuthorizeFilter jwtAuthorizeFilter;

    //security过滤器链
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                //路由管理
                .authorizeHttpRequests(conf->conf
                        .requestMatchers("/api/auth/**").permitAll()//允许这个路径的路由通过（可直接访问）
                        .anyRequest().authenticated()//其他的要验证后访问
                )
                //登录
                .formLogin(conf->conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )
                //登出
                .logout(conf->conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                //没有登录或不够权限
                .exceptionHandling(conf->conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDeny)
                )
                //关闭csrf
                .csrf(AbstractHttpConfigurer::disable)
                //会话管理
                .sessionManagement(conf->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//关闭通过session来验证（无状态验证）
                )
                //添加自己定义的filter
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }




    //----登录
    //登录验证成功
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");//返回值的字符编码
        User user=(User) authentication.getPrincipal();//获取登录的用户信息
        //获取存在数据库里面的用户信息
        Account account=accountService.findByUsername(user.getUsername());
        //创建jwt，发布token
        String token=jwtUtils.createJwt(user, Math.toIntExact(account.id()), account.username());
        AuthorizeVO vo =new AuthorizeVO();
        vo.setUsername(account.username());
        vo.setRole(account.role());
        vo.setToken(token);
        vo.setExpire(jwtUtils.expireTime());
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }
    //登录失败
    private void onAuthenticationFailure(HttpServletRequest request,
                                         HttpServletResponse response,
                                         AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");//返回值的字符编码
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }
    //----登出
    //登出成功(把token关进小黑屋)
    private void onLogoutSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");//返回值的字符编码
        PrintWriter writer = response.getWriter();
        String authorization=request.getHeader("Authorization");//获取Authorization请求头中的jwt信息
        if (jwtUtils.invalidateJwt(authorization)){
            writer.write(RestBean.success().asJsonString());
        }else {
            writer.write(RestBean.failure(400,"退出登录失败").asJsonString());
        }
    }
    //----没有登陆，或者权限不够
    //没有登录
    private void onUnauthorized(HttpServletRequest request,
                                HttpServletResponse response,
                                AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");//返回值的字符编码
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }
    //权限不够
    private void onAccessDeny(HttpServletRequest request,
                              HttpServletResponse response,
                              AccessDeniedException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");//返回值的字符编码
        response.getWriter().write(RestBean.forbidden(exception.getMessage()).asJsonString());
    }
}
