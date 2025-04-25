package org.example.cvitme01.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {
    //在配置yml文件里面获取密钥key
    @Value("${spring.security.jwt.key}")
    String key;
    //在配置yml文件里面获取过期时间
    @Value("${spring.security.jwt.expire}")
    int expire;

    //导入redis(记住注入的时候一定不要跟其他同类注入同名)
    @Resource
    StringRedisTemplate RedisTemplate_jwt;

    //创建jwt令牌（1）
    public String createJwt(UserDetails details, int id, String username){
        Algorithm algorithm=Algorithm.HMAC256(key);//jwt加密算法
        Date expire=this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())//给每个令牌一个Uuid
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("authorities",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())//用户权限
                .withExpiresAt(expire)//令牌过期时间
                .withIssuedAt(new Date())//创建时间
                .sign(algorithm);
    }
    //配置令牌过期时间（1）
    public Date expireTime(){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24);
        return  calendar.getTime();
    }

    //验证合法性，再将令牌弄进黑名单里面（2）
    public boolean invalidateJwt(String headerToken){
        String token=this.convertToken(headerToken);
        //没有jwt就不让它退出
        if (token==null) return false;
        //创建一个同源key的jwt验证实例，配合下面的verify验证
        Algorithm algorithm=Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier=JWT.require(algorithm).build();
        try{
            DecodedJWT jwt=jwtVerifier.verify(token);
            String id=jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }

    //验证jwt令牌（验证成功就返回jwt实例和过期时间）（3）
    public DecodedJWT resolveJwt(String headerToken){
        String token=this.convertToken(headerToken);
        if (token==null) return null;
        Algorithm algorithm=Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier=JWT.require(algorithm).build();
        try{
            DecodedJWT verify=jwtVerifier.verify(token);
            if (this.invalidateJwt(verify.getId())){
                return null;
            }
            Date expiresAt=verify.getExpiresAt();
            return new Date().after(expiresAt) ? null : verify;
        }catch (JWTVerificationException e){
            return null;
        }
    }

    //解析jwt令牌内容（User部分）
    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims=jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }
    //解析jwt令牌内容(id部分)
    public Integer toId(DecodedJWT jwt){
        Map<String,Claim> claims=jwt.getClaims();
        return claims.get("id").asInt();
    }

    //----内部工具方法
    //判断jwt是否合法(合法就返回jwt内容)(2)
    private String convertToken(String headerToken){
        return (headerToken==null || !headerToken.startsWith("Bearer"))
                ?  null : headerToken.substring(7);
    }
    //将令牌丢进小黑屋(2)
    private boolean deleteToken(String uuid,Date time){
        if (this.isInvalidToken(uuid)) return false;
        Date now=new Date();
        long expire=Math.max(time.getTime()-now.getTime(),0);//算出剩余时间
        RedisTemplate_jwt.opsForValue().set(Const.JWT_BLACK_LIST+uuid,"",expire, TimeUnit.MICROSECONDS);
        return true;
    }
    //判断令牌是否失效(在不在redis的黑名单中)(2)
    private boolean isInvalidToken(String uuid){
        return Boolean.TRUE.equals(RedisTemplate_jwt.hasKey(Const.JWT_BLACK_LIST+uuid));
    }

}

