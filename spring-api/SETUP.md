# Running InvestingHurdle Spring API (Swagger UI + CLI)

## Prerequisites
- Java 17+ (ensure `java -version` reports 17).
- Maven 3.8+ (`mvn -v`). If Maven is not installed, you can run via IntelliJ’s bundled Maven/Gradle tooling (see below).
- Git.
- No manual JAR setup needed; Maven pulls dependencies.

## Clone and Build
```bash
git clone <repo-url>
cd tax_hurdle/spring-api
mvn clean package -DskipTests
```

If Maven is not installed locally
- Use IntelliJ’s built-in Maven wrapper support: open the project (see below), IntelliJ will auto-download Maven and resolve dependencies.
- Or download Apache Maven and add it to PATH (skip if you prefer IntelliJ-managed Maven).

## Configuration (optional)
- `investing-hurdle.upload-dir` (default: `./uploads`)
- `investing-hurdle.default-financial-year` (default: `FY 2021-22`)
- Override via `src/main/resources/application.properties`, environment variables, or JVM flags, e.g.:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--investing-hurdle.upload-dir=./uploads"
```

## Run with Swagger UI (local dev)
```bash
mvn spring-boot:run
# or
java -jar target/spring-api-*.jar
```
- App URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Useful endpoints:
  - `POST /calculations/detect-broker` (multipart file) — verify broker mapping
  - `POST /calculations/calculate` — JSON result
  - `POST /calculations/export` — download Excel summary

## Run via CLI (curl examples)
Detect broker:
```bash
curl -F "file=@path/to/your.xlsx" \
  http://localhost:8080/calculations/detect-broker
```
Calculate (with explicit params):
```bash
curl -F "file=@path/to/your.xlsx" \
  -F "financial_year=FY 2024-25" \
  -F "quarter_scheme=STANDARD_Q4" \
  http://localhost:8080/calculations/calculate
```
Export Excel summary:
```bash
curl -o summary.xlsx \
  -F "file=@path/to/your.xlsx" \
  -F "financial_year=FY 2024-25" \
  -F "quarter_scheme=STANDARD_Q4" \
  http://localhost:8080/calculations/export
```

## Typical Workflow
1) Start server (`mvn spring-boot:run`).
2) Open Swagger UI, upload XLSX to `/calculations/detect-broker`; confirm mapping.
3) Call `/calculations/calculate` or `/calculations/export` with `financial_year` and `quarter_scheme` (defaults to `STANDARD_Q4` if omitted).
4) Download and review the generated Excel summary.

## Data Files
- Legacy console flow sample path: `./configuration/tax_2122_.xlsx`.
- For API calls, upload your XLSX; detection will infer broker mapping (Upstox/Zerodha/etc.).

## Troubleshooting
- Port in use: run with `-Dserver.port=9090`.
- Slow builds: add `-DskipTests`.
- Upload errors: ensure multipart with field name `file`.
- Java version issues: install JDK 17 and set `JAVA_HOME` accordingly.

## Running with IntelliJ IDEA (if Maven not installed)
1) Open IntelliJ → File → Open → select `tax_hurdle/spring-api` folder.
2) Let IntelliJ import as a Maven project (it will download Maven/Java deps automatically).
3) Ensure Project SDK is set to JDK 17 (File → Project Structure → Project SDK).
4) Build: use the Maven tool window → Lifecycle → `clean` then `package` (or run gutter icon on `pom.xml`).
5) Run app:
  - Via Maven tool window: Plugins → `spring-boot` → `spring-boot:run`, **or**
  - Create Run Configuration: Add “Spring Boot” or “Application”, main class `com.investinghurdle.api.ApiApplication` (or the main class defined in this project), classpath from Maven, VM options as needed, Program args empty.
6) Swagger UI: http://localhost:8080/swagger-ui/index.html (same endpoints as above).
7) CLI still works while IntelliJ run config is active.
