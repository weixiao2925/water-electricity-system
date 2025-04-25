package org.example.cvitme01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class WebConfiguration {

//    @Bean(name = "md5PasswordEncoder")
//    public PasswordEncoder passwordEncoder() {
//        return new MD5PasswordEncoder();
//    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
