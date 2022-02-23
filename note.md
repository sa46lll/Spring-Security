### 스프링 시큐리티 기본 API & Filter

> 인증 & 인가

- 인증
  - 유저가 누구인지 확인하는 절차, 회원가입하고 로그인 하는 것.
    - 방문자가 자신이 회사 건물에 들어 갈 수있는지 확인 받는 과정
- 인가
  - 유저에 대한 권한을 허락하는 것.
    - 방문자가 회사 건물에 방문했을 때, 허가된 공간에만 접근 가능


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
  
- Logout
  ```java
  protected void configure(HttpSecurity http) throws Exception {
       http.logout()						// 로그아웃 처리
               .logoutUrl("/logout")				// 로그아웃 처리 URL
               .logoutSuccessUrl("/login")			// 로그아웃 성공 후 이동페이지
               .deleteCookies("JSESSIONID", "remember-me") 	// 로그아웃 후 쿠키 삭제
               .addLogoutHandler(logoutHandler())		        // 로그아웃 핸들러
               .logoutSuccessHandler(logoutSuccessHandler()) 	// 로그아웃 성공 후 핸들러
  }
  ```



