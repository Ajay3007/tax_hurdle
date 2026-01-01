# InvestingHurdle JavaFX Desktop UI

A lightweight desktop GUI for the InvestingHurdle tax calculator. Provides file upload, broker detection, tax calculations, and report export via a user-friendly interface.

## Features

- **File Upload** with drag-and-drop support
- **Broker Detection** (Upstox, Zerodha, etc.) with auto-mapping
- **Tax Calculations** (STCG, LTCG, Speculation/Intraday)
- **Results Display** with summary tables and charts
- **Excel Export** for reports
- **Settings Panel** for API configuration, financial year, quarter scheme
- **Light/Dark Theme** toggle
- **Persistent Settings** (saved to local properties file)

## Prerequisites

- Java 17+
- Maven 3.8+
- InvestingHurdle Spring API running (e.g., `http://localhost:8080`)

## Build

```bash
cd javafx-ui
mvn clean package
```

## Run

### Option 1: Maven
```bash
mvn javafx:run
```

### Option 2: Java JAR
```bash
java -jar target/investing-hurdle-desktop.jar
```

## Configuration

Settings are stored locally in `investing-hurdle.properties` in the working directory.

- **API URL**: Default `http://localhost:8080` (configurable via Settings panel)
- **Financial Year**: Default `FY 2024-25`
- **Quarter Scheme**: Default `STANDARD_Q4`

## Architecture

- **InvestingHurdleUI**: Main application entry point
- **UploadScene**: File upload with broker detection
- **ResultsScene**: Display calculation results
- **SettingsScene**: Configure application preferences
- **ApiClient**: HTTP wrapper for REST API calls
- **SettingsManager**: Persistent local settings

## Workflow

1. **Start the Spring API**:
   ```bash
   cd ../spring-api
   mvn spring-boot:run
   ```

2. **Launch the Desktop App**:
   ```bash
   cd ../javafx-ui
   mvn javafx:run
   ```

3. **Upload Excel File**:
   - Drag-drop or browse to select your broker's Excel export
   - App auto-detects broker format

4. **Calculate**:
   - Click "Calculate Taxes"
   - Configure financial year / quarter scheme in Settings if needed

5. **View Results**:
   - Review STCG, LTCG, Speculation totals
   - Export to Excel

## Theme Support

Click the 🌙 **Dark** button in the top navigation to toggle light/dark theme.

## Troubleshooting

- **"Connection Failed"**: Ensure Spring API is running on the configured URL (Settings → Test Connection)
- **"Invalid File"**: Select an Excel (.xlsx) file exported from your broker
- **"Calculation Error"**: Check API logs for details; verify financial year/quarter scheme settings
