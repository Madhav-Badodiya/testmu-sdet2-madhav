# Test Strategy — TestMu SDET-2 Assessment

## What I Chose to Cover and Why

### UI Tests (SauceDemo)

**Login Flow** — Chosen as the entry gate to the entire application. If login breaks, everything else is blocked. Covered: valid login (data-driven, 2 users), locked-out user, empty credentials, wrong credentials. All scenarios use externalized JSON data.

**Dashboard / Inventory** — Post-login state. Verified: inventory loads, product count is correct (6), cart badge updates on add-to-cart, logout works. These are the core interactions a user performs immediately after login.

**Form Validation (Checkout)** — Chosen to demonstrate form testing discipline. Covered: empty form, missing last name, missing postal code, valid submission. Each error message is asserted exactly — not just "an error exists."

**Cross-Browser Smoke (Firefox)** — Login and product listing on Firefox. Chosen as the highest-risk cross-browser scenario: authentication and primary content render.

### API Tests (Restful-Booker)

**Auth** — Token generation is the dependency for all write operations. Covered: valid credentials return token, invalid credentials return "Bad credentials" body.

**CRUD** — Full create → read → update → delete cycle with dependency chaining (`dependsOnMethods`). GET validates against a JSON schema. Every call asserts response time < 5000ms.

**Error Handling** — 404 for non-existent resource, 403 for unauthenticated DELETE and PUT, 500 for malformed request body. These are the failure modes a real API consumer will hit.

### Integration Test

**BookingEndToEndTest** — API auth → API create booking → API verify data → UI login → UI cart interaction → API delete + verify 404. This test exercises both layers in sequence and demonstrates the SDET pattern of using API for state setup/teardown while asserting UI behavior independently.

---

## What I'd Cover Next

1. **PATCH /booking** — partial update is a separate HTTP verb with distinct behavior; not covered here
2. **Filter bookings** — GET /booking?firstname=X&checkout=Y — query param filtering
3. **Concurrent booking creation** — race condition / idempotency testing under parallel load
4. **UI: Checkout complete flow** — order summary → finish → confirmation screen
5. **UI: Sort functionality** — A-Z, Z-A, price low-high, high-low
6. **UI: Remove item from cart** — inverse of add-to-cart
7. **Performance baseline** — JMeter load test on POST /booking under 50 concurrent users

---

## Top 3 Risks I'd Flag to the Team

### Risk 1 — Flakiness from shared test state in API tests
**Current state:** `BookingCrudTest` uses `dependsOnMethods` which means if `testCreateBooking` fails, downstream tests (GET by ID, UPDATE, DELETE) are skipped rather than independently failing. This hides root cause.
**Recommended fix:** Each test that needs a booking should create its own via `@BeforeMethod` using a shared factory method, then clean up in `@AfterMethod`. Tests become independent and parallelisable.

### Risk 2 — No test data isolation between runs
**Current state:** Tests run against live public Restful-Booker data. Other users' bookings exist in the system. Filtering tests could return unexpected results if the dataset changes.
**Recommended fix:** Always use created booking IDs (not assumed IDs like `/booking/1`) for assertions. For production environments, use a dedicated test tenant or database snapshot reset between runs.

### Risk 3 — Browser driver version drift in CI
**Current state:** WebDriverManager auto-downloads the correct driver version. However, if Chrome updates mid-sprint in CI (which happens automatically on GitHub-hosted runners), a driver version mismatch can silently break all UI tests.
**Recommended fix:** Pin Chrome version in CI using `browser-actions/setup-chrome@v1` with `chrome-version: stable` and add a Slack/email notification on any UI test failure in `main` branch so the team catches driver issues within minutes of occurrence.
