# InvestingHurdle - Improvements & Enhancement Plan

A comprehensive roadmap for improving the tax_hurdle application with better code quality, maintainability, and functionality.

---

## Priority 1: Critical Improvements

### 1.1 FIFO Cost Basis Calculation
**Status**: Not Started  
**Impact**: Critical - Current calculation may not comply with tax regulations  
**Effort**: Medium

#### Overview
Replace the simple summation-based cost calculation with a proper FIFO (First-In-First-Out) cost basis method, which tracks buy/sell orders chronologically.

#### Implementation Steps
1. **Create FIFOCalculator class** (`src/util/FIFOCalculator.java`)
   - Maintain a queue/stack of buy orders with quantities and amounts
   - Match sell orders against buy orders in chronological order
   - Calculate cost basis for each sell transaction

2. **Modify Security class**
   - Add fields: `buyOrderId`, `sellOrderId`, `quantity`, `unitCost`
   - Track FIFO allocation details

3. **Update EquityLoader**
   - Use FIFOCalculator instead of simple summation
   - Process transactions in chronological order
   - Update cost of acquisition based on actual FIFO matching

4. **Add unit tests**
   - Test FIFO matching logic
   - Test edge cases (partial fills, multiple buys for one sell, etc.)

#### Code Example
```java
public class FIFOCalculator {
    private Queue<BuyOrder> pendingBuys = new LinkedList<>();
    
    public void addBuyOrder(LocalDate date, double quantity, double amount) {
        pendingBuys.offer(new BuyOrder(date, quantity, amount));
    }
    
    public FIFOAllocation calculateCostBasis(LocalDate date, double sellQuantity, double sellAmount) {
        // Match against pending buys in FIFO order
        List<BuyOrderMatch> matches = new ArrayList<>();
        double remainingSellQty = sellQuantity;
        double totalCost = 0;
        
        // Implementation...
        
        return new FIFOAllocation(matches, totalCost);
    }
}
```

#### Files to Modify
- Create: `src/util/FIFOCalculator.java`
- Create: `src/util/FIFOAllocation.java`
- Modify: [src/params/EquityLoader.java](src/params/EquityLoader.java)
- Modify: [src/security/Security.java](src/security/Security.java)

---

### 1.2 Replace java.util.Date with LocalDate
**Status**: Not Started  
**Impact**: Critical - java.util.Date is deprecated, improves code safety  
**Effort**: Medium

#### Overview
Migrate from legacy `java.util.Date` to modern `java.time.LocalDate` API for better date handling and immutability.

#### Implementation Steps
1. **Update imports** across all packages
   - Replace `java.util.Date` with `java.time.LocalDate`
   - Replace `java.text.SimpleDateFormat` with `java.time.format.DateTimeFormatter`

2. **Modify EquityLoader**
   - Update `INDEX_SELL_DATE` parsing to use `LocalDate.parse()`
   - Update `returnQuarter()` method to use LocalDate comparisons
   - Update date range logic in quarterly allocation

3. **Modify Security class**
   - Change date fields from `Date` to `LocalDate`
   - Update getters/setters

4. **Update HurdleConstant**
   - Define date format constants using `DateTimeFormatter`
   - Define quarter boundaries as `LocalDate` instances

5. **Update HurdleLogger**
   - Ensure date logging uses ISO format

#### Code Example
```java
// Before
private Date sellDate;
public Date getSellDate() { return sellDate; }

// After
private LocalDate sellDate;
public LocalDate getSellDate() { return sellDate; }

// Parsing
// Before: new SimpleDateFormat("dd-MM-yyyy").parse(dateString)
// After:
LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

// Quarter comparison
// Before: sellDate.after(q1Start) && sellDate.before(q1End)
// After: !sellDate.isBefore(q1Start) && !sellDate.isAfter(q1End)
```

#### Files to Modify
- [src/params/EquityLoader.java](src/params/EquityLoader.java)
- [src/security/Security.java](src/security/Security.java)
- [src/util/HurdleConstant.java](src/util/HurdleConstant.java)
- [src/logging/HurdleLogger.java](src/logging/HurdleLogger.java)

---

### 1.3 Fix Exception Handling
**Status**: Not Started  
**Impact**: Critical - Current code crashes on errors with no recovery  
**Effort**: Small

#### Overview
Implement proper exception handling with logging and graceful error reporting instead of letting exceptions bubble up.

#### Implementation Steps
1. **Update InvestingHurdleBootstrapper**
   ```java
   public static void main(String[] args) {
       try {
           InvestingHurdleBootstrapper bootstrapper = getInstance();
           bootstrapper.initialize();
       } catch (InvalidSecurityException e) {
           HurdleLogger.error("Security validation failed: " + e.getMessage());
           System.exit(1);
       } catch (IOException e) {
           HurdleLogger.error("File I/O error: " + e.getMessage());
           System.exit(2);
       } catch (Exception e) {
           HurdleLogger.error("Unexpected error: " + e.getMessage(), e);
           System.exit(3);
       }
   }
   ```

2. **Enhance InvalidSecurityException**
   - Add error codes (INVALID_DATA, MISSING_FILE, PARSE_ERROR, etc.)
   - Add detailed error context
   - Improve error messages

3. **Update EquityLoader**
   - Wrap Excel parsing in try-catch
   - Validate data before processing
   - Provide meaningful error messages for each validation failure

4. **Add validation in loaders**
   - Check if configuration files exist before opening
   - Validate Excel structure
   - Handle missing/corrupted data gracefully

#### Code Example
```java
public class InvalidSecurityException extends RuntimeException {
    public enum ErrorCode {
        INVALID_DATA("Data format is invalid"),
        MISSING_FILE("Configuration file not found"),
        PARSE_ERROR("Failed to parse Excel data"),
        INVALID_DATE("Invalid date format"),
        MISSING_COLUMN("Required column not found");
        
        private final String message;
        ErrorCode(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
    
    private ErrorCode code;
    private String context;
    
    public InvalidSecurityException(ErrorCode code, String context) {
        super(code.getMessage() + ": " + context);
        this.code = code;
        this.context = context;
    }
}
```

#### Files to Modify
- [src/bootstrap/InvestingHurdleBootstrapper.java](src/bootstrap/InvestingHurdleBootstrapper.java)
- [src/exception/InvalidSecurityException.java](src/exception/InvalidSecurityException.java)
- [src/params/EquityLoader.java](src/params/EquityLoader.java)

---

## Priority 2: Important Improvements

### 2.1 Externalize Quarter Configuration
**Status**: Not Started  
**Impact**: Important - Hardcoded dates limit flexibility  
**Effort**: Small

#### Overview
Move hardcoded quarter definitions from EquityLoader to external configuration, allowing easy modification for different financial years.

#### Implementation Steps
1. **Create QuarterConfig.java** (`src/util/QuarterConfig.java`)
   ```java
   public class QuarterConfig {
       private String financialYear;
       private List<Quarter> quarters;
       
       public static QuarterConfig loadFromFile(String configPath) {
           // Load from properties or JSON file
       }
   }
   
   public class Quarter {
       private String name;        // Q1, Q2, etc.
       private LocalDate startDate;
       private LocalDate endDate;
   }
   ```

2. **Create configuration file** (`configuration/quarters.properties`)
   ```properties
   # Financial Year 2021-22
   fy.year=2021-22
   
   Q1.name=Q1 (Apr-Jun)
   Q1.start=2021-04-01
   Q1.end=2021-06-15
   
   Q2.name=Q2 (Jun-Sep)
   Q2.start=2021-06-16
   Q2.end=2021-09-15
   
   Q3.name=Q3 (Sep-Dec)
   Q3.start=2021-09-16
   Q3.end=2021-12-15
   
   Q4.name=Q4 (Dec-Mar)
   Q4.start=2021-12-16
   Q4.end=2022-03-15
   
   Q5.name=Q5 (Mar-Mar)
   Q5.start=2022-03-16
   Q5.end=2022-03-31
   ```

3. **Update EquityLoader**
   - Load quarter configuration instead of using hardcoded dates
   - Use QuarterConfig in `returnQuarter()` method

4. **Support multiple financial years**
   - Allow switching FY via configuration
   - Validate quarters for selected FY

#### Files to Modify/Create
- Create: `src/util/QuarterConfig.java`
- Create: `src/util/Quarter.java`
- Create: `configuration/quarters.properties`
- Modify: [src/params/EquityLoader.java](src/params/EquityLoader.java)

---

### 2.2 Parameterize File Paths
**Status**: Not Started  
**Impact**: Important - Hardcoded paths reduce portability  
**Effort**: Small

#### Overview
Replace hardcoded file paths with configurable parameters via properties files or command-line arguments.

#### Implementation Steps
1. **Create ConfigFileManager.java** (`src/util/ConfigFileManager.java`)
   ```java
   public class ConfigFileManager {
       private Properties properties;
       
       public String getTaxWorkbookPath() { return getProperty("tax.workbook.path"); }
       public String getConfigWorkbookPath() { return getProperty("config.workbook.path"); }
       public String getOutputDirectory() { return getProperty("output.directory"); }
       
       public static ConfigFileManager loadFromFile(String configPath) { ... }
       public static ConfigFileManager fromCommandLine(String[] args) { ... }
   }
   ```

2. **Create configuration file** (`configuration/app.properties`)
   ```properties
   # File Paths
   tax.workbook.path=./configuration/tax_2122_.xlsx
   config.workbook.path=./configuration/configuration_stock.xlsx
   
   # Output Configuration
   output.directory=./output
   output.enable.csv=true
   output.enable.json=true
   
   # Logging
   log.level=INFO
   log.file.path=./logs/hurdle.log
   ```

3. **Update HurdleConstant**
   - Replace hardcoded paths with lazy-loaded from ConfigFileManager
   - Keep defaults for backward compatibility

4. **Add command-line argument support**
   ```bash
   java -cp "bin;lib/*" bootstrap.InvestingHurdleBootstrapper \
       --tax-file /path/to/tax_2122_.xlsx \
       --output-dir /path/to/output
   ```

5. **Update InvestingHurdleBootstrapper**
   - Parse command-line arguments
   - Load configuration from file or command-line
   - Pass configuration to loaders

#### Files to Modify/Create
- Create: `src/util/ConfigFileManager.java`
- Create: `configuration/app.properties`
- Modify: [src/util/HurdleConstant.java](src/util/HurdleConstant.java)
- Modify: [src/bootstrap/InvestingHurdleBootstrapper.java](src/bootstrap/InvestingHurdleBootstrapper.java)

---

### 2.3 Add Input Validation
**Status**: Not Started  
**Impact**: Important - Prevents invalid data from causing silent errors  
**Effort**: Medium

#### Overview
Create comprehensive input validation for Excel data before processing.

#### Implementation Steps
1. **Create DataValidator.java** (`src/util/DataValidator.java`)
   ```java
   public class DataValidator {
       public static ValidationResult validateDate(String dateString, String format) { ... }
       public static ValidationResult validateAmount(String amount) { ... }
       public static ValidationResult validateHoldingDays(String days) { ... }
       public static ValidationResult validateRow(Row row, int[] requiredColumns) { ... }
   }
   
   public class ValidationResult {
       private boolean valid;
       private String errorMessage;
   }
   ```

2. **Define validation rules**
   - Date: Must be valid date in format dd-MM-yyyy
   - Amount: Must be positive number, max 2 decimal places
   - Holding days: Must be non-negative integer
   - Quantity: Must be positive number
   - Symbol: Must not be empty/null

3. **Update EquityLoader**
   - Validate each row before processing
   - Collect all validation errors
   - Report validation summary at end
   - Skip invalid rows with warnings or fail fast based on config

4. **Add validation logging**
   ```
   INFO: Validating 273 rows...
   WARN: Row 50: Invalid date format "32-13-2021" - skipped
   WARN: Row 75: Negative amount "-5000" - skipped
   INFO: Validation complete: 271 rows valid, 2 skipped, 0 errors
   ```

#### Files to Modify/Create
- Create: `src/util/DataValidator.java`
- Create: `src/util/ValidationResult.java`
- Modify: [src/params/EquityLoader.java](src/params/EquityLoader.java)

---

## Priority 3: Feature Enhancements

### 3.1 Report Export (CSV)
**Status**: Not Started  
**Impact**: Enhancement - Enables integration with other tools  
**Effort**: Small

#### Overview
Add CSV export capability for tax calculations and transaction details.

#### Implementation Steps
1. **Create CSVReportExporter.java** (`src/util/CSVReportExporter.java`)
   ```java
   public class CSVReportExporter implements ReportExporter {
       public void exportSummaryReport(File outputFile, TaxCalculation calc) { ... }
       public void exportDetailedTransactions(File outputFile, List<Security> securities) { ... }
   }
   ```

2. **Generate reports**
   - **Summary Report**: STCG totals by quarter, Speculation turnover, P&L summary
   - **Transaction Report**: All buy/sell details, cost basis, profit/loss per transaction
   - **Quarterly Report**: Quarter-wise STCG breakdown

3. **CSV Output Samples**

   **summary_report.csv**
   ```csv
   Report Type,Summary Report
   Generated Date,2025-12-31
   Financial Year,2021-22
   
   STCG Summary
   Quarter,Amount
   Q1,0.00
   Q2,0.00
   Q3,1644.20
   Q4,-1131.79
   Q5,0.00
   Total,512.41
   
   Speculation Summary
   Metric,Amount
   Total Turnover,39280.20
   Total Profit/Loss,-16618.60
   Number of Transactions,145
   ```

   **transactions_report.csv**
   ```csv
   Trade Date,Symbol,Buy Qty,Buy Price,Buy Amount,Sell Qty,Sell Price,Sell Amount,Holding Days,STCG,Speculation
   2021-04-01,INFY,10,1500.00,15000.00,10,1510.00,15100.00,7,100.00,0.00
   2021-04-02,TCS,5,3500.00,17500.00,5,3490.00,17450.00,0,0.00,-50.00
   ```

4. **Update EquityLoader**
   - Collect transaction details for export
   - Generate reports after calculation

5. **Add command-line option**
   ```
   --export-csv ./output/report.csv
   ```

#### Files to Modify/Create
- Create: `src/util/ReportExporter.java` (interface)
- Create: `src/util/CSVReportExporter.java`
- Modify: [src/params/EquityLoader.java](src/params/EquityLoader.java)
- Modify: [src/bootstrap/InvestingHurdleBootstrapper.java](src/bootstrap/InvestingHurdleBootstrapper.java)

---

### 3.2 Report Export (JSON)
**Status**: Not Started  
**Impact**: Enhancement - Enables API integration and data interchange  
**Effort**: Medium

#### Overview
Add JSON export capability for structured data export and API integration.

#### Implementation Steps
1. **Create JSONReportExporter.java** (`src/util/JSONReportExporter.java`)
   ```java
   public class JSONReportExporter implements ReportExporter {
       public void exportSummaryReport(File outputFile, TaxCalculation calc) { ... }
       public void exportDetailedTransactions(File outputFile, List<Security> securities) { ... }
   }
   ```

2. **Add JSON dependency** to `lib/`
   - Use built-in `java.util.json` or add `com.google.gson` or `com.fasterxml.jackson`

3. **JSON Output Sample**
   ```json
   {
     "reportMetadata": {
       "generatedDate": "2025-12-31T10:30:00Z",
       "financialYear": "2021-22",
       "totalTransactions": 273,
       "validTransactions": 271,
       "invalidTransactions": 2
     },
     "stcgSummary": {
       "totalSellValue": 447343.86,
       "totalCostOfAcquisition": 446831.45,
       "totalSTCG": 512.41,
       "quarterlyBreakdown": {
         "Q1": 0.00,
         "Q2": 0.00,
         "Q3": 1644.20,
         "Q4": -1131.79,
         "Q5": 0.00
       }
     },
     "speculationSummary": {
       "totalTurnover": 39280.20,
       "totalProfit": -16618.60,
       "transactionCount": 145
     },
     "transactions": [
       {
         "date": "2021-04-01",
         "symbol": "INFY",
         "buyQty": 10,
         "buyPrice": 1500.00,
         "buyAmount": 15000.00,
         "sellQty": 10,
         "sellPrice": 1510.00,
         "sellAmount": 15100.00,
         "holdingDays": 7,
         "stcg": 100.00,
         "speculation": 0.00
       }
     ]
   }
   ```

4. **Add command-line option**
   ```
   --export-json ./output/report.json
   ```

#### Files to Modify/Create
- Create: `src/util/JSONReportExporter.java`
- Modify: [src/params/EquityLoader.java](src/params/EquityLoader.java)
- Modify: [src/bootstrap/InvestingHurdleBootstrapper.java](src/bootstrap/InvestingHurdleBootstrapper.java)

---

### 3.3 Add Unit Tests
**Status**: Not Started  
**Impact**: Enhancement - Ensures code reliability and prevents regressions  
**Effort**: High

#### Overview
Create comprehensive unit test suite using JUnit 5 with 70%+ code coverage.

#### Implementation Steps
1. **Setup JUnit 5** in project
   - Add junit-jupiter-api and junit-jupiter-engine to `lib/`
   - Create `tests/` directory

2. **Create test classes**
   ```
   tests/
   ├── util/
   │   ├── FIFOCalculatorTest.java
   │   ├── DataValidatorTest.java
   │   ├── QuarterConfigTest.java
   │   └── ConfigFileManagerTest.java
   ├── params/
   │   ├── EquityLoaderTest.java
   │   └── WorkbookLoaderTest.java
   ├── exception/
   │   └── InvalidSecurityExceptionTest.java
   └── integration/
       └── EndToEndTest.java
   ```

3. **Test coverage targets**
   - **EquityLoader**: 80%+ (critical logic)
   - **FIFOCalculator**: 90%+ (financial calculations)
   - **DataValidator**: 85%+ (validation rules)
   - **Utilities**: 75%+ (helper functions)
   - **Overall**: 70%+

4. **Sample test**
   ```java
   class FIFOCalculatorTest {
       private FIFOCalculator calculator;
       
       @BeforeEach
       void setUp() {
           calculator = new FIFOCalculator();
       }
       
       @Test
       void testSimpleFIFOAllocation() {
           calculator.addBuyOrder(LocalDate.of(2021, 4, 1), 10, 1500);
           FIFOAllocation result = calculator.calculateCostBasis(
               LocalDate.of(2021, 4, 5), 5, 7500);
           assertEquals(750, result.getTotalCost());
       }
   }
   ```

5. **Setup test data**
   - Create sample Excel files for testing
   - Create fixtures for common test scenarios

6. **Configure test runner**
   - Add test compilation to build script
   - Setup CI/CD to run tests

#### Files to Create
- Create: `tests/` directory with test suite
- Create: `pom.xml` or build configuration for Maven/Gradle (optional)
- Modify: Build scripts to include tests

---

## Priority 3: Future Enhancements (Roadmap)

### 3.4 Future Enhancements Documentation
**Status**: Not Started  
**Effort**: Small

Create `ROADMAP.md` documenting potential future improvements:

1. **Multi-Year Support**
   - Process multiple financial years in single run
   - Consolidated reports across years
   - Year-over-year comparisons

2. **Multiple Tax Regimes**
   - Support different country tax rules (India, US, Singapore, etc.)
   - Customizable tax rules per regime
   - Tax optimization suggestions

3. **Graphical User Interface**
   - JavaFX or Swing-based GUI
   - Interactive transaction browser
   - Real-time calculation preview

4. **Database Integration**
   - Store transactions in SQLite/MySQL
   - Historical data tracking
   - Data persistence and recovery

5. **REST API**
   - Spring Boot REST endpoints
   - File upload for Excel processing
   - JSON API for tax calculations

6. **Performance Optimization**
   - Support for large Excel files (10000+ transactions)
   - Parallel processing for multiple years
   - In-memory caching strategies

7. **Advanced Features**
   - Cost indexation for long-term gains
   - Tax loss harvesting analysis
   - Dividend tracking
   - Corporate action adjustments (splits, mergers)

8. **Reporting Enhancements**
   - PDF report generation
   - HTML interactive reports
   - Email delivery of reports
   - Scheduled batch processing

---

## Implementation Priority & Timeline

### Recommended Implementation Order

**Week 1-2: Priority 1 (Critical)**
1. Replace java.util.Date with LocalDate (P1.2)
2. Fix exception handling (P1.3)
3. Implement FIFO calculation (P1.1)

**Week 3: Priority 2 (Important)**
4. Add input validation (P2.3)
5. Externalize quarter configuration (P2.1)
6. Parameterize file paths (P2.2)

**Week 4-5: Priority 3 (Enhancements)**
7. CSV export functionality (P3.1)
8. JSON export functionality (P3.2)
9. Unit tests (P3.3)
10. Future roadmap documentation (P3.4)

---

## Resources & Dependencies

### New Dependencies to Add

| Library | Version | Purpose | File Location |
|---------|---------|---------|--------|
| JUnit 5 Jupiter | 5.9+ | Unit testing | `lib/junit-jupiter-api-5.9.x.jar` |
| JUnit 5 Engine | 5.9+ | Test runner | `lib/junit-jupiter-engine-5.9.x.jar` |
| Gson (Optional) | 2.8+ | JSON processing | `lib/gson-2.8.x.jar` |
| Commons CSV | 1.9+ | CSV processing | `lib/commons-csv-1.9.jar` |

### Build System Upgrade (Optional)
- Consider Maven or Gradle for dependency management
- Simplifies library management vs. manual JAR files

---

## Success Metrics

- [x] Code compiles without warnings
- [ ] 70%+ unit test coverage
- [ ] Exception handling covers 100% of error paths
- [ ] All hardcoded values externalized
- [ ] Support for multiple financial years
- [ ] CSV/JSON export functional
- [ ] Performance handles 5000+ transactions
- [ ] Documentation updated for all changes

---

## Getting Started

1. Review Priority 1 improvements first
2. Use the todo list in this guide to track progress
3. Create feature branches for each improvement:
   ```bash
   git checkout -b feature/p1-localdate-migration
   git checkout -b feature/p1-fifo-calculation
   git checkout -b feature/p1-exception-handling
   ```
4. Write tests as you implement features
5. Update documentation with each change

---

**Document Created**: December 31, 2025  
**Last Updated**: December 31, 2025
