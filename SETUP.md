# InvestingHurdle - Setup & Run Guide

A Java-based tax calculation tool for equity trading in India. This guide will help you set up the project and run it on your machine.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 15 or higher
  - Download from [Oracle Java](https://www.oracle.com/java/technologies/downloads/) or use [OpenJDK](https://openjdk.org/)
  - Verify installation: `java -version` and `javac -version`

- **Git**: To clone the repository (optional, you can download as ZIP)

## Project Setup

### 1. Clone or Download the Repository

#### Option A: Using Git (Recommended)
```bash
git clone <REPOSITORY_URL>
cd tax_hurdle
```

#### Option B: Download as ZIP
1. Download the repository as a ZIP file
2. Extract to your desired location
3. Open terminal/command prompt and navigate to the extracted folder

### 2. Verify Project Structure

After extraction, you should see the following structure:

```
tax_hurdle/
├── README.md
├── SETUP.md (this file)
├── bin/                          # Compiled classes (auto-generated)
├── configuration/                # Input Excel files
│   ├── tax_2122_.xlsx           # Tax calculation workbook
│   └── configuration_stock.xlsx  # Stock configuration (optional)
├── lib/                          # External libraries (JAR files)
│   ├── commons-collections4-4.4.jar
│   ├── commons-compress-1.26.0.jar
│   ├── commons-io-2.15.1.jar
│   ├── dom4j.jar
│   ├── log4j-api-2.22.0.jar
│   ├── log4j-core-2.22.0.jar
│   ├── poi-5.2.3.jar
│   ├── poi-ooxml-5.2.3.jar
│   ├── poi-ooxml-lite-5.2.3.jar
│   └── xmlbeans-5.2.0.jar
└── src/                          # Java source code
    ├── bootstrap/
    │   └── InvestingHurdleBootstrapper.java
    ├── exception/
    │   └── InvalidSecurityException.java
    ├── logging/
    │   └── HurdleLogger.java
    ├── params/
    │   ├── EquityLoader.java
    │   └── WorkbookLoader.java
    ├── security/
    │   └── Security.java
    └── util/
        └── HurdleConstant.java
```

**Important**: All required JAR files must be present in the `lib/` directory for compilation and execution.

### 3. Verify Configuration Files

Ensure the required Excel files exist in the `configuration/` directory:

- `tax_2122_.xlsx` - Main tax calculation workbook (required)
- `configuration_stock.xlsx` - Stock configuration (optional)

## Build & Compile

### On Windows (Command Prompt or PowerShell)

```bash
cd <PROJECT_ROOT>
javac -cp "lib/*" -d bin src/bootstrap/*.java src/exception/*.java src/logging/*.java src/params/*.java src/security/*.java src/util/*.java
```

### On macOS/Linux

```bash
cd <PROJECT_ROOT>
javac -cp "lib/*" -d bin src/bootstrap/*.java src/exception/*.java src/logging/*.java src/params/*.java src/security/*.java src/util/*.java
```

**Where:**
- `<PROJECT_ROOT>` = Your project directory path (e.g., `C:\Users\YourName\Documents\tax_hurdle` on Windows)
- `-cp "lib/*"` = Includes all JAR files from the lib directory in the classpath
- `-d bin` = Outputs compiled .class files to the bin directory

**Note**: You may see warnings about annotation processing and deprecated APIs - these are normal and can be safely ignored.

## Run the Application

### On Windows (Command Prompt or PowerShell)

```bash
cd <PROJECT_ROOT>
java -cp "bin;lib/*" bootstrap.InvestingHurdleBootstrapper
```

### On macOS/Linux

```bash
cd <PROJECT_ROOT>
java -cp "bin:lib/*" bootstrap.InvestingHurdleBootstrapper
```

**Note**: Windows uses `;` as classpath separator, while macOS/Linux use `:`

## Expected Output

When the application runs successfully, you should see output similar to:

```
***************** WELCOME TO THE INVESTING WORLD... ********************
Initializing Equity Loader...

Equity loader initialized SUCCESSFULLY :)

$$$$$$$$$********  STCG  ********$$$$$$$$$

Full Value of consideration : <amount>
Cost of acquisition : <amount>
STCG = <profit/loss>
STCG total : <amount>
STCG Q1 = <amount>
STCG Q2 = <amount>
STCG Q3 = <amount>
STCG Q4 = <amount>
STCG Q5 = <amount>

$$$$$$$$$********  SPECULATION  ********$$$$$$$$$

Full Value of consideration : <amount>
Cost of acquisition : <amount>
PL = <profit/loss>
Turnover total intraday : <amount>

*-*-*-*-*-*-*-*-*-*-*-*-*-*-* END *-*-*-*-*-*-*-*-*-*-*-*-*-*-*
```

## Troubleshooting

### Issue: "javac: command not found" or "'javac' is not recognized"

**Solution**: Java is not installed or not in your system PATH
- Install JDK from [Oracle Java](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
- Verify installation: `javac -version`

### Issue: "Error: Could not find or load main class bootstrap.InvestingHurdleBootstrapper"

**Solution**: Compilation failed or classpath is incorrect
- Ensure you've successfully compiled the code (check that `bin/` folder has .class files)
- Verify you're in the correct directory (`<PROJECT_ROOT>`)
- Check that the command uses correct classpath separator (`;` for Windows, `:` for macOS/Linux)

### Issue: "InvalidSecurityException" or Excel parsing errors

**Solution**: Configuration file issues
- Verify `tax_2122_.xlsx` exists in the `configuration/` directory
- Ensure the Excel file hasn't been moved or renamed
- Check that the Excel file structure matches expected format (see Project Overview section in README.md)

### Issue: "JAR files not found" or "ClassNotFoundException"

**Solution**: Missing libraries in lib directory
- Ensure all JAR files listed in the Project Structure are present in `lib/`
- Verify the classpath in compile/run commands includes `lib/*`

### Issue: Compilation warnings about deprecated API

**Solution**: These are normal and don't affect execution
- Warnings can be suppressed by adding `-Xlint:deprecation` flag during compilation
- The application will still run correctly

## Quick Start (Summary)

```bash
# 1. Navigate to project directory
cd <PROJECT_ROOT>

# 2. Compile the project
javac -cp "lib/*" -d bin src/bootstrap/*.java src/exception/*.java src/logging/*.java src/params/*.java src/security/*.java src/util/*.java

# 3. Run the application
# On Windows:
java -cp "bin;lib/*" bootstrap.InvestingHurdleBootstrapper

# On macOS/Linux:
java -cp "bin:lib/*" bootstrap.InvestingHurdleBootstrapper
```

## Additional Resources

- **README.md**: Project overview and architecture details
- **InvestingHurdleBootstrapper.java**: Entry point of the application
- **EquityLoader.java**: Core tax calculation logic
- **HurdleConstant.java**: Configuration constants and file paths

## Support

If you encounter issues:
1. Check the Troubleshooting section above
2. Verify all prerequisites are installed
3. Ensure the project structure matches the expected layout
4. Confirm configuration files are in place and accessible

---

**Last Updated**: December 31, 2025
