# InvestingHurdle - Usage Guide

## Running the Application

### Basic Usage (Default Configuration)
```bash
java -cp "bin;lib/*" bootstrap.InvestingHurdleBootstrapper
```

### Using Properties File
```bash
java -cp "bin;lib/*" bootstrap.InvestingHurdleBootstrapper --config-props ./configuration/application.properties
```

### Using Command-Line Arguments

#### Specify Financial Year
```bash
java -cp "bin;lib/*" bootstrap.InvestingHurdleBootstrapper --financial-year "FY 2022-23"
```

#### Specify Custom Tax File
```bash
java -cp "bin;lib/*" bootstrap.InvestingHurdleBootstrapper --tax-file ./configuration/tax_2223_.xlsx --financial-year "FY 2022-23"
```

#### Multiple Arguments
```bash
java -cp "bin;lib/*" bootstrap.InvestingHurdleBootstrapper \
  --tax-file ./configuration/tax_2223_.xlsx \
  --financial-year "FY 2022-23" \
  --output-dir ./output/fy2223 \
  --log-level DEBUG
```

## Command-Line Arguments

| Argument | Description | Example |
|----------|-------------|---------|
| `--config-props <path>` | Properties file for configuration | `--config-props config.properties` |
| `--tax-file <path>` | Path to tax workbook Excel file | `--tax-file ./configuration/tax_2223_.xlsx` |
| `--config-file <path>` | Path to alternate configuration file | `--config-file ./configuration/config.xlsx` |
| `--financial-year <fy>` | Financial year for calculations | `--financial-year "FY 2022-23"` |
| `--output-dir <path>` | Output directory for reports | `--output-dir ./output/reports` |
| `--log-level <level>` | Logging level (DEBUG, INFO, WARN, ERROR) | `--log-level DEBUG` |

## Configuration Priority

1. **Command-line arguments** (highest priority)
2. **Properties file** (specified via --config-props)
3. **Default values** (lowest priority)

## Enhancements Implemented

### 1. ConfigFileManager
- Centralized configuration management
- Support for properties files and command-line arguments
- Default values for all settings

### 2. QuarterConfig
- Factory methods for different financial years:
  - `QuarterConfig.createFY202122()` - FY 2021-22
  - `QuarterConfig.createFY202223()` - FY 2022-23
- Dynamic quarter detection based on transaction dates
- Custom quarter configuration support

### 3. DataValidator
- Input validation for:
  - Dates (multiple formats)
  - Amounts (positive numbers)
  - Quantities (positive numbers)
  - Holding days (non-negative integers)
  - Stock symbols (non-empty strings)

### 4. QuarterlyBreakdown
- Structured quarterly STCG breakdown
- Automatic total calculation
- Individual quarter access methods

### 5. Enhanced Error Handling
- InvalidSecurityException with error codes:
  - MISSING_FILE (exit code 2)
  - INVALID_DATA (exit code 3)
  - PARSE_ERROR (exit code 3)
  - IO_ERROR (exit code 4)
- Comprehensive logging with HurdleLogger

### 6. FIFOCalculator (Available but not yet integrated)
- FIFO cost basis calculation
- Buy/sell order matching
- Pending order queue management

## Output Format

The application outputs three main sections:

### 1. STCG (Short-Term Capital Gains)
- Full value of consideration (total sell amount)
- Cost of acquisition (total buy amount)
- Total STCG
- Quarterly breakdown (Q1-Q5)

### 2. SPECULATION (Intraday Trading)
- Full value of consideration
- Cost of acquisition
- Profit/Loss
- Total turnover

### 3. QUARTERLY BREAKDOWN
- Detailed quarter-wise STCG with date ranges
- Total STCG across all quarters

## Example Output

```
***************** WELCOME TO THE INVESTING WORLD... ********************

Quarter configuration: QuarterConfig [financialYear=FY 2021-22, quarters=5]
Initializing Equity Loader...

Equity loader initialized SUCCESSFULLY :)

$$$$$$$$$********  STCG  ********$$$$$$$$$

Full Value of consideration : 447343.86
Cost of acquisition : 446831.45
STCG = 512.41
STCG Q1 = 0.0
STCG Q2 = 0.0
STCG Q3 = 1644.2
STCG Q4 = -1131.79
STCG Q5 = 0.0

$$$$$$$$$********  SPECULATION  ********$$$$$$$$$

Full Value of consideration : 12447914.65
Cost of acquisition : 12464533.25
PL = -16618.60
Turnover total intraday : 39280.20

$$$$$$$$$********  QUARTERLY BREAKDOWN  ********$$$$$$$$$

Quarter 1 (Apr-Jun): ₹0.00
  Period: 2021-04-01 to 2021-06-15
Quarter 2 (Jun-Sep): ₹0.00
  Period: 2021-06-16 to 2021-09-15
Quarter 3 (Sep-Dec): ₹1644.20
  Period: 2021-09-16 to 2021-12-15
Quarter 4 (Dec-Mar): ₹-1131.79
  Period: 2021-12-16 to 2022-03-15
Quarter 5 (Mar-Mar): ₹0.00
  Period: 2022-03-16 to 2022-03-31

Total STCG across all quarters: ₹512.41

*-*-*-*-*-*-*-*-*-*-*-*-*-*-* END *-*-*-*-*-*-*-*-*-*-*-*-*-*-*
```

## Error Handling

The application uses structured error codes:

- **Exit Code 1**: General/unexpected error
- **Exit Code 2**: Missing file error
- **Exit Code 3**: Invalid data or parse error
- **Exit Code 4**: I/O error

Errors are logged to both console and log file (if configured).

## Next Steps

### Future Enhancements (see ROADMAP.md)
- Integrate FIFOCalculator for accurate cost basis
- Multi-asset class support (F&O, crypto, etc.)
- Report generation (PDF, Excel)
- Tax form pre-filling (ITR-2)
- Historical data analysis

## Notes

- All configuration files use UTF-8 encoding
- Excel files must be in .xlsx format (not .xls)
- Date format in Excel: DD/MM/YYYY
- Quarter boundaries are fixed (cannot be customized per transaction)
