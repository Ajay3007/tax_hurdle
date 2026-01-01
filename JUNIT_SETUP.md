# JUnit 5 Setup Instructions

## Current Status
Test classes have been created in the `tests/` directory with comprehensive test coverage design for:
- FIFOCalculatorTest (8 test cases)
- DataValidatorTest (28 test cases)
- QuarterConfigTest (17 test cases)
- ConfigFileManagerTest (16 test cases)
- InvalidSecurityExceptionTest (12 test cases)

**Total test cases planned: 81 tests** targeting ~70% code coverage

## Manual Setup (Required to Run Tests)

### Step 1: Download JUnit 5 JARs
Download the following files from Maven Central and place in `./lib/` directory:

**JUnit Platform (Core):**
- junit-platform-commons-1.10.0.jar
- junit-platform-engine-1.10.0.jar
- junit-platform-launcher-1.10.0.jar

**JUnit Jupiter (Programming Model):**
- junit-jupiter-api-5.10.0.jar
- junit-jupiter-engine-5.10.0.jar

**Download URLs:**
```
https://central.maven.org/maven2/org/junit/platform/junit-platform-commons/1.10.0/junit-platform-commons-1.10.0.jar
https://central.maven.org/maven2/org/junit/platform/junit-platform-engine/1.10.0/junit-platform-engine-1.10.0.jar
https://central.maven.org/maven2/org/junit/platform/junit-platform-launcher/1.10.0/junit-platform-launcher-1.10.0.jar
https://central.maven.org/maven2/org/junit/jupiter/junit-jupiter-api/5.10.0/junit-jupiter-api-5.10.0.jar
https://central.maven.org/maven2/org/junit/jupiter/junit-jupiter-engine/5.10.0/junit-jupiter-engine-5.10.0.jar
```

### Step 2: Update `.classpath`
After downloading JARs, add the following entries to `.classpath`:

```xml
<classpathentry kind="lib" path="C:/Users/Ajay.Gupt/OneDrive - Reliance Corporate IT Park Limited/Documents/csp/tax_hurdle/lib/junit-platform-commons-1.10.0.jar"/>
<classpathentry kind="lib" path="C:/Users/Ajay.Gupt/OneDrive - Reliance Corporate IT Park Limited/Documents/csp/tax_hurdle/lib/junit-platform-engine-1.10.0.jar"/>
<classpathentry kind="lib" path="C:/Users/Ajay.Gupt/OneDrive - Reliance Corporate IT Park Limited/Documents/csp/tax_hurdle/lib/junit-platform-launcher-1.10.0.jar"/>
<classpathentry kind="lib" path="C:/Users/Ajay.Gupt/OneDrive - Reliance Corporate IT Park Limited/Documents/csp/tax_hurdle/lib/junit-jupiter-api-5.10.0.jar"/>
<classpathentry kind="lib" path="C:/Users/Ajay.Gupt/OneDrive - Reliance Corporate IT Park Limited/Documents/csp/tax_hurdle/lib/junit-jupiter-engine-5.10.0.jar"/>
```

Also add `tests` source directory:
```xml
<classpathentry kind="src" path="tests"/>
```

### Step 3: Compile Tests
```bash
javac -cp "bin:lib/*" -d bin tests/util/FIFOCalculatorTest.java tests/util/DataValidatorTest.java tests/util/QuarterConfigTest.java tests/util/ConfigFileManagerTest.java tests/exception/InvalidSecurityExceptionTest.java
```

### Step 4: Run Tests with JUnit Console Launcher
```bash
java -cp "bin:lib/*" org.junit.platform.console.ConsoleLauncher --scan-classpath
```

## Alternative: Use Maven or Gradle

For easier dependency management, consider migrating to Maven:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

## Test Coverage Summary

| Test Class | Test Methods | Coverage Focus |
|-----------|-------------|-----------------|
| FIFOCalculatorTest | 8 | FIFO algorithm, edge cases, state management |
| DataValidatorTest | 28 | Date validation, amount validation, quantity validation, symbol validation, price validation, range validation |
| QuarterConfigTest | 17 | Quarter detection, date boundaries, fiscal year ranges, custom quarters |
| ConfigFileManagerTest | 16 | Properties file loading, CLI argument parsing, path validation, configuration management |
| InvalidSecurityExceptionTest | 12 | Error codes, exit codes, exception constructors, error handling |
| **Total** | **81** | **~70% estimated coverage** |

## Next Steps
1. Download JUnit 5 JARs as listed above
2. Update `.classpath` with new JAR paths
3. Add `tests` source directory to `.classpath`
4. Compile tests using command in Step 3
5. Run tests using command in Step 4
6. Monitor code coverage and add additional tests as needed

## Notes
- All test classes are in `tests/` directory (mirroring `src/` structure)
- Tests follow JUnit 5 conventions with `@Test`, `@DisplayName`, `@BeforeEach`, `@AfterEach` annotations
- ConfigFileManagerTest uses `@TempDir` for temporary file management
- Tests use assertions for validation: `assertEquals()`, `assertTrue()`, `assertFalse()`, `assertNotNull()`, `assertThrows()`
