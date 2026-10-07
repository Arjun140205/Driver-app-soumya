# Sprint TODO

## Backend

- [x] ~~Audit existing DTOs, mapping, validation, exception handling, security, and request lifecycle~~
- [ ] Replace manual DTO mapping with annotation-based mapper (no mapper dependency exists yet)
- [x] ~~Use Java Streams for entity to DTO collection transformations~~
- [x] ~~Convert Spring-managed required dependencies to constructor injection~~
- [ ] Add SLF4J AOP `@Before` / `@After` operation logging
- [x] ~~Route persistent ErrorLog writes through ErrorLogService and retain structured global error responses~~
- [x] ~~Use repository existence check for unique driver license numbers; add unique/duplicate tests~~
- [x] ~~Add JWT-principal `/api/users/me` profile endpoint~~
- [x] ~~Enforce cancellation status rules, configurable cutoff, and transactional driver release~~
- [x] ~~Add focused new tests for cancellation, ErrorLog persistence service, and driver uniqueness~~
- [ ] Expand backend tests for validation, profile authorization, and exception response
- [ ] Run backend suite and Spring server (blocked: Maven unavailable; Homebrew auto-update stops on untrusted unrelated tap)

## Frontend

- [x] ~~Add authenticated profile page and navigation~~
- [x] ~~Add Ongoing / Scheduled / Completed request tabs~~
- [x] ~~Add request and profile skeleton loading~~
- [x] ~~Show server cancellation errors in the confirmation dialog~~
- [ ] Add broader loading states to driver/admin list screens
- [x] ~~Install Angular dependencies, run production build and Karma suite (8 pass, 83 skipped), and start dev server successfully~~

## Documentation and verification

- [x] ~~Create sprint implementation report~~
- [x] ~~Record actual environment and verification attempts~~
- [ ] Verify APIs against a running database-backed application
