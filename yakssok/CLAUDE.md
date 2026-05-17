# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**약쏙(Yakssok)** is a medication adherence and social wellness platform. Users register medication routines, get daily schedules generated automatically, check off doses, and share progress with friends who can send praise/nag feedback via push notifications.

## Commands

```bash
# Build
./gradlew build

# Run (with spring profile)
./gradlew bootRun --args='--spring.profiles.active=local'

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "server.yakssok.domain.medication.application.service.MedicationServiceTest"

# Run a single test method
./gradlew test --tests "server.yakssok.domain.medication.application.service.MedicationServiceTest.CreateMedication.create_success"

# Generate QueryDSL Q-classes (required after entity changes)
./gradlew compileJava
```

Generated QueryDSL Q-classes go to `src/main/generated/` (included in source sets automatically).

## Architecture

### Package Structure

```
server.yakssok
├── domain/
│   ├── auth/
│   ├── feedback/
│   ├── friend/
│   ├── image/
│   ├── medication/
│   ├── medication_schedule/
│   ├── notification/
│   ├── notice/
│   └── user/
└── global/
    ├── common/       # jwt, security, querydsl, response, swagger, util
    ├── dummy/        # dev data initializers
    ├── exception/    # ErrorCode, GlobalException, ResponseCode
    └── infra/        # fcm, oauth (apple/kakao), s3
```

Each domain follows this layered structure:
```
{domain}/
├── application/
│   ├── exception/    # Domain-specific exception (extends GlobalException)
│   └── service/      # Business logic
├── domain/
│   ├── entity/       # JPA entities
│   ├── policy/       # Domain policies (e.g., OverduePolicy)
│   └── repository/   # Spring Data JPA + custom QueryDSL repos
└── presentation/
    ├── controller/
    └── dto/
        ├── request/
        └── response/
```

### Key Architectural Patterns

**API Response Wrapper**
All endpoints return `ApiResponse<T>` with `{ code, message, body }`. Use `ApiResponse.success(body)` or `ApiResponse.success()` for void responses.

**Exception Handling**
- `ResponseCode` interface implemented by `ErrorCode` (errors) and `SuccessCode` (successes) enums
- Domain exceptions extend `GlobalException(ResponseCode responseCode)`
- Example: `throw new MedicationException(ErrorCode.NOT_FOUND_MEDICATION)`
- Error codes grouped by domain: 1xxx OAuth, 2xxx Auth, 3xxx User, 4xxx Friend, 5xxx Medication, 9xxx Common/Image/FCM

**Custom Repository Pattern**
Complex queries use QueryDSL via `*QueryRepositoryImpl` classes (e.g., `UserQueryRepositoryImpl`). JDBC batch insert is used for bulk schedule creation via `MedicationScheduleJdbcRepository.batchInsert()`.

**OAuth Strategy Pattern**
`OAuthStrategy` interface with Apple and Kakao implementations, selected via `OAuthStrategyFactory`. Strategy handles `fetchUserInfo`, `getOAuthType`, and `unlink`.

**Async Messaging (RabbitMQ)**
Feedback, notice, and medication alarm events are published to RabbitMQ exchanges. Consumers in `domain/notification/consumer/` (`FeedbackAlarmConsumer`, `MedicationAlarmConsumer`) listen to queues and trigger FCM push via `PushService`.

**Schedule Generation**
`MedicationScheduleGenerator` generates all `MedicationSchedule` records at medication creation time (full insert for the entire date range × intake days × intake times). JDBC batch insert handles bulk persistence.

**Security**
JWT filter (`JwtAuthenticationFilter`) populates `YakssokUserDetails` in the security context. Controllers extract `userId` from the authenticated principal.

### Active Spring Profiles

| Profile | DB | Use |
|---------|----|----|
| `local`  | MySQL localhost | Local development |
| `dev`    | MySQL (remote) | Dev server |
| `prod`   | MySQL (remote) | Production |
| (test)   | H2 in-memory (MySQL mode) | `src/test/resources/application.yml` |

### Test Conventions

- Use `@ExtendWith(MockitoExtension.class)` with `@Mock` / `@InjectMocks`
- Organize with `@Nested` + `@DisplayName` in Korean
- Tests run against H2; no Spring context load required for service-layer unit tests
- Integration tests that need Spring context use `src/test/resources/application.yml`

## Claude Code 관련 질문 처리

claude-code-guide는 틀린 답을 낼 때가 있다. 사용자가 Claude Code 기능에 대해 추가 질문을 하면, 공식 문서를 curl로 직접 참조해서 답한다.

```bash
curl https://code.claude.com/docs/ko/overview.md
```

문서 URL 패턴: `https://code.claude.com/docs/ko/{페이지명}.md`

답변 후에는 `AskUserQuestion`으로 퀴즈를 내서 사용자가 직접 따라해보도록 안내한다.