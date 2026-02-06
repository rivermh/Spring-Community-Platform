# [Project] Spring Boot Community Platform

<div align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java" alt="Java 17">
  <img src="https://img.shields.io/badge/SpringBoot-3.2.5-green?style=flat-square&logo=springboot" alt="SpringBoot 3.2.5">
  <img src="https://img.shields.io/badge/SpringSecurity-6.x-blueviolet?style=flat-square&logo=springsecurity" alt="SpringSecurity">
  <img src="https://img.shields.io/badge/JPA-Hibernate-red?style=flat-square&logo=hibernate" alt="JPA">
</div>

---

## ■ Language Selector
- [日本語 (Japanese)](#-jp-プロジェクト概要)
- [한국어 (Korean)](#-ko-프로젝트-개요)

---

## 🇯🇵 [JP] プロジェクト概要

### □ 開発背景
実務で利用されるシステムを想定し、「堅牢な認証システム」と「データベース性能の最適化」を重点的に開発しました。ユーザーの状態遷移（認証前、有効、退会済）に伴うアクセス制御と、JPAによる効率的なデータ照会を実装しています。

### □ 技術スタック (Tech Stack)
- **Backend**: Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **Database**: H2 / MySQL
- **Frontend**: Thymeleaf, JavaScript (Fetch API), Bootstrap 5
- **Library**: Lombok, Java Mail Sender

### □ 主な機能 (Core Features)
1. **ユーザー状態管理**: ACTIVE, INACTIVE(未認証), WITHDRAWN(退会済)のステータスによるアクセス制御
2. **多重ログイン検証**: Spring Securityを拡張し、認証成功後もユーザー状態を確認するフローを実装
3. **メール認証**: UUIDトークンによる本人確認を行い、有効なアカウントのみを活性化
4. **アカウント復旧**: 退会状態のユーザーがID/PW認証を経て自ら復旧できる機能
5. **マイページ**: 投稿、コメント、いいね一覧の管理機能

---

## 🇰🇷 [KO] 프로젝트 개요

### □ 개발 배경
실제 서비스에서 요구되는 보안과 성능 최적화를 목표로 설계한 프로젝트입니다. 사용자의 복잡한 상태 변화(인증 전, 활성, 탈퇴)에 따른 접근 제어를 세밀하게 관리하고, JPA 사용 시 발생할 수 있는 성능 저하를 해결하는 데 주력했습니다.

### □ 핵심 기능 (Core Features)
1. **유저 상태 관리**: ACTIVE, INACTIVE(미인증), WITHDRAWN(탈퇴) 상태값을 통한 서비스 접근 제어
2. **커스텀 보안 핸들러**: 로그인 성공 후에도 유저 상태를 재검증하여 부적합한 접근을 차단하는 로직 구현
3. **이메일 인증**: UUID 토큰을 이용한 본인 확인 및 계정 활성화 프로세스 구축
4. **계정 복구 시스템**: 탈퇴한 유저가 본인 확인(ID/PW)을 통해 계정을 다시 활성화할 수 있는 기능
5. **활동 관리**: 마이페이지를 통해 본인이 작성한 게시글, 댓글, 좋아요 내역 확인

---

## ■ 技術的解決 (Technical Problem Solving)

### 01. ユーザーライフサイクルの管理 (유저 생명주기 관리)
- **課題 (Problem)**: ユーザーが退会しても統計データ保持のために物理削除は避けたいが、退会済みユーザーの再活動は完全に防ぐ必要がある。
- **解決 (Solution)**: `UserStatus` (ACTIVE, INACTIVE, WITHDRAWN) を定義し、論理削除(Soft Delete)を採用。認証成功直後に `SuccessHandler` でステータスをチェックし、不適切な場合はセッションを無効化。
- **結果 (Result)**: データの整合性を保ちつつ、退会アカウントによる不正利用を完全に遮断。

### 02. JPA N+1 問題の改善 (N+1 문제 해결)
- **課題 (Problem)**: 投稿一覧（Post）の表示時、作成者（User）情報を取得するために投稿数(N)だけ追加クエリが発生し、レスポンス速度が低下。
- **解決 (Solution)**: Repositoryレイヤーで `@EntityGraph` を活用し、`Fetch Join` を実行。
- **結果 (Result)**: クエリ発行回数を **101回 → 1回** (100件基準) に削減。DB負荷を劇的に改善。

### 03. 共通例外ハンドリング (공통 예외 처리)
- **課題 (Problem)**: 各コントローラーでの例外処理の重複と、一貫性のないエラーレスポンス形式によるフロントエンドの開発効率低下。
- **解決 (Solution)**: `@RestControllerAdvice` を利用した `GlobalExceptionHandler` を実装し、すべての例外を `ErrorResponse` オブ젝트に統一。
- **結果 (Result)**: エラーハンドリングの構造化により、開発生産性と保守性が向上。

---

## ■ 主要API仕様 (Main API Specification)

| Method | Path | Description (KR) | 説明 (JP) |
| :--- | :---: | :--- | :--- |
| `POST` | `/auth/register` | 회원가입 및 인증 메일 발송 | 会員登録及び認証メール送信 |
| `GET` | `/auth/verify` | 이메일 토큰 인증 처리 | メールトークン認証処理 |
| `POST` | `/user/restore` | 탈퇴 유저 계정 자가 복구 | 退会ユーザーのアカウント自己復旧 |
| `GET` | `/post/list` | 게시글 페이징 및 검색 조회 | 投稿一覧の照会（検索・ページング） |
| `POST` | `/post/like/{id}` | 좋아요 토글 처리 | いいねの切り替え（Toggle） |

---

## ■ Database Schema (ERD)

- **User**: 全ての活動の主軸。状態(`status`)と権限(`role`)を管理。
- **Post**: 投稿データ。Userと1:Nの関係。
- **Comment**: 投稿に対する返信。UserおよびPostと関連付け。
- **PostLike**: 重複防止のため、UserとPostの組み合わせにユニーク制約を適用。

---

## ■ Project Structure (한일 병행 상세)

```text
src/main/java/com/example/demo/
├── common/                     # 共通データ及び規格 (공통 데이터 및 규격)
│   ├── UserStatus.java         # ユーザー状態 (유저 상태: ACTIVE, INACTIVE, WITHDRAWN)
│   └── BaseTimeEntity.java     # 作成/修正時間の共通管理 (공통 생성/수정 시간 관리)
├── config/                     # システム設定 (시스템 설정)
│   └── SecurityConfig.java     # Spring Security設定及びパスセキュリティ (보안 설정)
├── exception/                  # 例외 처리 (예외 처리)
│   ├── GlobalExceptionHandler.java # 全域例外ハンドリング (전역 예외 처리)
│   └── ErrorResponse.java      # 共通エラーレスポンス規格 (공통 에러 응답 규격)
├── security/                   # 認証・認可のロジック (인증 및 인가 로직)
│   ├── CustomUserDetails.java  # Security専用ユーザーオブジェクト (보안 전용 유저 객체)
│   ├── CustomUserDetailsService.java # DB連動認証サービス (DB 연동 인증 서비스)
│   ├── CustomLoginSuccessHandler.java # ログイン成功時の状態検証 (로그인 성공 핸들러)
│   └── CustomLoginFailureHandler.java # ログイン失敗時の事由別ハンドリング (로그인 실패 핸들러)
├── email/                      # メール認証ドメイン (이메일 인증 도메인)
│   ├── entity/
│   │   └── EmailVerificationToken.java # 認証トークンエンティティ (인증 토큰 엔티티)
│   ├── repository/
│   │   └── EmailVerificationTokenRepository.java
│   └── service/
│       └── MailService.java    # SMTPを利用したメール送信 (메일 발송 서비스)
├── post/                       # 投稿及びいいねドメイン (게시글 및 좋아요 도메인)
│   ├── controller/
│   │   └── PostController.java
│   ├── entity/
│   │   ├── Post.java
│   │   └── PostLike.java       # 投稿とユーザーのN:M連結 (게시물-유저 연결 엔티티)
│   ├── repository/
│   │   ├── PostRepository.java # @EntityGraphによる最適化 (Fetch Join 적용)
│   │   └── PostLikeRepository.java
│   └── service/
│       ├── PostService.java
│       └── PostLikeService.java
├── comment/                    # コメントドメイン (댓글 도메인)
│   ├── controller/
│   │   └── CommentController.java
│   ├── entity/
│   │   └── Comment.java
│   ├── repository/
│   │   └── CommentRepository.java
│   └── service/
│       └── CommentService.java
└── user/                       # ユーザー及びマイページ (유저 및 마이페이지)
    ├── controller/
    │   ├── AccountController.java  # アカウント復旧及び認証管理 (계정 복구 및 인증)
    │   ├── AuthController.java     # 会員登録及びログインページ (회원가입 및 로그인)
    │   ├── MyPageController.java   # 活動履歴の照会及び退会 (활동 조회 및 탈퇴)
    │   └── UserCheckController.java # 重複チェック(REST API) (중복 체크)
    ├── entity/
    │   ├── User.java               # ユーザー基幹エンティティ (유저 핵심 엔티티)
    │   └── Role.java               # 権限区分 (권한 구분: USER, ADMIN)
    ├── repository/
    │   └── UserRepository.java
    └── service/
        └── UserService.java        # 会員登録、退会、復旧ロジック (회원 관리 로직)
