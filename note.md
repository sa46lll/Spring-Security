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
- **세션**
  - 동시 세션 제어
    - 동일한 계정으로 인증 받을때 생성되는 세션의 허용갯수가 초과하지 않고 유지하는 것
    - 제어 방법
      - 이전 사용자 세션 만료
      - 현재 사용자 인증 실패
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
- **세션 제어 필터**
  - SessionManagementFilter
    - 핵심기능
      - 세션관리
        - 인증 시 사용자의 세션정보를 등록,조회,삭제 등의 세션 이력을 관리
      - 동시적 세션 제어
        - 동일 계정으로 접속이 허용되는 최대 세션수를 제한
      - 세션 고정 보호
        - 인증 할 때마다 세션쿠키를 새로 발급하여 공격자의 쿠키 조작을 방지
      - 세션 생성 정책
        - Always,if_required,Never,Stateless
  - ConcurrentSessionFilter
    - 매 요청마다 현재 사용자의 세션 만료 체크 여부
    - 세션이 만료되었을 경우 즉시 만료 처리
    - `Session.isExpired() == true` // 로그아웃 처리, 즉시 오류 페이지 응답
  
  _SessionManagementFilter, ConcurrentSessionFilter 두개가 동시적 세션 제어를 처리하게 된다_
- 권한 설정
  - 선언적 방식
    - URL
      - http.antMatchers("/users/**").hasRole("USER")
      ```java
      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http
              .antMatcher("/shop/**")                                    // 설정된 보안기능이 작동하는 특정 url
              .authorizeRequests()
              .antMatchers("/shop/login", "/shop/users/**").permitAll()  // 아래의 조건에 하나라도 포함되지 않으면 접근 불가
              .antMatchers("/shop/mypage").hasRole("USER")
              .antMatchers("/shop/admin/pay").access("hasRole('ADMIN')");
              .antMatchers("/shop/admin/**").access("hasRole('ADMIN') or hasRole(‘SYS ')");
              .anyRequest().authenticated()

              // 설정 시 구체적인 경로가 먼저오고 큰 범위의 경로가 뒤에 오게 해야 함.
              // ex) "/shop/admin/pay"가 "/shop/admin/**"보다 먼저 와야 함.
              // 그 외 인가 API 표현식 참고
      }
      ```
    - Method
      - @PreAuthorize("hasRole('USER')")
        public void user(){ System.out.println("user"))
  - 동적 방식 - DB 연동 프로그래밍
  
> 인증/인가 API
- FilterSecurityInterceptor
  - 인증 예외를 발생시키는 필터 (맨 마지막에 위치)
- ExceptionTranslationFilter
  - AuthenticationException
    - 인증 예외 처리
      1. AuthenticationEntryPoint 호출
          - 로그인 페이지 이동, 401 오류 코드 전달 등
      2. 인증 예외가 발생하기 전의 요청정보를 저장
          - RequestCache
            - 사용자의 이전 요청 정보를 세션에 저장하고 이를 꺼내오는 캐시 메카니즘
            - SavedRequest
              - 사용자가 요청앴던 request 파라미터 값들, 그 당시의 헤더값들 등이 저장
        
      _# 요청정보는 SavedRequest에 저장되고, SavedRequest를 RequestCache가 세션에 저장_
  - AccessDeniedException
    - 인가 예외 처리
      - AccessDeniedHandler에서 예외 처리하도록 제공
  ```java
  protected void configure(HttpSecurity http) throws Exception {
	    http.exceptionHandling() 					
                  .authenticationEntryPoint(authenticationEntryPoint())     // 인증실패 시 처리
                  .accessDeniedHandler(accessDeniedHandler()) 		  // 인증실패 시 처리
  }
  ```
- Form 인증
  - CSRF (사이트 간 요청 위조)
    - 유저가 자신의 의지와는 무관하게 공격자가 의도한 행위(등록, 수정, 삭제 등)를 특정 웹사이트에 요청하도록 만드는 공격
    - 방지 방법
      - CsrfFilter
        - 모든 요청에 랜덤하게 생성된 토큰을 HTTP 파라미터로 요구
        - 요청 시 전달되는 토큰 값과 서버에 저장된 실제 값과 비교한 후 일치하지 않으면 요청 실패
        - Client
          - <input type="hidden" name="${_csrf.paramterName}"value="${_csrf.token}"/>
          - HTTP METHOD : PATCH, POST, PUT, DELETE
        - Spring Security
          - http.csrf() : 기본 활성화 되어있음
          - http.csrf().disabled(): 비활성화
  
### 스프링 시큐리티 주요 아키텍처
> 위임 필터 및 필터 빈 초기화
- ServletFilter
  - 요청을 보내면 서블릿 컨테이너가 받게 된다.
  - 서블릿 필터는 스프링에서 정의된 빈을 주입해서 사용할 수 없기 때문에..
    - DelegatingFilterProxy를 통해 서블릿 컨테이너에서 필터로써 요청을 취득하고, 스프링 컨테이너에 존재하는 특정 빈(name=springSecurityFilterChain)을 찾아 요청을 위임함.
  - DelegatingFilterProxy
    - 특정한 이름을 가진 스프링 빈을 찾아 그 빈에게 요청을 위임하는 ServletFilter
      - springSecurityFilterChain 이름으로 생성된 빈을 ApplicationContext에서 찾아 요청을 위임
      - 실제 보안처리를 하지 않음
  - FilterChainProxy
    - springSecurityFilterChain 이름으로 생성되는 필터 Bean
    - DelegatingFilterProxy로부터 요청을 위임받고 스프링 시큐리티 초기화 시 생성되는 필터들을 관리하고 제어
      - 스프링 시큐리티가 기본적으로 생성하는 필터
      - 설정 클래스에서 API 추가 시 생성되는 필터
    - 사용자의 요청을 필터 순서대로 호출하여 전달
    - 사용자정의 필터를 생성해서 기존의 필터 전,후로 추가 가능
      - 필터의 순서를 잘 정의
    - 마지막 필터까지 인증 및 인가 예외가 발생하지 않으면 보안 통과
> 필터 초기화, 다중 보안 설정
- 다중 보안 설정
  - 설정 클래스 별로 보안 기능이 각각 작동
    - 설정 클래스 별로 RequestMatcher 설정
    - 설정 클래스 별로 필터가 생성된다.
    - 다중 설정 클래스를 설정 할 경우, @Order 어노테이션으로 우선순위 설정
    - Filter들과 RequestMatcher를 가진 SecurityFilterChain 객체가 각각의 보안 설정에 따라 생성
      - 객체들은 FilterChainProxy 빈에서 SecurityChains 리스트 멤버 변수로 관리
> Authentication
- Authentication
  - 누구인지 증명하는 것
    - 사용자의 인증 정보를 저장하는 토큰 개념
      - 인증 시 id와 password를 담고 인증 검증을 위해 전달되어 사용
      - 인증 후 최종 인증 결과(user 객체, 권한정보)를 담고 SecurityContext에 저장되어 전역적으로 참조가 가능
        - `Authentication authentication = SecurityContextHolder.getContext().getAuthentication();`
    - 구조
      - principal
        - 사용자 아이디 혹은 User 객체를 저장
      - credentials
        - 사용자 비밀번호
      - authorities
        - 인증된 사용자의 권한 목록
      - details
        - 인증 부가 정보
      - Authenticated
        - 인증 여부
- SecurityContext
  - Authentication 객체가 저장되는 보관소, 필요 시 언제든지 Authentication 객체를 꺼내 쓸 수 있도록 제공되는 클래스
  - ThreadLocal에 저장되어 아무 곳에서나 참조가 가능하도록 설계함.
  - 인증이 완료되면 HttpSession에 저장되어 어플리케이션 전반에 걸쳐 전역적인 참조가 가능
- SecurityContextHolder
  - SecurityContext 객체 저장 방식
    - MODE_THREADLOCAL
      - 스레드당 SecurityContext 객체를 할당, 기본값
    - MODE_INHERITABLETHREADLOCAL
      - 메인 스레드와 자식 스레드에 관하여 동일한 SecurityContext 유지
    - MODE_GLOBAL
      - 응용 프로그램에서 단 하나의 SecurityContext 저장
  - SecurityContextHolder.clearContext()
    - SecurityContext 기존 정보 초기화
  - `Authentication authentication = SecurityContextHolder.getContext().getAuthentication();`
- SpringContextPersistenceFilter
  - SecurityContext 객체의 생성, 저장, 조회
    - 익명 사용자
      - 새로운 SecurityContext 객체를 생성하여 SecurityContextHolder에 저장
      - AnonymousAuthenticationFilter에서 AnonymousAuthenticationToken객체를 SecurityContext에 저장
    - 인증 시
      - 새로운 SecurityContext 객체를 생성하여 SecurityContextHolder에 저장
      - UsernamePasswordAuthenticationFilter에서 인증 성공 후 SecurityContext에 UsernamePasswordAuthentication 토큰 객체를 SecurityContext에 저장
      - 인증이 완료되면 Session에 SecurityContext를 저장
    - 인증 후
      - Session에서 SecurityContext를 꺼내 SecurityContextHolder에 저장
      - SecurityContext 안에 Authentication 객체가 존재하면 인증 유지
    - 최종 응답 시 공통
      - `SecurityContextHolder.clearContext();`
> Authentication Flow
- Authentication Flow
  1. Client
      - 로그인 요청
  2. UsernamePasswordAuthenticationFilter
      - id+pw를 담은 인증 전 토큰 객체 Authentication 생성
      - `authenticate(Authentication)`
  3. AuthenticationManager
      - 인증의 전반적인 관리
      - 실제 인증 역할을 하지 않고 적절한 AuthenticationProvider에 위임
      - `authenticate(Authentication)`
  4. AuthenticationProvider
      - 실제 인증 처리 역할
      - 유저 유효성 검증(패스워드 체크 등)
      - `loadUserByUsername(username)`
  5. UserDetailsService
      - 유저 객체 조회
      - UserDetails 타입으로 반환
      - `findById()`
  6. Repository
      - User 객체를 역순차적으로 전달
> Authorization
- Authorization
  - 스프링 시큐리티가 지원하는 권한 계층
    - 웹 계층
      - URL 요청에 따른 메뉴 혹은 화면 단위의 레벨 보안
    - 서비스 계층
      - 화면 단위가 아닌 메소드 같은 기능 단위의 레벨 보안
    - 도메인 계층
      - 객체 단위의 레벨 보안
- FilterSecurityInterceptor
  - 스프링 Security의 가장 마지막에 위치한 필터로, 인증된 사용자에 대하여 특정 요청의 승인/거부를 최종적으로 결정 
  - 인증 객체 없이 보호자원에 접근을 시도할 경우 AuthenticationException을 발생
  - 인증 여부는 SecurityContext에 인증 객체 유무로 판단
  - 인증 후 자원에 접근 가능한 권한이 존재하지 않을 경우 AccessDeniedException을 발생 (인가 예외)
  - 권한 제어 방식 중 HTTP 자원의 보안을 처리하는 필터
  - 권한 처리를 AccessDecisionManager에게 위임
- AccessDecisionManager
  - 인증정보, 요청정보, 권한정보를 이용해서 사용자의 자원접근을 허용할 것인지 거부할 것인지를 최종 결정하는 주체
  - 여러 Voter들을 가질 수 있으며 Voter들로부터 접근허용, 거부, 보류에 해당하는 각각의 값을 리턴받고 판단 및 결정
  - 최종 접근 거부 시 예외 발생
  - 접근 결정의 세 가지 유형
    - AffirmativeBased
      - 여러 개의 Voter 클래스 중 하나라도 접근 허가로 결론을 내면 접근 허가로 판단한다.
    - ConsensusBased
      - 다수표(승인 및 거부)에 의해 최정 결정을 판단
      - 동수일 경우 기본은 접근허가하나 allowIfEqualGrantedDeniedDecisions를 false로 설정할 경우 접근 거부로 결정
    - UnanimousBased
      - 모든 Voter가 만장일치로 접근을 승인해야하며 그렇지 않은 경우 접근을 거부한다.
- AccessDecisionVoter
  - 판단을 심사하는 것(위원)
  - Voter가 권한 부여 과정에서 판단하는 자료
    - Authentication
      - 인증 정보(user)
    - FilterInvocation 
      - 요청 정보(antMatcher("/user"))
    - ConfigAttributes 
      - 권한 정보(hasRole("USER"))
    - 결정 방식
      - ACCESS_GRANTED: 접근 허용(1)
      - ACCESS_DENIED: 접근 거부(-1)
      - ACCESS_ABSTAIN: 접근 보류(0)
        - Voter가 해당 타입의 요청에 대해 결정을 내릴 수 없는 경우