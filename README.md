# jenkins-selenium-java

Java + Maven + TestNG + Selenium WebDriver project for the Jenkins CI/CD
hands-on tutorial (SauceDemo login tests).

## Structure

- `src/main/java/.../config/ConfigManager.java` — reads baseUrl/browser/remoteUrl/credentials
  from system properties or environment variables, with sensible defaults.
- `src/main/java/.../driver/DriverFactory.java` — ThreadLocal WebDriver factory,
  supports local Chrome/Firefox or RemoteWebDriver against a Selenium Grid/Standalone container.
- `src/main/java/.../pages/` — Page Objects for Login and Inventory pages.
- `src/test/java/.../tests/` — TestNG tests (BaseTest, LoginTest) covering valid login,
  locked-out user, and invalid password scenarios.
- `src/test/java/.../listeners/TestListener.java` — captures a screenshot on test failure.
- `testng.xml` — TestNG suite definition, registers the TestListener.
- `Jenkinsfile` — Declarative pipeline: starts a Selenium Standalone container, waits for
  Grid readiness, runs Maven tests, publishes JUnit results, archives evidence.
- `docker-compose.yml` — spins up a local Selenium Standalone Chrome container for
  running tests locally before pushing to Jenkins.

## Run locally

```powershell
docker compose up -d
docker compose ps
curl http://localhost:4444/status

mvn clean test `
  -Dbrowser=chrome `
  -DremoteUrl=http://localhost:4444 `
  -DbaseUrl=https://www.saucedemo.com/

docker compose down
```

## Run in Jenkins

1. Create Jenkins credentials with IDs `saucedemo-username` and `saucedemo-password`
   (values `standard_user` / `secret_sauce` for the public demo site).
2. Create a Pipeline job pointing at this repository, script path `Jenkinsfile`.
3. Build with parameters: `BROWSER` (chrome/firefox), `TEST_GROUP` (all/smoke/regression),
   `BASE_URL`.
