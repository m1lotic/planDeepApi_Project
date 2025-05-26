# 🗂️ Plan API Service

Spring Boot 기반의 일정 관리 백엔드 프로젝트입니다.  
유저는 회원가입 및 로그인 후 자신의 일정을 CRUD 할 수 있으며, 인증되지 않은 사용자는 일정 기능에 접근할 수 없습니다.

---

## 프로젝트 설명

- **Framework**: Spring Boot 3.5, Gradle
- **DB**: Mysql ( JPA 사용 )
- **기능 구성**
  - 회원가입 / 로그인 (세션 기반 인증)
  - 유저 정보 조회, 수정, 삭제
  - 일정 CRUD
  - 인증 필터를 통한 보호된 라우팅

---

## 패키징 구조

```bash
com.planApiService.manage
├── config # WebConfig 등 전역 설정
├── controller # API 컨트롤러
│ ├── UserController
│ └── ScheduleController
├── dto
│ ├── request # 요청 DTO
│ ├── response # 응답 DTO
├── entity # JPA 엔티티 클래스
├── filter # 인증 필터 (AuthFilter)
├── repository # JPA Repository 인터페이스
├── service # 비즈니스 로직
└── ManageApplication # Main 클래스
```

---

## ERD 다이어그램

```plaintext
[User]
- id (PK)
- name
- email (unique)
- password

[Schedule]
- id (PK)
- title
- content
- user_id (FK → User.id)

---

## API 명세서

유저(User)
| 기능       | Method | URL               | Request Body                | Response         | 상태  |
| -------- | ------ | ----------------- | --------------------------- | ---------------- | --- |
| 회원가입     | POST   | `/users/register` | `{ name, email, password }` | 없음               | 200 |
| 로그인      | POST   | `/users/login`    | `{ email, password }`       | 없음 (세션 생성)       | 200 |
| 유저 목록 조회 | GET    | `/users`          | 없음                          | `[UserResponse]` | 200 |
| 유저 단건 조회 | GET    | `/users/{id}`     | 없음                          | `UserResponse`   | 200 |
| 유저 수정    | PUT    | `/users/{id}`     | `{ name, email, password }` | 없음               | 200 |
| 유저 삭제    | DELETE | `/users/{id}`     | 없음                          | 없음               | 200 |

일정(schedule)
| 기능       | Method | URL                   | Request Body         | Response             | 상태  |
| -------- | ------ | --------------------- | -------------------- | -------------------- | --- |
| 일정 등록    | POST   | `/api/schedules`      | `{ title, content }` | 없음                   | 200 |
| 전체 일정 조회 | GET    | `/api/schedules`      | 없음                   | `[ScheduleResponse]` | 200 |
| 단건 일정 조회 | GET    | `/api/schedules/{id}` | 없음                   | `ScheduleResponse`   | 200 |
| 일정 수정    | PUT    | `/api/schedules/{id}` | `{ title, content }` | 없음                   | 200 |
| 일정 삭제    | DELETE | `/api/schedules/{id}` | 없음                   | 없음                   | 200 |
