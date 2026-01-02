# InvestingHurdle: Architecture & Code Walkthrough Guide

*A comprehensive guide for new developers to understand the architecture, technology choices, and code structure of the InvestingHurdle tax calculator project.*

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Why These Technologies?](#why-these-technologies)
3. [Architecture Overview](#architecture-overview)
4. [Core Concepts Explained](#core-concepts-explained)
5. [Code Structure Walkthrough](#code-structure-walkthrough)
6. [Technology Deep Dive](#technology-deep-dive)
7. [Design Patterns Used](#design-patterns-used)
8. [Data Flow](#data-flow)
9. [Common Questions](#common-questions)

---

## Project Overview

### What is InvestingHurdle?

**InvestingHurdle** is a tax calculation engine for Indian equity traders. It processes trading data from Excel files and calculates tax liabilities for multiple trading categories:

- **STCG (Short-Term Capital Gains):** Profits from stocks held < 1 year
- **LTCG (Long-Term Capital Gains):** Profits from stocks held > 1 year
- **Speculation (Intraday):** Profits from same-day trading

### Why Build This?

Indian tax rules are complex:
- Different taxation rates for STCG, LTCG, and intraday trading
- Quarterly filing requirements
- Multiple broker formats for Excel exports
- Manual calculation is error-prone

**Solution:** An automated engine that:
- ✅ Reads broker Excel files
- ✅ Detects column layouts (works with any broker)
- ✅ Calculates tax categories
- ✅ Breaks down results by quarter
- ✅ Exports summary reports

---

## Why These Technologies?

### 1. Java (Language)

**What is Java?**
Java is a general-purpose programming language that runs on the JVM (Java Virtual Machine). Write once, run anywhere (Windows, Mac, Linux).

**Why Java for this project?**

| Reason | Explanation |
|--------|-------------|
| **Cross-platform** | Works on Windows, Mac, Linux without changes |
| **Mature** | 30 years of development, stable, widely used in finance |
| **Performance** | Fast execution (milliseconds for large datasets) |
| **Libraries** | Rich ecosystem for Excel, JSON, web services |
| **Enterprise standard** | Used by 90% of Fortune 500 companies |
| **Type-safe** | Catches errors at compile time (safer than Python/JavaScript) |

**For beginners:** Think of Java as "a safer, more structured programming language designed for large-scale applications."

### 2. Maven (Build Tool)

**What is Maven?**
Maven automates the build process: compiling code, downloading libraries, running tests, and packaging applications.

**What problems does it solve?**

❌ **Without Maven:**
```bash
# Manually download POI library, add to classpath, compile
# If POI 5.2.2 needs commons-collections4, you manually download that too
# If commons-collections4 needs another library, repeat...
# New developer = "Hey, where do I put these JARs? Which versions?"
# Building = error-prone, inconsistent across machines
```

✅ **With Maven:**
```xml
<!-- pom.xml: "I need POI 5.2.2" -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.2</version>
</dependency>
<!-- Maven automatically:
     1. Downloads POI 5.2.2
     2. Downloads all dependencies POI needs
     3. Manages versions to avoid conflicts
     4. Reusable across all machines -->
```

**Why Maven?**

| Feature | Benefit |
|---------|---------|
| **Dependency Management** | Automatic library downloads + conflict resolution |
| **Standardized Structure** | Every Maven project looks the same: `src/main/java`, `src/test/java`, etc. |
| **Plugins** | Pre-built tasks: compile, test, package, deploy |
| **Reproducible Builds** | Same code = same result, always |
| **Team Collaboration** | New developers don't need manual setup |
| **CI/CD Integration** | Automated testing & deployment pipelines |

**For beginners:** "Maven is like a recipe book for building software. Instead of manually doing 20 steps, you say 'build this' and Maven does everything."

### 3. Spring Boot (Web Framework)

**What is Spring Boot?**
Spring Boot is a framework that simplifies building production-ready web applications in Java. It provides:
- REST API endpoints (HTTP requests/responses)
- Configuration management
- Dependency injection
- Logging

**Why Spring Boot?**

❌ **Without Spring Boot:**
```java
// Manually set up web server, routing, error handling, etc.
// 500+ lines of boilerplate code
ServerSocket server = new ServerSocket(8080);
while (true) {
    Socket client = server.accept();
    // Parse HTTP request
    // Route to correct handler
    // Send response
    // Handle errors
}
```

✅ **With Spring Boot:**
```java
@RestController
public class CalculationController {
    @PostMapping("/calculations/upload")
    public ResponseEntity<TaxCalculationResponse> upload(@RequestParam MultipartFile file) {
        // Just write business logic
        // Spring handles: routing, parsing, response, errors
        return ResponseEntity.ok(response);
    }
}
```

**Why Spring Boot for this project?**

| Feature | Why It Matters |
|---------|---|
| **REST API** | Enables web & mobile clients to use calculations |
| **Auto-configuration** | "Just works" with minimal setup |
| **Testing Support** | Built-in test framework |
| **Production-ready** | Security, error handling, performance out of the box |
| **Swagger UI** | Auto-generated API documentation (users can test endpoints visually) |
| **Extensibility** | Easy to add new features (authentication, caching, etc.) |

**For beginners:** "Spring Boot is like having a pre-built house frame. You just add your rooms (business logic) instead of building walls from scratch."

### 4. Apache POI (Excel Processing)

**What is Apache POI?**
POI = Poor Obfuscation Implementation. A library that reads/writes Excel files programmatically.

**Why Apache POI?**

❌ **Without POI:**
```java
// Excel is binary format, complex structure
// Would need to manually parse .xlsx (which is actually a ZIP file!)
// Risk of bugs, incomplete implementation
```

✅ **With POI:**
```java
Workbook workbook = new XSSFWorkbook(new FileInputStream("file.xlsx"));
Sheet sheet = workbook.getSheetAt(0);
Row row = sheet.getRow(24);
Cell cell = row.getCell(8);
double value = cell.getNumericCellValue();
```

**For beginners:** "POI handles all the complexity of reading Excel files so you can work with data directly."

### 5. Log4j 2 (Logging)

**What is Log4j?**
A library for recording application events (info, warnings, errors) to console and files.

**Why Log4j?**

❌ **Without logging:**
```java
System.out.println("Error: file not found"); // Where? Mixed with other output
// Can't control verbosity, can't redirect to file easily
```

✅ **With Log4j:**
```java
logger.info("Processing file: {}", fileName);
logger.warn("Unexpected format in row {}", rowNum);
logger.error("Calculation failed", exception); // Includes stack trace
```

**Benefits:**
- 🎯 Different levels: DEBUG, INFO, WARN, ERROR
- 📁 Output to console, file, or both
- 🕐 Timestamps for all events
- 🔍 Essential for debugging production issues

**For beginners:** "Logging is like a detailed diary of what your application did. Invaluable when things go wrong."

---

## Architecture Overview

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INTERFACES                         │
├──────────────────────┬──────────────────┬──────────────────┤
│  JavaFX Desktop      │   REST API       │   Command Line   │
│  (GUI App)           │   (Web Service)  │   (CLI)          │
└──────────────────────┴──────────────────┴──────────────────┘
           │                    │                    │
           └────────────────────┼────────────────────┘
                                │
┌───────────────────────────────▼──────────────────────────────┐
│              CORE CALCULATION ENGINE                         │
├─────────────────────────────────────────────────────────────┤
│  • Broker Detection (Upstox, Zerodha, etc.)                │
│  • Column Mapping (Auto-detect layout)                     │
│  • Data Validation (Check data integrity)                  │
│  • Tax Calculation (STCG, LTCG, Speculation)               │
│  • Quarterly Breakdown (Q1-Q5 allocation)                  │
│  • Report Generation (CSV, Excel, JSON)                    │
└─────────────────────────────────────────────────────────────┘
           │
┌──────────┴──────────────────────────────────────────────────┐
│         EXTERNAL DEPENDENCIES                               │
├─────────────────────────────────────────────────────────────┤
│  • Apache POI (Excel I/O)                                  │
│  • Log4j 2 (Logging)                                       │
│  • Spring Boot (Web framework)                             │
│  • Jackson (JSON serialization)                            │
└─────────────────────────────────────────────────────────────┘
```

### Component Interaction

```
User uploads Excel
        │
        ▼
┌─────────────────┐
│ File Upload     │
│ Handler         │
└────────┬────────┘
         │
         ▼
┌─────────────────────────┐
│ Broker Detection        │ ◄─── Identifies: Upstox, Zerodha, etc.
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Column Mapping          │ ◄─── Maps columns: Buy, Sell, Date, etc.
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Data Validation         │ ◄─── Checks: Dates valid? Amounts > 0?
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Tax Calculation         │ ◄─── Calculates STCG/LTCG/Speculation
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Quarterly Breakdown     │ ◄─── Groups by: Q1, Q2, Q3, Q4, Q5
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Report Generation       │ ◄─── Creates: Excel, CSV, JSON
└────────┬────────────────┘
         │
         ▼
    Return Results
```

---

## Core Concepts Explained

### 1. Broker Detection

**What does it do?**
Detects which broker's format the Excel file uses by examining column headers and data patterns.

**Why?**
Different brokers export data differently:
- Upstox: columns [Date, Symbol, Buy Price, Sell Price, ...]
- Zerodha: columns [Trade Type, Symbol, Price, Qty, ...]

**Example:**
```java
// Broker detection algorithm
1. Read header row
2. Look for keywords: "Buy", "Sell", "Date", "Quantity"
3. Check data patterns (dates, numbers, text)
4. Match against known broker formats
5. Return: BrokerType.UPSTOX or BrokerType.ZERODHA or etc.
```

### 2. Column Mapping

**What does it do?**
Maps column indices to meaningful names (e.g., column 8 = Buy Amount, column 12 = Sell Amount).

**Why?**
Once broker is detected, we know which columns to read:
```java
// For Upstox:
INDEX_BUYAMT = 8;      // Buy Amount is in column 8
INDEX_SELLAMT = 12;    // Sell Amount is in column 12
INDEX_DAYS_HOLD = 13;  // Holding Days in column 13
```

### 3. STCG vs LTCG vs Speculation

**STCG (Short-Term Capital Gains)**
- Holding period: < 1 year (365 days)
- Tax rate: Added to income, taxed at slab rate
- Quarterly filing: Required

**LTCG (Long-Term Capital Gains)**
- Holding period: ≥ 1 year (365+ days)
- Tax rate: 20% flat (with indexation benefit)
- Quarterly filing: Not required for this calculation

**Speculation (Intraday)**
- Holding period: 0 days (same day buy & sell)
- Special category for tax purposes
- Affects quarterly allocation differently

### 4. Quarterly Breakdown

**Why quarters?**
Indian Income Tax requires quarterly estimated advance payments (Q1, Q2, Q3, Q4, Q5).

**Example:**
```
Financial Year 2021-22:
Q1: April 1 - June 15, 2021
Q2: June 16 - September 15, 2021
Q3: September 16 - December 15, 2021
Q4: December 16 - March 15, 2022
Q5: March 16 - March 31, 2022

Each trade's profit is allocated to the quarter when it was sold.
```

---

## Code Structure Walkthrough

### Directory Layout

```
tax_hurdle/
│
├── src/                          # Main source code
│   ├── bootstrap/
│   │   └── InvestingHurdleBootstrapper.java    # Entry point
│   │
│   ├── params/
│   │   ├── EquityLoader.java                   # Loads equity data from Excel
│   │   └── WorkbookLoader.java                 # Alternative loader
│   │
│   ├── security/
│   │   └── Security.java                       # Represents one stock transaction
│   │
│   ├── exception/
│   │   └── InvalidSecurityException.java       # Custom error type
│   │
│   ├── logging/
│   │   └── HurdleLogger.java                   # Logging utility
│   │
│   └── util/
│       └── HurdleConstant.java                 # Configuration constants
│
├── spring-api/                   # REST API (Spring Boot)
│   ├── src/main/java/com/investinghurdle/
│   │   ├── api/
│   │   │   ├── InvestingHurdleApiApplication.java    # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── CalculationController.java        # REST endpoints
│   │   │   ├── service/
│   │   │   │   └── TaxCalculationService.java        # Business logic
│   │   │   └── dto/
│   │   │       └── TaxCalculationResponse.java       # Data transfer object
│   │   └── util/
│   │       └── BrokerType.java                       # Broker enumeration
│   │
│   └── src/main/resources/
│       └── application.yml                           # Configuration
│
├── javafx-ui/                    # Desktop GUI (JavaFX)
│   ├── src/main/java/com/investinghurdle/ui/
│   │   ├── InvestingHurdleUI.java                    # Main app window
│   │   ├── ApiClient.java                           # Calls REST API
│   │   ├── UploadScene.java                         # Upload screen
│   │   ├── ResultsScene.java                        # Results screen
│   │   └── SettingsScene.java                       # Settings screen
│   │
│   └── src/main/resources/
│       └── application.yml                          # Configuration
│
├── configuration/                # Sample data
│   ├── tax_2122_.xlsx            # Sample Excel file
│   └── tax_2223_.xlsx            # Another year's data
│
├── lib/                          # External libraries (JAR files)
│   ├── poi-ooxml-5.2.2.jar       # Excel processing
│   ├── log4j-api-2.20.0.jar      # Logging
│   ├── commons-collections4.jar  # Utilities
│   └── ...more JARs...
│
├── pom.xml                       # Maven configuration (parent)
├── README.md                     # Project overview
├── SETUP.md                      # Installation guide
└── docs/                         # Documentation
    ├── ARCHITECTURE_AND_CODE_WALKTHROUGH.md    # This file
    ├── images/
    └── README.md
```

### Key Files & What They Do

#### 1. InvestingHurdleBootstrapper.java (Entry Point)

**Location:** `src/bootstrap/InvestingHurdleBootstrapper.java`

**What it does:**
```java
public class InvestingHurdleBootstrapper {
    public static void main(String[] args) {
        // Step 1: Parse command-line arguments
        //         Example: --financial-year "FY 2022-23"
        
        // Step 2: Load configuration from file or defaults
        
        // Step 3: Create EquityLoader instance
        EquityLoader loader = new EquityLoader();
        
        // Step 4: Read Excel file and perform calculations
        loader.initialize();
        
        // Step 5: Display results to console
        
        // Step 6: Export to CSV/Excel (if configured)
    }
}
```

**Why separate entry point?**
- Legacy console app can run independently
- REST API has its own entry point (InvestingHurdleApiApplication)
- Keeps concerns separated (CLI vs Web)

#### 2. EquityLoader.java (Core Calculation Engine)

**Location:** `src/params/EquityLoader.java`

**What it does:**
```java
public class EquityLoader {
    // Hardcoded column indices for Upstox format
    private static final int INDEX_BUYAMT = 8;      // Column I
    private static final int INDEX_SELLAMT = 12;    // Column M
    private static final int INDEX_DAYS_HOLD = 13;  // Column N
    
    public void initialize() {
        // 1. Load Excel file from configuration/tax_2122_.xlsx
        // 2. Iterate through rows 25-297
        // 3. For each row:
        //    a. Create Security object (one stock transaction)
        //    b. Extract: buy amount, sell amount, holding days
        //    c. Determine if STCG or Speculation
        //    d. Allocate to correct quarter based on sell date
        // 4. Calculate totals: STCG per quarter, total speculation turnover
        // 5. Print formatted results
    }
}
```

**Key logic:**
```
For each transaction:
  If holding_days == 0:
    → Speculation (intraday trade)
  Else if holding_days > 0:
    → STCG (short-term capital gain)
    → Determine quarter from sell_date
    → Add profit to that quarter
```

#### 3. Security.java (Data Model)

**Location:** `src/security/Security.java`

**What it represents:**
```java
public class Security {
    private String symbol;           // Stock symbol: "RELIANCE", "TCS"
    private double buyAmount;        // Buy price × quantity
    private double sellAmount;       // Sell price × quantity
    private int daysHeld;            // Holding period in days
    private LocalDate saleDate;      // When was it sold
    private double profit;           // Calculated: sellAmount - buyAmount
    
    // Getters & calculated properties
    public boolean isIntraday() {
        return daysHeld == 0;
    }
    
    public Quarter getAllocatedQuarter(FinancialYear fy) {
        // Determine which quarter this transaction falls into
        // Based on saleDate and FinancialYear
    }
}
```

**Why this class?**
- Represents one stock transaction
- Provides methods to determine tax category
- Easy to test, modify, extend

#### 4. TaxCalculationResponse.java (Data Transfer Object)

**Location:** `spring-api/src/main/java/com/investinghurdle/api/dto/TaxCalculationResponse.java`

**What it is:**
```java
public class TaxCalculationResponse {
    private String financialYear;           // "FY 2021-22"
    private StcgBreakdown stcg;             // STCG calculations
    private SpeculationBreakdown speculation; // Intraday calculations
    private QuarterlyBreakdown quarterly;    // Quarter-wise details
    private long processingTimeMs;          // How long calculation took
    
    // Automatically converts to/from JSON:
    // {"financial_year": "FY 2021-22", "stcg": {...}, ...}
}
```

**Why DTO?**
- Separates internal data model from API response format
- Can evolve API response without changing database/core logic
- Enables serialization to JSON automatically

#### 5. CalculationController.java (REST Endpoints)

**Location:** `spring-api/src/main/java/com/investinghurdle/api/controller/CalculationController.java`

**What it provides:**
```java
@RestController
@RequestMapping("/calculations")
public class CalculationController {
    
    // Endpoint 1: Upload Excel & Calculate
    @PostMapping("/upload")
    public ResponseEntity<TaxCalculationResponse> upload(
        @RequestParam MultipartFile file,
        @RequestParam String financialYear,
        @RequestParam(defaultValue = "STANDARD_Q4") String quarterScheme) {
        // Receive uploaded file
        // Call TaxCalculationService.calculateFromFile()
        // Return JSON response
    }
    
    // Endpoint 2: Detect Broker Format
    @PostMapping("/detect-broker")
    public ResponseEntity<?> detectBroker(@RequestParam MultipartFile file) {
        // Analyze file structure
        // Return broker type + column mapping
    }
    
    // Endpoint 3: Export as Excel
    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> exportExcel(...) {
        // Generate Excel file
        // Return as downloadable attachment
    }
    
    // Endpoint 4: Health Check
    @GetMapping("/health")
    public ResponseEntity<Map> health() {
        // Return: {"status": "UP"}
    }
}
```

**Why separate controller?**
- Handles HTTP requests/responses
- Controllers shouldn't contain business logic
- Logic lives in Service layer

#### 6. TaxCalculationService.java (Business Logic)

**Location:** `spring-api/src/main/java/com/investinghurdle/api/service/TaxCalculationService.java`

**What it does:**
```java
public class TaxCalculationService {
    
    public TaxCalculationResponse calculateFromFile(MultipartFile file, String fy, String scheme) {
        // 1. Validate input
        // 2. Create EquityLoader instance
        // 3. Load Excel data
        // 4. Run calculations
        // 5. Build response DTO
        // 6. Return to controller
    }
    
    public Map<String, Object> detectBrokerFormat(MultipartFile file) {
        // 1. Read Excel headers
        // 2. Analyze structure
        // 3. Match to known broker formats
        // 4. Return mapping information
    }
}
```

**Why separate service?**
- **Layered Architecture** = each layer has single responsibility
- Controller = HTTP handling
- Service = Business logic
- Easy to test (mock controller, test service)

#### 7. ApiClient.java (JavaFX ↔ Spring API)

**Location:** `javafx-ui/src/main/java/com/investinghurdle/ui/ApiClient.java`

**What it does:**
```java
public class ApiClient {
    
    public JsonObject detectBroker(File file) {
        // 1. Create HTTP POST request
        // 2. Add file to multipart form
        // 3. Send to http://localhost:8080/api/v1/calculations/detect-broker
        // 4. Parse JSON response
        // 5. Return to caller (JavaFX UI)
    }
    
    public JsonObject calculate(File file, String fy, String scheme) {
        // 1. Create HTTP POST request
        // 2. Add file + financial_year + quarter_scheme
        // 3. Send to http://localhost:8080/api/v1/calculations/upload
        // 4. Parse JSON response
        // 5. Return to caller
    }
}
```

**Why this class?**
- Decouples JavaFX UI from REST API details
- One place to change base URL, add authentication, etc.
- Easy to mock for testing UI

---

## Technology Deep Dive

### Java Virtual Machine (JVM)

**What is JVM?**
JVM = Java Virtual Machine. A software layer that interprets compiled Java code.

**How it works:**
```
Java Source Code (.java)
        ↓
    Compiler
        ↓
Bytecode (.class) ← Can run on ANY machine with JVM
        ↓
    JVM
        ↓
Machine Code (Windows/Mac/Linux specific)
        ↓
Operating System
```

**Benefit: Write Once, Run Anywhere**
- Compile on Windows → bytecode works on Mac/Linux unchanged
- This is why Java dominates enterprise software

### Maven Project Structure

**Standard Maven Layout:**
```
my-project/
├── pom.xml                          # Project configuration
├── src/
│   ├── main/
│   │   ├── java/                    # Your code
│   │   │   └── com/example/App.java
│   │   └── resources/               # Config files, images
│   │       └── application.properties
│   └── test/
│       ├── java/                    # Test code
│       │   └── com/example/AppTest.java
│       └── resources/               # Test configs
│
└── target/
    ├── classes/                     # Compiled .class files
    ├── test-classes/                # Compiled test classes
    └── my-project-1.0.jar           # Packaged JAR file
```

**Why this structure?**
- Consistency: Every Maven project follows same layout
- Automation: Maven knows exactly where to find code, tests, resources
- Team collaboration: New developers immediately know file locations

**Maven Build Lifecycle:**
```
clean  →  compile  →  test  →  package  →  install  →  deploy
  │          │         │         │          │          │
  │          │         │         │          │          Deploy to production
  │          │         │         │          Install to local repository
  │          │         │         Create JAR file
  │          │         Run unit tests
  │          Compile .java → .class
  Delete target/ directory
```

### REST API Fundamentals

**What is REST?**
REST = Representational State Transfer. A style of building web APIs.

**Key concepts:**

1. **HTTP Methods:**
   - GET: Retrieve data (safe, no side effects)
   - POST: Create/submit data (not safe, may have side effects)
   - PUT: Update data
   - DELETE: Remove data

2. **Endpoint = URL + Method:**
   ```
   GET /api/v1/calculations/financial-years
   POST /api/v1/calculations/upload
   ```

3. **Request/Response Format:**
   ```
   Request (JSON):
   {
     "financial_year": "FY 2021-22",
     "quarter_scheme": "STANDARD_Q4"
   }
   
   Response (JSON):
   {
     "stcg": {"total": 512.41, ...},
     "speculation": {"total_turnover": 39280.20},
     ...
   }
   ```

**Spring Boot REST Implementation:**
```java
@PostMapping("/calculations/upload")           // POST method, path
@RequestParam MultipartFile file               // Extract from request
@RequestParam String financialYear              // Extract from request
public ResponseEntity<TaxCalculationResponse> upload(...) {  // Return JSON
    TaxCalculationResponse response = service.calculate(...);
    return ResponseEntity.ok(response);         // HTTP 200 + JSON body
}
```

### Spring Boot Request/Response Flow

```
User sends HTTP Request:
POST /api/v1/calculations/upload
Content-Type: multipart/form-data
[Binary file data]
        │
        ▼
Spring Boot Routes to CalculationController.upload()
        │
        ▼
Method receives parameters from request:
  - file ← from multipart form
  - financialYear ← from query string
  - quarterScheme ← from query string
        │
        ▼
Method calls TaxCalculationService.calculateFromFile()
        │
        ▼
Service returns TaxCalculationResponse object
        │
        ▼
Spring Boot automatically converts to JSON
        │
        ▼
Returns HTTP Response:
HTTP 200 OK
Content-Type: application/json
{
  "financial_year": "FY 2021-22",
  "stcg": {...},
  "speculation": {...}
}
        │
        ▼
User receives JSON response
```

### Dependency Injection

**What is it?**
Dependency Injection = Giving an object what it needs instead of it creating dependencies itself.

**Example:**

❌ **Without DI (tightly coupled):**
```java
public class CalculationController {
    private TaxCalculationService service;
    
    public CalculationController() {
        this.service = new TaxCalculationService();  // Creates dependency
        // If TaxCalculationService changes, Controller breaks
    }
}
```

✅ **With DI (loosely coupled):**
```java
public class CalculationController {
    private TaxCalculationService service;
    
    @Autowired  // Spring injects this
    public CalculationController(TaxCalculationService service) {
        this.service = service;  // Received from outside
        // Service can change implementation, Controller still works
    }
}
```

**Benefits:**
- Easy to test (inject mock service)
- Easy to extend (swap different implementation)
- Less coupling, more flexibility

---

## Design Patterns Used

### 1. Singleton Pattern (One Instance)

**What:**
Only one instance of a class exists throughout application.

**Where used:**
- Logger instances
- Configuration managers
- Database connections

**Example:**
```java
public class HurdleLogger {
    private static final Logger logger = LogManager.getLogger();  // Single instance
    
    public static void info(String message) {
        logger.info(message);
    }
}
```

### 2. Factory Pattern (Object Creation)

**What:**
A class/method that creates objects without specifying exact classes.

**Where used:**
- Creating QuarterConfig for different financial years
- Creating Security objects from Excel rows

**Example:**
```java
public class QuarterConfigFactory {
    public static QuarterConfig create(String financialYear) {
        switch(financialYear) {
            case "FY 2021-22":
                return new QuarterConfig(2021, 22);
            case "FY 2022-23":
                return new QuarterConfig(2022, 23);
            default:
                throw new IllegalArgumentException("Unknown year");
        }
    }
}
```

### 3. Strategy Pattern (Pluggable Algorithms)

**What:**
Define multiple algorithms in separate classes, choose at runtime.

**Where used:**
- Different broker detection strategies
- Different export formats (CSV, Excel, JSON)

**Example:**
```java
public interface BrokerDetectionStrategy {
    BrokerType detect(Sheet sheet);
}

public class UpstoxDetectionStrategy implements BrokerDetectionStrategy {
    @Override
    public BrokerType detect(Sheet sheet) {
        // Upstox-specific detection logic
    }
}

public class ZerodhaDetectionStrategy implements BrokerDetectionStrategy {
    @Override
    public BrokerType detect(Sheet sheet) {
        // Zerodha-specific detection logic
    }
}

// Usage:
BrokerDetectionStrategy strategy = getStrategy(brokerType);
BrokerType detected = strategy.detect(sheet);
```

### 4. Data Transfer Object (DTO) Pattern

**What:**
A simple class that carries data between layers without business logic.

**Where used:**
- TaxCalculationResponse
- StcgBreakdown
- QuarterlyBreakdown

**Example:**
```java
// DTO (just data, no logic)
public class TaxCalculationResponse {
    private String financialYear;
    private StcgBreakdown stcg;
    private SpeculationBreakdown speculation;
    // Getters only, no heavy methods
}

// Automatically converts to/from JSON thanks to Jackson library
```

---

## Data Flow

### Complete Tax Calculation Flow

```
1. USER UPLOADS FILE
   ↓
2. API RECEIVES REQUEST
   POST /api/v1/calculations/upload
   ↓
3. SPRING BOOT ROUTES TO CONTROLLER
   CalculationController.upload()
   ↓
4. CONTROLLER VALIDATES INPUT
   - Check file is .xlsx
   - Check financial_year is valid
   - Check quarter_scheme exists
   ↓
5. CONTROLLER CALLS SERVICE
   TaxCalculationService.calculateFromFile()
   ↓
6. SERVICE LOADS FILE
   POI reads Excel file into memory
   ↓
7. SERVICE DETECTS BROKER
   BrokerDetector.detect()
   - Analyzes headers
   - Compares against known formats
   - Returns: UPSTOX / ZERODHA / etc.
   ↓
8. SERVICE DETECTS COLUMN MAPPING
   ColumnMapper.mapColumns()
   - Finds: Buy column, Sell column, Date column, etc.
   - Creates mapping: INDEX_BUY=8, INDEX_SELL=12, etc.
   ↓
9. SERVICE READS DATA ROWS
   For each row 25-297:
   ↓
10. CREATE SECURITY OBJECT
    From row data:
    - Buy Amount: POI.getCell(8).getValue()
    - Sell Amount: POI.getCell(12).getValue()
    - Date: POI.getCell(9).getValue()
    - Holding Days: POI.getCell(13).getValue()
    
    → new Security(buyAmt, sellAmt, date, daysHeld)
    ↓
11. DETERMINE TAX CATEGORY
    If daysHeld == 0:
      → Speculation (intraday)
    Else:
      → STCG (short-term capital gain)
    ↓
12. ALLOCATE TO QUARTER
    If STCG:
      quarterConfig.getQuarter(saleDate)
      → Q1 / Q2 / Q3 / Q4 / Q5
    ↓
13. ACCUMULATE RESULTS
    STCG per quarter += profit
    Total Speculation turnover += abs(profit)
    ↓
14. BUILD RESPONSE OBJECT
    TaxCalculationResponse response = new TaxCalculationResponse()
    response.setFinancialYear("FY 2021-22")
    response.setStcg(stcgBreakdown)
    response.setSpeculation(speculationBreakdown)
    response.setQuarterly(quarterlyBreakdown)
    ↓
15. CONTROLLER CONVERTS TO JSON
    Jackson library automatically converts:
    TaxCalculationResponse → JSON string
    ↓
16. SPRING BOOT SENDS RESPONSE
    HTTP 200 OK
    Content-Type: application/json
    {
      "financial_year": "FY 2021-22",
      "stcg": {
        "total_stcg": 512.41,
        "q1": 0.0,
        "q2": 0.0,
        "q3": 1644.20,
        "q4": -1131.79,
        "q5": 0.0
      },
      "speculation": {...},
      ...
    }
    ↓
17. CLIENT RECEIVES RESPONSE
    JavaScript / Python / Java client parses JSON
    Displays results to user
```

---

## Common Questions

### Q1: Why use classes if we could use functions?

**A:** 
Classes group related data and functions together, making code:
- **Organized:** Related things live together
- **Reusable:** One Security class used throughout
- **Testable:** Easy to create mock objects for tests
- **Maintainable:** Changes in one place affect all uses

```java
// Without classes (messy):
double buyAmt, sellAmt; int daysHeld;
String determineTaxCategory(double buy, double sell, int days) {...}
int getQuarter(double buy, double sell, int days, Date date) {...}
// Hard to keep these synchronized

// With classes (clean):
Security security = new Security(buyAmt, sellAmt, daysHeld, date);
security.getTaxCategory();  // Always consistent
security.getQuarter(fy);
```

### Q2: Why use Spring Boot instead of plain Java?

**A:**
Spring Boot handles the boilerplate:
- ✅ HTTP request routing
- ✅ JSON serialization
- ✅ Error handling
- ✅ CORS (cross-origin requests)
- ✅ Request validation
- ✅ Logging

Without Spring: 500+ lines of manual setup. With Spring: 0 lines.

### Q3: Why separate Controller, Service, Repository layers?

**A:** Separation of Concerns = Each layer has one responsibility

```
CONTROLLER:     HTTP handling only
                ↓
SERVICE:        Business logic only
                ↓
REPOSITORY:     Database queries only
                ↓
DATABASE:       Data storage
```

**Benefits:**
- Change HTTP format (REST → GraphQL)? Only change Controller
- Change business logic? Only change Service
- Change database (MySQL → Postgres)? Only change Repository
- Test Service without HTTP? Use mock Controller

### Q4: Why Excel instead of database?

**A:**
- **Users know Excel:** No learning curve
- **Easy to share:** Email as attachment
- **Local-first:** No server needed
- **Familiar format:** Brokers already export Excel

Could evolve to database later, but Excel is right choice for MVP.

### Q5: How do I add a new broker?

**A:**
1. Understand new broker's Excel format (get sample file)
2. Create `NewBrokerDetectionStrategy.java`
3. Implement column detection logic
4. Register in `BrokerDetector.java`
5. Add test cases

### Q6: Why does the code have so many comments?

**A:**
Comments explain the "why", not the "what":

❌ **Bad comments:**
```java
i++;  // Increment i
```

✅ **Good comments:**
```java
// Skip header row (row 0 contains column names)
startRow = 1;
```

---

## Summary

### Technologies Used & Why

| Technology | Purpose | Why? |
|----------|---------|------|
| **Java 17** | Programming language | Type-safe, cross-platform, widely used |
| **Maven** | Build & dependency management | Reproducible builds, standard structure |
| **Spring Boot** | Web framework | REST APIs without boilerplate |
| **Apache POI** | Excel processing | Read/write .xlsx files reliably |
| **Log4j 2** | Logging | Track execution, debug issues |
| **Jackson** | JSON serialization | Convert objects to/from JSON |
| **JavaFX** | GUI framework | Desktop application UI |

### Architectural Principles

1. **Layered Architecture:** Each layer (Controller → Service → Core) has single responsibility
2. **Separation of Concerns:** HTTP, Business Logic, Data are separate
3. **Dependency Injection:** Objects receive dependencies instead of creating them
4. **Design Patterns:** Factory, Strategy, Singleton, DTO for code reuse
5. **Testability:** Each layer can be tested independently

### Key Takeaways

- ✅ Small problem (tax calculation) → Organized architecture prevents chaos as code grows
- ✅ Technologies chosen for maturity, stability, and community support
- ✅ Multiple interfaces (CLI, Web API, Desktop GUI) for same core logic
- ✅ Clean separation allows easy modifications and testing
- ✅ Thorough logging helps debug production issues

---

## Next Steps to Learn More

1. **Read:** [SETUP.md](../SETUP.md) - Installation guide
2. **Read:** [README.md](../README.md) - Project overview
3. **Explore:** `src/bootstrap/InvestingHurdleBootstrapper.java` - Entry point
4. **Explore:** `spring-api/src/main/java/.../CalculationController.java` - REST endpoints
5. **Run:** The application locally and trace execution
6. **Modify:** Add a new broker detection strategy
7. **Test:** Write unit tests for tax calculation logic

---

**Happy Learning! 🚀**

This project is an excellent example of real-world software architecture. Use it as a reference for your own projects.
