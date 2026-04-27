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
- **解決 (Solution)**: `@RestControllerAdvice` を利用した `GlobalExceptionHandler` を実装し、すべての例外を `ErrorResponse` オブジェクトに統一。
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
src/main/java/com/community/platform/
├── admin/                      # 管理者機能ドメイン (관리자 기능 도메인)
│   ├── controller/
│   │   ├── AdminMainController.java # ダッシュボード統計およびユーザー管理 (대시보드 통계 및 유저 관리)
│   │   ├── AdminPostController.java # 投稿の参照および強制削除 (게시물 조회 및 강제 삭제)
│   │   └── AdminUserController.java # ユーザー状態・権限管理API (유저 상태 및 권한 관리 API)
│   ├── dto/
│   │   ├── AdminPostResponseDto.java # 管理者用投稿情報のレスポンスDTO (관리자용 게시물 정보 반환 DTO)
│   │   └── AdminUserResponseDto.java # 管理者用ユーザー情報のレスポンスDTO (관리자용 유저 정보 반환 DTO)
│   └── service/
│       ├── AdminPostService.java    # 投稿管理および統計データ集計 (게시물 관리 및 통계 데이터 집계)
│       └── AdminUserService.java    # 会員権限変更・状態管理および登録統計 (회원 권한 변경, 상태 관리 및 가입 통계)

├── comment/                    # コメントドメイン (댓글 도메인)
│   ├── controller/
│   │   └── CommentRestController.java # コメントの作成・参照・更新・削除API (댓글 작성·조회·수정·삭제 API)
│   ├── dto/
│   │   ├── CommentCreateDto.java   # コメント作成用DTO (댓글 작성용 입력 DTO)
│   │   ├── CommentResponseDto.java # コメント表示用レスポンスDTO (댓글 표시용 데이터 반환 객체)
│   │   └── CommentUpdateDto.java   # コメント更新用DTO (댓글 수정용 DTO)
│   ├── entity/
│   │   └── Comment.java            # コメントエンティティ (댓글 핵심 엔티티)
│   ├── repository/
│   │   └── CommentRepository.java  # @EntityGraphによるクエリ最適化 (EntityGraph를 통한 쿼리 최적화)
│   └── service/
│       └── CommentService.java     # コメント作成・削除および権限検証 (댓글 생성·삭제 및 권한 검증)

├── common/                      # 共通定義 (공통 데이터 및 규격)
│   ├── DataInit.java           # 開発環境用テストデータ自動生成 (개발 환경용 테스트 데이터 자동 생성)
│   ├── PostSearchType.java     # 投稿検索条件区分 (게시물 검색 조건 구분)
│   ├── PostSortType.java       # 投稿ソート順定義 (게시물 정렬 순서 정의)
│   └── UserStatus.java         # ユーザーアカウント状態定義 (유저 계정 상태 정의)

├── config/                     # 設定 (환경 설정)
│   └── LocaleConfig.java       # 多言語対応(i18n)設定 (다국어 대응 설정)

├── controller/                 # 共通コントローラー (공통 컨트롤러)
│   └── HomeController.java     # メインページルーティング (메인 페이지 라우팅)

├── email/                      # メール認証ドメイン (이메일 인증 도메인)
│   ├── controller/
│   │   └── EmailVerificationController.java # メール認証およびパスワード再設定処理 (메일 인증 및 비밀번호 재설정 처리)
│   ├── entity/
│   │   └── EmailVerificationToken.java # 認証トークン管理および有効期限定義 (인증 토큰 관리 및 유효 기간 정의)
│   ├── repository/
│   │   └── EmailVerificationTokenRepository.java # 認証トークン取得および既存データ削除 (인증 토큰 조회 및 기존 데이터 삭제)
│   └── service/
│       ├── EmailVerificationService.java # トークン検証およびアカウント有効化ロジック (토큰 검증 및 계정 활성화 로직)
│       ├── MailService.java         # メール送信インターフェース (메일 송신 기능 인터페이스)
│       ├── GmailServiceImpl.java    # 開発環境用(SMTP)メール送信実装 (개발 환경용 메일 송신 구현)
│       └── ResendMailServiceImpl.java # 本番環境用(外部API)メール送信実装 (운영 환경용 메일 송신 구현)

├── exception/                  # 例外処理 (예외 처리)
│   └── GlobalExceptionHandler.java # グローバル例外ハンドリングおよびエラーページ遷移 (전역 예외 핸들링)

├── post/                       # 投稿ドメイン (게시물 도메인)
│   ├── controller/
│   │   └── PostController.java      # 投稿作成・検索・ソート・いいね機能制御 (게시물 작성·검색·정렬·좋아요 기능 제어)
│   ├── dto/
│   │   ├── PostDetailDto.java       # 投稿詳細表示用DTO (게시물 상세 표시용 DTO)
│   │   └── PostEditDto.java         # 投稿編集用DTO (게시물 편집용 DTO)
│   ├── entity/
│   │   ├── Post.java                # 投稿エンティティおよびライフサイクル定義 (게시물 엔티티 및 라이프사이클 정의)
│   │   └── PostLike.java            # 投稿とユーザーの中間エンティティ (게시물과 유저의 중간 엔티티)
│   ├── repository/
│   │   ├── PostLikeRepository.java  # @EntityGraphによるN+1問題解決 (EntityGraph를 통한 N+1 문제 해결)
│   │   └── PostRepository.java      # キーワード検索および統計用クエリ定義 (키워드 검색 및 통계용 쿼리 정의)
│   └── service/
│       ├── PostLikeService.java     # いいね機能トグル(登録・解除)ロジック (좋아요 기능 토글 로직)
│       └── PostService.java         # 検索・ソート・ページングを含むビジネスロジック (검색·정렬 및 페이징 포함 비즈니스 로직)

├── security/                   # セキュリティ設定 (보안 설정)
│   ├── CustomAuthenticationFailureHandler.java # ログイン失敗時例外処理 (로그인 실패 시 예외 핸들링)
│   ├── CustomLoginSuccessHandler.java # ログイン成功後状態検証および後処理 (로그인 성공 후 상태 검증 및 후속 처리)
│   ├── CustomUserDetails.java       # Spring Securityユーザー情報カプセル化 (유저 정보 캡슐화)
│   ├── CustomUserDetailsService.java # DBユーザー情報取得および認証連携 (DB 유저 정보 취득 및 인증 연동)
│   └── SecurityConfig.java          # セキュリティポリシーおよびフィルタチェーン設定 (보안 정책 및 필터 체인 설정)

├── user/                       # ユーザードメイン (유저 도메인)
│   ├── controller/
│   │   ├── AccountController.java    # アカウント復旧およびパスワード再設定処理 (계정 복구 및 비밀번호 재설정)
│   │   ├── AuthController.java       # 会員登録処理 (회원가입 처리)
│   │   ├── LoginController.java      # ログインページルーティング (로그인 페이지 라우팅)
│   │   ├── MyPageController.java     # マイページ管理 (마이페이지 관리)
│   │   └── UserCheckController.java  # 重複チェックAPI (중복 체크 API)
│   ├── entity/
│   │   ├── Role.java                 # ユーザー権限定義 (유저 권한 정의)
│   │   └── User.java                 # ユーザーエンティティおよび状態管理 (유저 엔티티 및 상태 관리)
│   ├── repository/
│   │   └── UserRepository.java       # 各種検索条件および統計用クエリ定義 (각종 검색 조건 및 통계용 쿼리 정의)
│   └── service/
│       └── UserService.java         # 会員登録・認証・パスワード再設定等コアロジック (회원가입·인증·비밀번호 재설정 핵심 로직)
