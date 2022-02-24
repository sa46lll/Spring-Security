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
- AnonymousAuthenticationFilter
  - 익명사용자와 인증사용자를 구분해서 처리하기 위한 용도로 사용
    - SecurityContext에 Authentication이 null일 경우, AnonymousAnthenticationToken을 주입해줌
  - 인증 여부를 구현할 때 isAnonymous() 와 isAuthenticated()로 구분해서 사용
  - 인증객체를 세션에 저장하지 않는다.
- 세션
  - 동시 세션 제어
    - 동일한 계정으로 인증 받을때 생성되는 세션의 허용갯수가 초과하지 않고 유지하는 것
    - 제어 방법
      1. 이전 사용자 세션 만료
      2. 현재 사용자 인증 실패
    ```java
    protected void configure(HttpSecurity http) throws Exception {
          http.sessionManagement()
                  .maximumSessions(1)                         // 최대 허용 가능 세션 수 , -1 : 무제한 로그인 세션 허용
                  .maxSessionsPreventsLogin(true)             // 동시 로그인 차단함,  false : 기존 세션 만료(default)
                  .invalidSessionUrl("/invalid")              // 세션이 유효하지 않을 때 이동 할 페이지
                  .expiredUrl("/expired ")  	          // 세션이 만료된 경우 이동 할 페이지
    }
    ```
  - 세션 고정 보호
    - 배경
      - 공격자가 서버에 접속하여 얻어낸 쿠키를 사용자에게 심어두고, 사용자가 해당 세션쿠키로 로그인 인증을 시도하는 문제를 방지
    - 사용자가 로그인 인증을 할 때, 세션 ID를 변경하여 공격자와 사용자의 세션쿠키 정보가 달라지게 함.
    ```java
    protected void configure(HttpSecurity http) throws Exception {
          http.sessionManagement()
                  .sessionFixation().changeSessionId()      // 기본값
                                                            // none, migrateSession, newSession
    }
    ```
  - 세션 정책
    ```java
    protected void configure(HttpSecurity http) throws Exception {
          http.sessionManagement()
                  .sessionCreationPolicy(SessionCreationPolicy. If_Required )
    }
    
    // SessionCreationPolicy. Always 		:  스프링 시큐리티가 항상 세션 생성
    // SessionCreationPolicy. If_Required 	        :  스프링 시큐리티가 필요 시 생성(기본값)
    // SessionCreationPolicy. Never   		:  스프링 시큐리티가 생성하지 않지만 이미 존재하면 사용
    // SessionCreationPolicy. Stateless	 	:  스프링 시큐리티가 생성하지 않고 존재해도 사용하지 않음
    ```