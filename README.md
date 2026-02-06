# Spring Boot JPA Board Project

Spring Boot와 JPA를 활용한 게시판 기반 웹 애플리케이션입니다.  
게시글(Post) 도메인을 중심으로 CRUD, 검색, 페이징 기능을 구현했습니다.

## Tech Stack
- Java 17
- Spring Boot
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven

## Core Features (Post)
- 게시글 CRUD
- 검색 / 정렬 (ENUM 기반)
- 페이징 처리
- 작성자 권한 검증
- 좋아요 기능

## Design Highlights
- 도메인 중심 패키지 구조
- Service 계층에서 비즈니스 로직 처리
- DTO 분리로 엔티티 직접 노출 방지
- JPA Dirty Checking 활용

## Additional Features
- 댓글 CRUD (REST API)
- Spring Security 기반 인증
- 이메일 인증
