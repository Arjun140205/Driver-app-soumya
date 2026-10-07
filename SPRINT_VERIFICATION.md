# Sprint Verification

## Environment

- Java: OpenJDK 17.0.13
- Node: v24.1.0 (requested sprint version was Node 20; `nvm` is unavailable)
- Angular: 16.2 from `angularapp/package.json`
- Maven: not installed (`mvn` command unavailable; Homebrew installation was blocked by an unrelated untrusted tap during auto-update)
- Database: not started; application config uses in-memory H2

## Backend tests

Command attempted: `cd springapp && ./mvnw test`

Result: **BLOCKED**. The repository wrapper cannot start because `org.apache.maven.wrapper.MavenWrapperMain` is missing.

Command attempted: `cd springapp && mvn test`

Result: **BLOCKED**. Maven is not installed.

## Angular tests

Command attempted: `cd angularapp && npx ng test --watch=false --browsers=ChromeHeadless`

Result: **PASS**. 8 executed, 83 skipped under the existing Karma/Jasmine configuration. The dedicated profile sprint spec was also run with `--include` and passed both tests.

Command: `cd angularapp && npx ng build`

Result: **PASS**. Production bundle generated successfully.

Command: `cd angularapp && npm start`

Result: **PASS** after running outside the sandbox. Angular compiled successfully and listened on port 8081; the server was stopped after verification.

## Feature verification

- User profile: implemented as `/api/users/me` using the authenticated principal and a profile page; not HTTP-verified.
- Trip tabs: implemented using existing Pending / Approved / Trip End / Closed / Rejected statuses; Cancelled added to lifecycle.
- Cancellation: pending and approved request cancellation implemented; terminal/rejected/cancelled states rejected.
- Driver availability: assigned driver set Active and unlinked from cancelled request transactionally.
- Cancellation cutoff: configured with `cancellation.cutoff-hours` / `CANCELLATION_CUTOFF_HOURS`, default 2 hours.
- Skeleton loading: implemented on profile and requests pages.
- Backend validation: pre-existing DTO constraints retained; cancellation is business validation.
- DTO mapping: existing manual `DtoMapper` preserved; no mapper dependency is declared in `pom.xml`.
- Error handling: existing `ErrorLog` persistence retained; response now includes status, timestamp, and path, and generic 5xx messages are sanitized.
- AOP logging: not implemented; no Spring AOP dependency exists, and build tooling is unavailable for dependency validation.
- Constructor injection: converted regular Spring-managed dependencies to constructor injection; the optional JavaMailSender remains optional field injection by design.
- Driver uniqueness: repository `existsByLicenseNumber` is used; unit tests added, but backend suite cannot run without Maven.
- ErrorLog flow: ErrorLog entity/repository pre-existed; new service layer delegates persistence to the existing repository.

## Regression checks

- Existing Angular `*.spec.ts` files: not modified.
- Existing JUnit files: not modified.
- Karma configuration: not modified.
- JWT/security and CORS configuration: not modified.
- API behavior: not verified against a running service.
