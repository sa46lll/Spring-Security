package io.security.basicsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * AnonymousAuthenticationFilter, 동시 세션 제어,
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig2 extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated();
        http
                .formLogin();

        /*
        // 동시 세션 제어
        http
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)     // b. 현재 사용자 인증 실패
                ;

        */

        // 세션 고정 보호
        http
                .sessionManagement()
                .sessionFixation().none();


    }
}
