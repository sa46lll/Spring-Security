### 스프링 시큐리티 기본 API & Filter

> 인증 & 인가

- 인증


> 인증 API

- Form Login
    ```java
    protected void configure(HttpSecurity http) throws Exception {
         http.formLogin()
                    .loginPage("/login.html")   			// 사용자 정의 로그인 페이지
                    .defaultSuccessUrl("/home")			// 로그인 성공 후 이동 페이지
                    .failureUrl("/login.html?error=true")		// 로그인 실패 후 이동 페이지
                    .usernameParameter("username")			// 아이디 파라미터명 설정
                    .passwordParameter("password")			// 패스워드 파라미터명 설정
                    .loginProcessingUrl("/login")			// 로그인 Form Action Url
                    .successHandler(loginSuccessHandler())		// 로그인 성공 후 핸들러
                    .failureHandler(loginFailureHandler())		// 로그인 실패 후 핸들러
    }
    ```
  


