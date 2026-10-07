# Sprint Implementation

## Implemented

- [x] Audited the existing Spring Boot / Angular structure, DTOs, mapper, validation, authentication, request statuses, and current pages.
- [x] Added a profile endpoint at `/api/users/me`. It reads the user from the authenticated JWT principal and returns only existing non-sensitive profile fields.
- [x] Added a guarded Angular profile page linked from both customer and admin navigation. It handles loading, success, and error states.
- [x] Organized customer requests into Ongoing (Approved), Scheduled (Pending), and Completed (Trip End, Closed, Rejected, Cancelled) tabs using existing lifecycle terminology.
- [x] Added request/profile skeleton placeholders.
- [x] Changed request deletion into cancellation history. Pending and approved requests can be cancelled; completed/rejected/already-cancelled requests cannot. Approved requests must be cancelled before the configurable cutoff.
- [x] Added `cancellation.cutoff-hours`, configurable through `CANCELLATION_CUTOFF_HOURS`, defaulting to 2 hours.
- [x] On valid cancellation, the request status is set to Cancelled, its driver association is released, and the driver is marked Active in the same transaction.
- [x] Removed a controller catch-all that hid request creation errors from centralized exception handling.
- [x] Improved global error response structure with status, timestamp, and path, while sanitizing generic server errors.
- [x] Added a new backend cancellation service test and a separate Angular profile component test. Existing tests were not changed.
- [x] Used Java Streams for request list DTO mapping.
- [x] Converted required Spring dependencies to constructor injection with final fields across controllers, services, service implementations, configuration, and advice. The optional JavaMailSender remains optional injection by design.
- [x] Routed persistent error logging through a dedicated `ErrorLogService` while retaining the existing JPA `ErrorLogs` mapping and repository.
- [x] Confirmed driver license uniqueness existed and standardized the service check to `existsByLicenseNumber`; added tests for the unique and duplicate paths.

## Existing architecture retained / incomplete

- [x] Existing DTOs reused. DTO architecture was not recreated.
- [ ] Annotation-based mapper: `pom.xml` contains no mapping library, and `DtoMapper` is static/manual. No new library was added without being able to install/build and assess compatibility.
- [x] Existing bean validation patterns and `@Valid` usage were audited and preserved.
- [x] Existing persistent ErrorLog and global exception advice were retained and improved.
- [ ] Spring AOP `@Before` / `@After`: not added. The project has no AOP dependency and Maven is unavailable to validate dependency resolution/build compatibility.
- [ ] Broader skeleton loading for driver and admin list pages remains.

## Before / after

- Before: request cards were one list, pending requests were physically deleted, and exception handling in the create controller converted all exceptions to an empty 409.
- After: requests are categorized, cancellation is recorded with business rules and driver release, and create errors flow to global handling.
- Before: there was no profile route.
- After: profile details are fetched from the authenticated principal through an API that does not accept a user ID.

## Verification

See [SPRINT_VERIFICATION.md](SPRINT_VERIFICATION.md) for environment and actual command results. Angular dependencies installed, production build and browser tests passed. Spring execution remains blocked because Maven is unavailable and Homebrew could not install it due an unrelated untrusted tap.

## File changes

See the completion response for the complete created/modified file list.
