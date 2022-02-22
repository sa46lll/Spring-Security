# Spring-Security

> 보안 정책 설정
1. 자원 및 권한 설정
    - 마이페이지
        - 자원 설정 - /mypage
        - 권한 매핑 - ROLE_USER
    - 메시지
        - 자원 설정 - /message
        - 권한 매핑 - ROLE_MANAGER
    - 환경설정
        - 자원 설정 - /config
        - 권한 매핑 - ROLE_ADMIN
    - 관리자
        - 자원 설정 - /admin/**
        - 권한 매핑 - ROLE_ADMIN
2. 사용자 등록 및 권한부여
3. 권한계층적용
    - ROLE_ADMIN > ROLE_MANAGER > ROLE_USER
4. 메소드 보안 설정
    - 메소드 보안 - 서비스 계층 메소드 접근 제어
    - 포인트컷 보안 - 포인트컷 표현식에 따른 메소드 접근 제어
5. IP 제한하기

> 강의 내용
- Spring Security의 보안 설정 API와 이와 연계된 각 Filter
    - 각 API 개념, 사용법, 처리과정, 동작방식
    - API 설정 시 생성 및 초기화되어 사용자의 요청을 처리하는 Filter
- Spring Security 내부 아키텍처와 각 객체의 역할 및 처리과정
    - 초기화, 인증, 인가 과정 등을 아키텍처적 관점에서..
- 프로젝트
    - 인증 기능 구현 -> Form 방식, Ajax 인증 처리
    - 인가 기능 구현 -> DB와 연동해서 권한 제어 시스템
        - url, method 방식
> Study Note

