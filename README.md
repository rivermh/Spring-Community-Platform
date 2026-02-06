# 🚀 Spring Boot Community Platform

> **정교한 권한 관리와 성능 최적화가 반영된 커뮤니티 프로젝트**
> 본 프로젝트는 사용자 인증(Security), 이메일 연동, 게시판 기능 및 마이페이지 활동 추적을 포함한 풀스택 웹 애플리케이션입니다.

---

## 🛠 Tech Stack

| Category | Tech Stack |
| :--- | :--- |
| **Framework** | **Spring Boot 3.x**, Spring Security |
| **Persistence** | **Spring Data JPA**, H2 / MySQL |
| **Template** | **Thymeleaf**, JavaScript (Fetch API) |
| **Library** | **Lombok**, Java Mail Sender |
| **Design** | **Bootstrap 5**, CSS3 |

---

## ✨ 핵심 기능 (Key Features)

### 🔐 인증 및 보안 (Authentication & Security)
* **상태 기반 유저 관리**: `ACTIVE`, `INACTIVE`(미인증), `WITHDRAWN`(탈퇴) 상태별로 로그인 접근 권한을 세밀하게 제어합니다.
* **이메일 승인 시스템**: 회원가입 시 `UUID` 토큰을 발송하여 메일 인증을 마친 사용자만 서비스 이용이 가능하도록 구현했습니다.
* **세션 보안 제어**: 탈퇴 유저가 로그인 시도 시, 인증 성공 직후 핸들러에서 상태를 체크하여 즉시 세션을 파기(`Session Invalidate`)하는 강력한 보안 정책을 적용했습니다.

### 📝 게시판 및 인터랙션 (Post & Interaction)
* **게시물 관리**: 페이징 처리, 제목/내용/작성자 기반 검색 필터링 및 동적 정렬 기능을 지원합니다.
* **좋아요 시스템**: DB 유니크 제약 조건을 활용한 중복 방지 및 비동기 토글 비즈니스 로직을 구현했습니다.
* **댓글 시스템**: 게시글 상세 페이지 내 계층적 구조를 고려한 댓글 관리 기능을 제공합니다.

### 👤 마이페이지 및 편의성 (MyPage & UX)
* **활동 추적**: 내가 쓴 글, 내가 쓴 댓글, 내가 좋아요 한 글을 모아볼 수 있는 통합 대시보드를 제공합니다.
* **계정 복구**: 탈퇴한 유저가 본인 인증을 거쳐 다시 계정을 활성화할 수 있는 셀프 복구 프로세스를 구축했습니다.

---

## 🚀 기술적 해결 및 최적화 (Troubleshooting)

### 1️⃣ JPA N+1 문제 해결 (`@EntityGraph`)
게시글 목록 조회 시 작성자(User) 정보를 가져오기 위해 게시글 수만큼 추가 쿼리가 발생하는 성능 저하 문제를 확인했습니다.
* **해결**: Repository 레이어에 `@EntityGraph`를 적용하여 `Join Fetch` 방식으로 단 한 번의 쿼리로 연관 데이터를 조회하도록 최적화했습니다.



### 2️⃣ 보안 핸들러 커스터마이징 (UX & Security)
로그인 실패 시 사용자에게 단순 실패 메시지가 아닌, 구체적인 사유(비밀번호 오류, 탈퇴 계정, 미인증 등)를 안내하여 UX를 개선했습니다. 또한, 성공 핸들러에서 부적절한 상태의 계정을 걸러내는 2중 보안을 적용했습니다.



### 3️⃣ 도메인 중심 설계 (DDD 기반 엔티티)
Setter를 지양하고 엔티티 내부에서 `withdraw()`, `restore()`, `activate()` 등의 비즈니스 메서드를 직접 관리하게 함으로써, 서비스 레이어의 복잡도를 낮추고 데이터 무결성을 높였습니다.

---

## 📂 Project Structure

```text
com.example.demo
├── common/         # 공통 Enum (UserStatus, Role, PostSearchType)
├── config/         # Security, Web Config
├── email/          # 인증 메일 발송 및 토큰 관리 로직
├── post/           # 게시글 & 좋아요 (Entity, Repository, Service, Controller)
├── comment/        # 댓글 도메인 (Entity, Service, Controller)
├── user/           # 사용자 도메인 & 마이페이지 관리
└── exception/      # 전역 예외 처리 (GlobalExceptionHandler)
