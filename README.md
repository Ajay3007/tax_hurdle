# InvestingHurdle - Tax & Capital Gains Calculator

A Java-based application for calculating **Short-Term Capital Gains (STCG)** and **Speculation/Intraday Trading** profit/loss from equity trading data stored in Excel workbooks.

## 📋 Overview

This application processes equity trading data and calculates:
- **STCG (Short-Term Capital Gains)**: For stocks held less than 1 year
- **Speculation Income**: For intraday trading activities
- **Quarterly Breakdown**: Q1-Q5 STCG distribution
- **Turnover Calculations**: Total turnover for tax purposes

## 🏗️ Project Structure

```
InvestingHurdle/
├── src/                          # Source code
│   ├── bootstrap/
│   │   └── InvestingHurdleBootstrapper.java    # Main entry point
│   ├── exception/
│   │   └── InvalidSecurityException.java        # Custom exceptions
│   ├── logging/
│   │   └── HurdleLogger.java                    # Logging utility
│   ├── params/
│   │   ├── EquityLoader.java                    # Loads equity data from Excel
│   │   └── WorkbookLoader.java                  # Workbook processing
│   ├── security/
│   │   └── Security.java                        # Security/Stock entity
│   └── util/
│       └── HurdleConstant.java                  # Application constants
├── configuration/                # Configuration files
│   ├── configuration_stock.xlsx  # Stock portfolio data
│   └── tax_2122_.xlsx           # Tax calculation data
├── lib/                          # External JAR dependencies
│   ├── log4j-api-2.20.0.jar
│   ├── log4j-core-2.20.0.jar
│   ├── poi-5.2.3.jar
│   ├── poi-ooxml-5.2.3.jar
│   ├── poi-ooxml-lite-5.2.3.jar
│   ├── xmlbeans-5.1.1.jar
│   ├── commons-collections4-4.4.jar
│   ├── commons-compress-1.21.jar
│   ├── commons-io-2.11.0.jar
│   ├── commons-math3-3.6.1.jar
│   └── SparseBitSet-1.2.jar
├── bin/                          # Compiled classes (generated)
├── .classpath                    # Eclipse/VS Code classpath config
├── .project                      # Eclipse project file
└── README.md                     # This file
```

## 🔧 Dependencies

### Core Libraries

| Library | Version | Purpose |
|---------|---------|---------|
| Apache Log4j | 2.20.0 | Logging framework |
| Apache POI | 5.2.3 | Excel file processing (XLSX) |
| XMLBeans | 5.1.1 | XML parsing (POI dependency) |
| Commons Collections | 4.4 | Enhanced collection utilities |
| Commons Compress | 1.21 | Compression utilities |
| Commons IO | 2.11.0 | I/O utilities |
| Commons Math | 3.6.1 | Mathematical utilities |
| SparseBitSet | 1.2 | Efficient bit set implementation |

### Java Requirements
- **Java Version**: Java 8 or higher
- **JDK/JRE**: Required for compilation and execution

## 🚀 Getting Started

### Prerequisites

1. **Java Development Kit (JDK)** - Version 8+
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or use OpenJDK: https://adoptium.net/

2. **IDE** (Optional but recommended):
   - Visual Studio Code with Java Extension Pack
   - Eclipse IDE
   - IntelliJ IDEA

### Checkout the Project

#### Option 1: Using Git

```bash
git clone <repository-url>
cd TaxHrd/InvestingHurdle
```

#### Option 2: Download ZIP

1. Download the project ZIP file
2. Extract to your desired location
3. Navigate to `InvestingHurdle` directory

### Project Setup

#### For Visual Studio Code

1. **Install Java Extension Pack**:
   - Open VS Code
   - Go to Extensions (Ctrl+Shift+X)
   - Search for "Java Extension Pack"
   - Install it

2. **Open Project**:
   ```bash
   code /path/to/InvestingHurdle
   ```

3. **Configure Classpath**:
   - The `.classpath` file is already configured
   - If you encounter issues, press `Ctrl+Shift+P`
   - Type `Java: Clean Java Language Server Workspace`
   - Select `Restart and delete`

4. **Verify Libraries**:
   - Press `Ctrl+Shift+P`
   - Type `Java: Configure Classpath`
   - Ensure all JAR files from `lib/` folder are listed

#### For Eclipse IDE

1. **Import Project**:
   - File → Open Projects from File System
   - Select the `InvestingHurdle` directory
   - Click Finish

2. **Verify Build Path**:
   - Right-click project → Properties
   - Java Build Path → Libraries
   - All JARs from `lib/` should be present

#### For IntelliJ IDEA

1. **Open Project**:
   - File → Open
   - Select `InvestingHurdle` directory

2. **Add Libraries**:
   - File → Project Structure → Libraries
   - Click `+` → Java
   - Select all JARs from `lib/` folder

## ▶️ Running the Application

### Method 1: Using VS Code

1. Open [InvestingHurdleBootstrapper.java](src/bootstrap/InvestingHurdleBootstrapper.java)
2. Right-click in the editor
3. Select `Run Java`

OR

1. Press `F5` to debug
2. Select `Java` environment

### Method 2: Command Line (Windows)

```powershell
cd path\to\InvestingHurdle
java -cp "bin;lib\*" bootstrap.InvestingHurdleBootstrapper
```

### Method 3: Command Line (Linux/Mac)

```bash
cd path/to/InvestingHurdle
java -cp "bin:lib/*" bootstrap.InvestingHurdleBootstrapper
```

### Method 4: Compile and Run from Scratch

**Windows:**
```powershell
# Compile
javac -d bin -cp "lib\*" src\bootstrap\*.java src\params\*.java src\security\*.java src\util\*.java src\exception\*.java src\logging\*.java

# Run
java -cp "bin;lib\*" bootstrap.InvestingHurdleBootstrapper
```

**Linux/Mac:**
```bash
# Compile
javac -d bin -cp "lib/*" src/bootstrap/*.java src/params/*.java src/security/*.java src/util/*.java src/exception/*.java src/logging/*.java

# Run
java -cp "bin:lib/*" bootstrap.InvestingHurdleBootstrapper
```

## 📊 Expected Output

```
***************** WELCOME TO THE INVESTING WORLD... ********************
Initializing Equity Loader...

Equity loader initialized SUCCESSFULLY :)

$$$$$$$$$********  STCG  ********$$$$$$$$$

Full Value of consideration : 447343.86
Cost of acquisition : 446831.45
STCG = 512.41
STCG total : 512.41
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


*-*-*-*-*-*-*-*-*-*-*-*-*-*-* END *-*-*-*-*-*-*-*-*-*-*-*-*-*-*
```

## 🔧 Configuration

### Input Data Files

The application reads equity data from Excel files located in the `configuration/` folder:

1. **configuration_stock.xlsx**: Contains stock portfolio and trading data
2. **tax_2122_.xlsx**: Contains tax calculation configurations

### Modifying Input Files

- Edit the Excel files in `configuration/` folder
- Ensure the file structure matches the expected format:
  - Column indices are defined in `EquityLoader.java`
  - Row range: START_ROW = 25, END_ROW = 297 (adjustable)

### Changing File Paths

Edit [HurdleConstant.java](src/util/HurdleConstant.java) to modify configuration file paths:

```java
public static final String CONFIGURATION_FILE_PATH = "./configuration/configuration_stock.xlsx";
public static final String TAX_CONFIG_FILE_PATH = "./configuration/tax_2122_.xlsx";
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Import errors (`org.apache cannot be resolved`)

**Solution**: Ensure all JAR files are properly added to classpath
- VS Code: Use `Java: Configure Classpath` command
- Restart Java Language Server: `Java: Clean Java Language Server Workspace`

#### 2. File not found errors

**Solution**: Verify that configuration files exist:
- `configuration/configuration_stock.xlsx`
- `configuration/tax_2122_.xlsx`

#### 3. ClassNotFoundException

**Solution**: Compile all source files first
```bash
javac -d bin -cp "lib/*" src/**/*.java
```

#### 4. Excel file format errors

**Solution**: 
- Ensure Excel files are in `.xlsx` format (not `.xls`)
- Verify file is not corrupted
- Check file is not open in Excel while running

## 📝 Development

### Adding New Features

1. Create new classes in appropriate package under `src/`
2. Update imports in relevant files
3. Recompile the project
4. Test thoroughly with sample data

### Logging

The application uses Log4j2 for logging. Configure logging by adding `log4j2.xml` in the classpath:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

## 📄 License

[BLANK]

## 👤 Author

**Ajay Gupta**

## 🤝 Contributing

[Add contribution guidelines if applicable]

## 📮 Support

For issues or questions, please [create an issue/contact information]

---

**Last Updated**: December 2025
