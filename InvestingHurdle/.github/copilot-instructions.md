# InvestingHurdle - AI Coding Instructions

## Project Overview
InvestingHurdle is a Java-based tax calculation tool for equity trading in India. It processes Excel workbooks containing trading data to calculate **STCG (Short-Term Capital Gains)** and **Speculation (Intraday) turnover** for tax reporting purposes.

## Architecture

### Core Components
- **InvestingHurdleBootstrapper** ([src/bootstrap/InvestingHurdleBootstrapper.java](src/bootstrap/InvestingHurdleBootstrapper.java)): Singleton entry point managing the application lifecycle and coordinating loaders
- **EquityLoader** ([src/params/EquityLoader.java](src/params/EquityLoader.java)): Primary loader for tax calculations from `tax_2122_.xlsx`
- **WorkbookLoader** ([src/params/WorkbookLoader.java](src/params/WorkbookLoader.java)): Alternate loader for buy/sell data from `configuration_stock.xlsx` (currently unused in main flow)
- **Security** ([src/security/Security.java](src/security/Security.java)): Domain model representing individual stock transactions

### Data Flow
1. Main entry point initializes `EquityLoader` (not `WorkbookLoader` - that's commented out)
2. `EquityLoader.initialize()` reads Excel from `./configuration/tax_2122_.xlsx`
3. Processes rows 25-297 from sheet index 1 (equities sheet)
4. Calculates quarterly STCG (Q1-Q5) and intraday turnover
5. Outputs formatted tax calculations to console

## Excel Processing Patterns

### Column Indices (EquityLoader)
- `INDEX_BUYAMT = 8`: Buy amount column
- `INDEX_SELLAMT = 12`: Sell amount column  
- `INDEX_DAYS_HOLD = 13`: Holding period (0 = intraday)
- `INDEX_SELL_DATE = 9`: Sale date for quarterly allocation
- `INDEX_STCG = 15`: STCG value
- `INDEX_SPECUL = 17`: Speculation/intraday turnover

### Key Logic
- **Intraday vs STCG**: Determined by `INDEX_DAYS_HOLD == 0` (intraday) vs `> 0` (STCG)
- **Quarterly Allocation**: `returnQuarter()` uses hardcoded date ranges for FY 2021-22:
  - Q1: April 1 - June 15, 2021
  - Q2: June 16 - Sept 15, 2021
  - Q3: Sept 16 - Dec 15, 2021
  - Q4: Dec 16 - March 15, 2022
  - Q5: March 16 - March 31, 2022
- **Cell Parsing**: All cells converted to `CellType.STRING` then parsed to avoid type errors

## Configuration

### File Paths (HurdleConstant)
- `TAX_CONFIG_FILE_PATH = "./configuration/tax_2122_.xlsx"`: Active tax calculation workbook
- `CONFIGURATION_FILE_PATH = "./configuration/configuration_stock.xlsx"`: Alternate workbook (unused in current flow)

### Dependencies (Eclipse .classpath)
Project uses Eclipse with JavaSE-15 and external JARs:
- **Apache POI 5.2.2**: Excel processing (`poi-ooxml-5.2.2.jar` for .xlsx)
- **Log4j 2.17.2**: Logging framework
- **Commons libraries**: collections4-4.1, io-2.11.0, compress-1.21

**Important**: JAR paths are absolute and user-specific. New developers need to update `.classpath` with their local JAR locations.

## Running the Application

### Build & Execute
```bash
# Compile (if using command line instead of Eclipse)
javac -cp "path/to/poi-jars/*:path/to/log4j/*" -d bin src/**/*.java

# Run
java -cp "bin:path/to/poi-jars/*:path/to/log4j/*" bootstrap.InvestingHurdleBootstrapper
```

### Expected Output Format
```
***************** WELCOME TO THE INVESTING WORLD... ********************
Initializing Equity Loader...

$$$$$$$$$********  STCG  ********$$$$$$$$$
Full Value of consideration : <total_sell>
Cost of acquisition : <total_buy>
STCG = <profit/loss>
STCG Q1 = <quarterly_value>
...
$$$$$$$$$********  SPECULATION  ********$$$$$$$$$
Turnover total intraday : <turnover>
```

## Common Modifications

### Updating Tax Year
1. Modify `HurdleConstant.TAX_CONFIG_FILE_PATH` for new Excel file
2. Update `returnQuarter()` date ranges in `EquityLoader.java`
3. Adjust `START_ROW` and `END_ROW` if Excel structure changes

### Changing Excel Structure
- Column indices are hardcoded constants - update `INDEX_*` fields in respective loaders
- Sheet indices: `INDEX_EQUITES = 1` (EquityLoader), `INDEX_BUY_SHEET = 0` / `INDEX_SELL_SHEET = 1` (WorkbookLoader)

## Error Handling
- `InvalidSecurityException`: Custom unchecked exception for missing/invalid Excel data
- No try-catch in main flow - exceptions bubble up and crash with stack trace
- Cell validation pattern: Check null → convert to STRING → check empty → parse
