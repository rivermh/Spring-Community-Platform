# 🚀 Community Project: Spring Boot 기반 커뮤니티 플랫폼

> **Spring Security와 이메일 인증을 활용한 견고하고 안전한 커뮤니티 시스템**

본 프로젝트는 단순한 게시판 기능을 넘어, 실무 수준의 사용자 인증 흐름과 보안 가이드라인을 준수하여 설계된 웹 애플리케이션입니다. 사용자 계정의 상태 관리(Active/Inactive/Withdrawn)와 효율적인 데이터 검색/정렬 로직을 포함하고 있습니다.

---

## 🛠 Tech Stack

- **Framework:** Java 17, Spring Boot 3.x
- **Security:** Spring Security, BCrypt Password Encoder
- **Data JPA:** Spring Data JPA, Hibernate, Query Method
- **Validation:** Hibernate Validator, Regular Expression(Regex)
- **Email:** Java Mail Sender (SMTP), UUID Verification Token
- **Lombok:** Boilerplate Code Reduction

---

## ✨ Key Features

### 🔐 1. Robust Authentication & Security
- **이메일 인증 시스템:** 회원가입 시 `UUID` 기반 토큰을 발급하여 이메일 인증을 완료해야만 계정이 활성화(`ACTIVE`)되는 프로세스를 구현했습니다.
- **Custom Security Handler:** 로그인 성공/실패 시 커스텀 핸들러(`successHandler`, `failureHandler`)를 통해 구체적인 에러 메시지 처리 및 사용자 흐름을 제어합니다.
- **계정 상태 관리:** 유저의 상태를 `ACTIVE`, `INACTIVE`(미인증), `WITHDRAWN`(탈퇴)으로 세분화하여 관리합니다.
- **Soft Delete & Recovery:** 탈퇴 시 데이터를 즉시 삭제하지 않고 상태를 변경하며, 비밀번호 재확인을 통한 계정 복구 기능을 제공합니다.

### 📝 2. Dynamic Board & Search System
- **전략적 검색/정렬:** `Enum` 타입을 활용하여 제목, 내용, 작성자 기반의 검색을 지원하며 최신순/오래된순/제목순 정렬 기능을 구현했습니다.
- **Paging:** Spring Data JPA의 `Pageable`을 활용하여 대량 데이터 조회 시 성능과 가동성을 최적화했습니다.
- **좋아요(Likes) 시스템:** 게시글에 대한 유저별 좋아요 토글 기능을 제공하며, 상세 조회 시 카운트를 포함합니다.

### 🏗 3. Clean Architecture & Logic
- **Domain Model Pattern:** 엔티티 내부에서 비즈니스 로직(수정, 탈퇴 등)을 처리하고 JPA의 **Dirty Checking**을 활용하여 데이터 일관성을 유지합니다.
- **DTO Layering:** 엔티티를 외부로 직접 노출하지 않고 `PostDetailDto`, `PostEditDto` 등을 사용하여 보안성과 API 유연성을 확보했습니다.
- **Global Exception Handling:** `@ControllerAdvice`를 통해 403, 404 등 발생 가능한 예외를 전역적으로 관리하여 일관된 피드백을 제공합니다.

---

## 📂 Project Structure

```text
com.example.demo
├── common          # 공통 Enum (PostSearchType, PostSortType, UserStatus)
├── security        # SecurityConfig 및 커스텀 핸들러 (Success/Failure)
├── user            # 회원 가입, 중복 체크, 계정 복구 및 마이페이지 로직
├── post            # 게시글 작성, 페이징 조회, 검색, 수정, 삭제, 좋아요
├── comment         # 댓글 CRUD (REST API)
├── email           # 이메일 발송 및 인증 토큰 관리 엔티티/서비스
└── exception       # GlobalExceptionHandler 기반 예외 처리
