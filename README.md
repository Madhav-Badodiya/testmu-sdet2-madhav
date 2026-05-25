# testmu-sdet2-madhav

**TestMu AI — SDET-2 Quality Engineering Assessment**
**Candidate:** Madhav Badodiya | QA Automation Engineer | 3 Years @ TCS

---

## Architecture Overview

This framework unifies UI and API test automation in a single repository with a shared config, shared utilities, and a consistent reporting layer — directly addressing the scenario: *"UI and API tests live in separate repos, there's no shared framework, half the tests are flaky, and nobody trusts the report."*

```
testmu-sdet2-madhav/
├── .github/workflows/
│   └── regression.yml          # GitHub Actions — push/PR trigger, parallelised by suite
├── src/test/
│   ├── java/com/testmu/
│   │   ├── base/               # BaseTest, BaseApiTest, BasePage — shared lifecycle
│   │   ├── config/             # ConfigManager (env switching), DriverManager (thread-safe)
│   │   ├── pages/              # POM — LoginPage, InventoryPage, CartPage, CheckoutPage...
│   │   ├── tests/
│   │   │   ├── ui/             # Login, Dashboard, FormValidation, CrossBrowser
│   │   │   ├── api/            # Auth, CRUD, ErrorHandling
│   │   │   └── integration/    # BookingEndToEndTest (API + UI combined)
│   │   └── utils/              # WaitUtils, RetryAnalyzer, JsonDataReader, CustomAssertions
│   └── resources/
│       ├── config/
│       │   ├── config.properties   # All env config — overridable via system properties
│       │   └── testng.xml          # Suite definition — parallel execution by class
│       └── testdata/
│           ├── login_data.json     # Externalised login scenarios
│           ├── booking_data.json   # Externalised API payloads
│           └── booking_schema.json # JSON schema for GET /booking/{id} validation
└── reports/screenshots/        # Failure screenshots — auto-captured by BaseTest
```

---

## Stack

| Layer | Technology |
|---|---|
| UI Automation | Selenium WebDriver 4 (Java) |
| API Automation | REST Assured 5 |
| Test Framework | TestNG 7 |
| Build | Maven |
| Reporting | Allure Reports |
| CI | GitHub Actions |
| Browser Mgmt | WebDriverManager (auto driver download) |
| Data | Jackson (JSON reader) |

---

## Setup

### Prerequisites
- Java 11+
- Maven 3.8+
- Chrome and Firefox installed (local runs)

### Run All Tests
```bash
mvn clean test
```

### Run Headless (CI-style)
```bash
mvn clean test -Dheadless=true
```

### Run Specific Suite
```bash
# API only
mvn test -Dsuite=api

# UI only
mvn test -Dsuite=ui
```

### Generate Allure Report
```bash
mvn allure:serve
```

### Switch Environments
```bash
# Override any config property via system property
mvn test -Dui.base.url=https://staging.saucedemo.com -Dapi.base.url=https://staging-api.example.com
```

---

## Design Decisions

**1. Single repo for UI + API**
Flakiness and trust problems compound when teams work in separate repos with no shared standards. Unified repo means shared config, shared utilities, single CI pipeline, and one Allure report.

**2. Thread-safe DriverManager with ThreadLocal**
Parallel test execution requires each thread to own its WebDriver instance. ThreadLocal prevents driver collision between parallel test classes.

**3. Config-driven environment switching**
`ConfigManager` reads `config.properties` but system properties take priority. Switch env with `-Dui.base.url=...` — no code changes, no separate property files per env.

**4. RetryAnalyzer wired via listener (not per-test annotation)**
Applying `RetryAnalyzer` per `@Test` is error-prone and gets missed. Registering via `RetryListener` in `testng.xml` applies it globally — every test gets retry coverage automatically.

**5. Data-driven via externalized JSON**
Test data lives in `testdata/*.json`, read via `JsonDataReader`. Adding a new login scenario or booking payload requires zero code changes — edit the JSON file only.

**6. `data-test` selectors throughout**
SauceDemo ships `data-test` attributes explicitly for automation. Using these over XPath or CSS class selectors makes tests resilient to visual redesigns.

**7. GitHub Actions parallelised by suite**
`strategy.matrix` runs UI, API, and integration suites as separate jobs simultaneously. Total CI time is bounded by the slowest suite, not the sum of all suites.

---

## What I'd Build Next With More Time

- PATCH /booking support in API tests (partial update coverage)
- Visual regression testing with Percy or Applitools
- Performance baseline assertions using JMeter integrated into CI
- Allure History Trend — wire to GitHub Pages for persistent run history
- Dedicated staging environment config with secrets stored in GitHub Actions Secrets
- Contract testing with Pact for the API layer
