# Sprint Changes and File Guide

This guide summarizes the previous sprint changes and the follow-up code quality work. For test and runtime outcomes, see [SPRINT_VERIFICATION.md](SPRINT_VERIFICATION.md).

## Previous sprint

- Profile API/page: `AuthController.java`, `auth.service.ts`, `user-profile.component.ts/html/css`, route/module files, and both navigation templates.
- Request tabs, cancellation cutoff, driver release, and request skeleton: `DriverRequestController.java`, `DriverRequestServiceImpl.java`, `ValidationPatterns.java`, `application.properties`, and customer request component files.
- Central exception response: `GlobalExceptionHandler.java`.
- Focused tests: `DriverRequestCancellationSprintTest.java`, `user-profile.component.sprint.spec.ts`.
- Sprint tracking: `SPRINT_IMPLEMENTATION.md`, `SPRINT_TODO.md`, and `SPRINT_VERIFICATION.md`.

## Follow-up sprint

- Constructor injection: controllers, primary service implementations, `SecurityConfig`, `AdminSeeder`, `MyUserDetailsService`, and `GlobalExceptionHandler` now use constructor injection and final dependencies where applicable.
- ErrorLog flow: `ErrorLogService.java`, `ErrorLogServiceImpl.java`; `GlobalExceptionHandler.java` delegates persistence through the service; existing `ErrorLog.java`, `ErrorLogRepo.java`, and `ErrorLogDTO.java` are retained. The table remains `ErrorLogs` and JPA creates/updates it from the entity.
- Driver uniqueness: `DriverRepo.java` adds `existsByLicenseNumber`; `DriverServiceImpl.java` uses that check and throws the existing `DuplicateDriverException`.
- Validation: DTO boundary constraints remain the input validation source. Duplicate license and cancellation state/cutoff remain business rules. Broad controller catches were removed to preserve centralized errors.
- New tests: `DriverUniquenessSprintTest.java` and `ErrorLogServiceSprintTest.java`.

## Run locally

Backend (from `springapp`):

```sh
mvn spring-boot:run
```

Frontend (Node 20, from `angularapp`):

```sh
nvm use 20
npm i
npm start
```

The current environment did not have Maven, `nvm`, or installed Angular dependencies; see verification report for exact attempts/results.
