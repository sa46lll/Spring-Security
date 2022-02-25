package io.security.basicsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * CsrfFilter, 필터 초기화와 다중 보안 설정
 */

@Configuration
@EnableWebSecurity
@Order(0)           // SecurityConfig3_2와 다른 순서를 주기 위함.
public class SecurityConfig3 extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/admin/**")
                .authorizeRequests()
                .anyRequest().authenticated()
        .and()
                .httpBasic();

        /*
        // CsrfFilter
        http
                .csrf();      // csrf 필터 생성 (기본적으로 생성 됨)

         */
    }
}

@Configuration
@Order(1)
class SecurityConfig3_2 extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().permitAll()
        .and()
                .formLogin()
        ;

    }
}