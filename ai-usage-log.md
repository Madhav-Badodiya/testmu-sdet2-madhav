# AI Usage Log

All AI tools used during this assessment, per the submission guidelines.

---

| Tool | Task | What It Produced | My Decision |
|---|---|---|---|
| Claude (claude.ai) | Scaffolding review | Suggested folder structure and Maven dependency versions | I reviewed the structure against the assessment requirements and adjusted to match exactly what was asked |
| Claude (claude.ai) | pom.xml dependency versions | Provided current stable versions for Selenium 4, REST Assured 5, Allure 2.25, TestNG 7 | Verified versions against Maven Central before using |
| Claude (claude.ai) | GitHub Actions matrix syntax | Suggested `strategy.matrix` for parallelising suites | I made the decision to parallelise by suite (ui/api/integration) rather than by browser — aligns with the assessment ask |
| Claude (claude.ai) | Restful-Booker API behavior | Confirmed DELETE returns 201 (not 204) and invalid auth returns 200 with `reason: "Bad credentials"` rather than 401 | Cross-checked against actual Restful-Booker API docs at restful-booker.herokuapp.com |

---

**Note on ownership:** All architectural decisions — unified repo structure, ThreadLocal DriverManager, global RetryListener via testng.xml, config-priority system properties, JSON schema validation on GET — were my own design choices based on 3 years of production framework experience. AI was used to accelerate syntax lookup and version checking, not to make design decisions.
