# 🚀 Spring Boot Community Platform (コミュニティプラットフォーム)

<div align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java" alt="Java 17">
  <img src="https://img.shields.io/badge/SpringBoot-3.2.5-green?style=flat-square&logo=springboot" alt="SpringBoot 3.2.5">
  <img src="https://img.shields.io/badge/SpringSecurity-6.x-blueviolet?style=flat-square&logo=springsecurity" alt="SpringSecurity">
  <img src="https://img.shields.io/badge/JPA-Hibernate-red?style=flat-square&logo=hibernate" alt="JPA">
  <img src="https://img.shields.io/badge/Language-日本語/한국어-blue?style=flat-square" alt="Language">
</div>

---

## 🌏 Language Selector
- [日本語 (Japanese)](#-jp-プロジェクト概要)
- [한국어 (Korean)](#-ko-프로젝트-개요)

---

## 🇯🇵 [JP] プロジェクト概要

### 📋 開発背景
本プロジェクトは、日本でのエンジニア就職を目指し、「堅牢な認証システム」と「データ整合性の確保」を最優先事項として設計・開発しました。単なる機能の実装にとどまらず、商用環境で求められる例外処理の共通化や、JPAによるパフォーマンス最適化のプロセスを体系的に反映させています。

### 🛠 技術スタック (Tech Stack)
- **Backend**: Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **Database**: H2 / MySQL
- **Frontend**: Thymeleaf, JavaScript (Fetch API), Bootstrap 5
- **Library**: Lombok, Java Mail Sender, JUnit 5

### ✨ 主な機能 (Core Features)
1. **精密なユーザー状態管理**: `ACTIVE`, `INACTIVE`(メール未認証), `WITHDRAWN`(退会済み)の3状態を定義。
2. **2段階ログイン検証**: パスワード一致確認後、SuccessHandlerにてユーザー状態を再検証する強固なセキュリティ。
3. **メール認証システム**: UUIDトークンを用いたメールリンク認証による虚偽アカウント生成の防止。
4. **アカウントセルフ復旧**: 退会したユーザーが認証を経て自らアカウントを復旧できるUXの提供。
5. **活動ログの可視化**: マイページにて自分が作成した投稿、コメント、いいねを一覧で管理可能。

---

## 🇰🇷 [KO] 프로젝트 개요

### 📋 개발 배경
실무 수준의 안정성과 확장성을 목표로 설계한 커뮤니티 플랫폼입니다. 일본 IT 업계에서 중시하는 '상세 설계의 논리성'과 '데이터 정합성'을 증명하기 위해, 사용자의 복잡한 상태 변화를 도메인 주도 설계(DDD) 관점에서 정교하게 풀어냈습니다.

### ✨ 핵심 기능 (Core Features)
1. **상세한 유저 상태 관리**: 유저의 가입/인증/탈퇴 상태를 세분화하여 비즈니스 로직 적용.
2. **보안 핸들러 커스터마이징**: Spring Security의 성공/실패 핸들러를 확장하여 상태별 맞춤형 대응.
3. **성능 최적화**: JPA 사용 시 발생하는 성능 저하 요인(N+1)을 분석하고 해결책을 적용.
4. **이메일 연동**: 실제 메일 서버 연동을 통한 비동기 계정 인증 프로세스 구축.

---

## 🚀 技術的解決と意思決定 (Technical Rationale)

### 1️⃣ JPA N+1 問題の解決と最適化
- **課題 (Problem)**: 投稿一覧取得時、作成者(User)情報を取得するために投稿数と同じ数の追加クエリが発生し、DBの負荷が増大。
- **解決 (Solution)**: Repositoryに `@EntityGraph` を適用。`Fetch Join` を使用することで、1回のクエリで関連データを一括取得するように改善。
- **結果 (Result)**: 100件のデータ照会時、従来の101回のクエリを1回に削減し、レスポンス速度を大幅に向上。



### 2️⃣ セキュリティハンドラーによるUXと信頼性の向上
- **課題 (Problem)**: ログイン失敗時、理由（パスワードミス、退会済み、未認証）をユーザーが特定できず、サポート負荷が増大する懸念。
- **解決 (Solution)**: `AuthenticationFailureHandler` を拡張し、例外の種類を特定してURLパラメータにメッセージを付与。また、`SuccessHandler` で退会アカウントのセッションを即時破棄。
- **結果 (Result)**: ユーザーの利便性を高めつつ、不適切なアクセスの試みを論理的に遮断。



### 3️⃣ ドメイン駆動設計(DDD)の意識
- **課題 (Problem)**: サービス層にロジックが集中（Fat Service）し、可読性とテストの容易性が低下。
- **解決 (Solution)**: Setterを制限し、エンティ티(`User`, `Post`)内部に `withdraw()`, `activate()` 等のビジネスメソッドを配置。
- **結果 (Result)**: オブジェクト自らが自身の状態を保証する設計となり、コードの保守性が向上。

---

## 📊 Database Schema (ERD)



- **User**: 全ての活動(Post, Comment, Like)の主軸となる。
- **Post**: 投稿データ。作成者(User)と1:Nの関係。
- **Comment**: 投稿に対する返信。UserとPost両方に紐づく。
- **PostLike**: 重複防止のため、UserとPostの複合キーに相当するユニーク制約を適用。

---

## 📂 Project Structure

```text
com.example.demo
├── common/         # 共通Enum (UserStatus, Role, SearchType)
├── config/         # Security, Web設定クラス
├── email/          # 非同期メール送信 & 認証トークンロジック
├── post/           # 投稿・いいねドメイン (Controller, Service, Repository, Entity)
├── comment/        # コメントドメイン
├── user/           # ユーザー管理、マイページ、アカウント復旧
└── exception/      # GlobalExceptionHandlerによる一括例外処理
