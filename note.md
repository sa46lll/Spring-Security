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
  
- Remember Me 인증
  - 세션이 만료되고 웹 브라우저가 종료된 후에도 어플리케이션이 사용자를 기억하는 기능
  - Remember-Me 쿠키에 대한 http 요청을 확인 -> 토큰 기반 인증을 사용해 유효성 검사하고 토큰이 검증되면 로그인
  - 사용자 라이프 사이클
    - 인증 성공(Remember-Me 쿠키 설정)
    - 인증 실패(쿠키가 존재하면 쿠키 무효화)
    - 로그아웃(쿠키가 존재하면 쿠키 무효화)
  ```java
  protected void configure(HttpSecurity http) throws Exception {
        http.rememberMe()
                .rememberMeParameter(“remember”)                // 기본 파라미터명은 remember-me
                .tokenValiditySeconds(3600)                     // Default 는 14일
                .alwaysRemember(true)                           // 리멤버 미 기능이 활성화되지 않아도 항상 실행
                .userDetailsService(userDetailsService)         // 사용자 계정을 조회할 때 필요한 클래스 
  }
  ```
  


