# 🚀 Spring Boot Community Platform (コミュニティプラットフォーム)

<div align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java" alt="Java 17">
  <img src="https://img.shields.io/badge/SpringBoot-3.2.5-green?style=flat-square&logo=springboot" alt="SpringBoot 3.2.5">
  <img src="https://img.shields.io/badge/SpringSecurity-6.x-blueviolet?style=flat-square&logo=springsecurity" alt="SpringSecurity">
  <img src="https://img.shields.io/badge/JPA-Hibernate-red?style=flat-square&logo=hibernate" alt="JPA">
</div>

---

## 🌏 Language Selector
- [日本語 (Japanese)](#-jp-プロジェクト概要)
- [한국어 (Korean)](#-ko-프로젝트-개요)

---

## 🇯🇵 [JP] プロジェクト概要

### 📋 開発背景
本プロジェクトは、実무で利用されるシステムを想定し、「堅牢な認証システム」と「データベース性能の最適化」に焦点を当てて開発しました。特にユーザーの状態遷移（認証前、有効、退会済）に伴うアクセス制御と、JPAによる効率的なデータ照会を重点的に実装しています。

### 🛠 技術スタック (Tech Stack)
- **Backend**: Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **Database**: H2 / MySQL
- **Frontend**: Thymeleaf, JavaScript (Fetch API), Bootstrap 5
- **Library**: Lombok, Java Mail Sender

### ✨ 主な機能 (Core Features)
1. **ユーザー状態管理**: `ACTIVE`, `INACTIVE`(未認証), `WITHDRAWN`(退会済)のステータスによるアクセス制御。
2. **多重ログイン検証**: Spring SecurityのHandlerを拡張し、認証成功後もユーザー状態を確認するフローを実装。
3. **メール認証**: UUIDトークンによる本人確認を行い、有効なアカウントのみを活性化。
4. **アカウント復旧**: 退会状態のユーザーがID/PW認証を経て自ら復旧できる機能を提供。
5. **マイページ**: 自分が作成した投稿、コメント、いいね一覧の管理機能。

---

## 🇰🇷 [KO] 프로젝트 개요

### 📋 개발 배경
실제 서비스에서 요구되는 보안과 성능 최적화를 목표로 설계한 프로젝트입니다. 사용자의 복잡한 상태 변화(인증 전, 활성, 탈퇴)에 따른 접근 제어를 세밀하게 관리하고, JPA 사용 시 발생할 수 있는 성능 저하를 기술적으로 해결하는 데 주력했습니다.

### ✨ 핵심 기능 (Core Features)
1. **유저 상태 관리**: 유저의 가입 및 탈퇴 여부를 상태값으로 관리하여 비즈니스 로직에 반영.
2. **커스텀 보안 핸들러**: 로그인 성공/실패 시 상태별로 다른 안내 및 세션 제어 적용.
3. **이메일 인증**: 실제 메일 서버 연동을 통한 비동기 계정 인증 프로세스 구축.
4. **계정 복구 시스템**: 탈퇴한 유저가 본인 확인을 통해 계정을 다시 활성화할 수 있는 기능.
5. **활동 관리**: 마이페이지를 통해 본인이 작성한 게시글 및 참여도를 한눈에 파악 가능.

---

## 🚀 技術的解決 (Technical Problem Solving)

### 1️⃣ JPA N+1 問題の解決 (N+1 문제 해결)
- **課題**: 投稿一覧の照会時、作成者(User)情報を取得するために投稿数と同じ数の追加クエリが発生。
- **解決**: Repositoryに `@EntityGraph` を適用。`Fetch Join` を使用することで、1回のクエリで関連データを一括取得するように最適化。
- **結果**: クエリ発行数を大幅に削減し、データ照会速度を向上。



### 2️⃣ セキュリティハンドラーによる例外処理 (보안 핸들러 처리)
- **課題**: ログイン失敗時、理由（パスワード不一致、退会済、未認証）をユーザーが特定できず利便性が低下。
- **解決**: `AuthenticationFailureHandler` を拡張し、例外の種類に応じたエラーメッセージを返却。また、`SuccessHandler` で退会アカウントのセッションを即時破棄する論理的な遮단を適用。



### 3️⃣ オブジェクト指向に基づいたドメイン設計 (객체지향 도메인 설계)
- **課題**: サービス層にロジックが集中し、コードの保守性が低下。
- **解決**: エンティティ(`User`, `Post`)内部に `withdraw()`, `activate()` 等のビジネスメソッドを配置し、オブジェクト自身が状態管理の責任を持つように設計。

---

## 📊 Database Schema (ERD)



- **User**: 全ての活動の主軸となるデータ。
- **Post**: 投稿データ。Userと1:Nの関係。
- **Comment**: 投稿に対する返信。UserとPost両方に紐付く。
- **PostLike**: 重複防止のため、UserとPostの組み合わせにユニーク制約を適用。

---

## 📂 Project Structure

```text
com.example.demo
├── common/         # 共通Enum (UserStatus, Role)
├── config/         # Security, Web設定クラス
├── email/          # 非同期メール送信 & 認証トークンロジック
├── post/           # 投稿・いいねドメイン
├── comment/        # コメントドメイン
├── user/           # ユーザー管理、マイページ、アカウント復旧
└── exception/      # GlobalExceptionHandlerによる例外処理
