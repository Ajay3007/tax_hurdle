# Step-by-Step Guide: Running InvestingHurdle in IntelliJ IDEA

## Prerequisites
- IntelliJ IDEA (Community or Ultimate)
- Java 17 JDK installed
- Maven (bundled with IntelliJ)

---

## Quick Fixes (Common Errors)

- 404 on health/calculate: Add `/api/v1` to the API base URL (e.g., `http://localhost:8080/api/v1`).
- Connection refused: Start the backend (`mvn spring-boot:run -f spring-api/pom.xml`) or fix the port in Settings.
- Port 8080 in use: Set `server.port=8081` in `spring-api/src/main/resources/application.properties` and update the API URL.
- Maven sync issues: Reload Maven projects (elephant icon) or Build → Rebuild Project.
- JavaFX run errors: Use `javafx:run` via Maven instead of direct Application config.

---

## Step 1: Open the Project

1. Launch **IntelliJ IDEA**
2. Click **File** → **Open**
3. Navigate to and select the root folder: `C:\Users\Ajay.Gupt\OneDrive - Reliance Corporate IT Park Limited\Documents\csp\tax_hurdle`
4. Click **OK**
5. Wait for IntelliJ to import the Maven project (you'll see a progress indicator in the bottom-right)

---

## Step 2: Configure Java SDK

1. Go to **File** → **Project Structure** (or press `Ctrl+Alt+Shift+S`)
2. Under **Project**:
   - Set **SDK** to **17** (if not listed, click **Add SDK** → **Download JDK** → select version 17)
   - Set **Language level** to **17**
3. Under **Modules**:
   - Verify both `spring-api` and `javafx-ui` modules are recognized
4. Click **OK**

---

## Step 3: Run the Spring Boot Backend

### Option A: Using the Run Icon (Easiest)
1. In Project view, navigate to: `spring-api/src/main/java/com/investinghurdle/InvestingHurdleApplication.java`
2. Look for the **green play icon** (▶) in the gutter next to the class declaration or `main` method
3. Click the icon → select **Run 'InvestingHurdleApplication'**
4. Wait for the application to start (you'll see logs in the Run panel)
5. Look for the message: `Started InvestingHurdleApplication on port 8080`

### Option B: Using Run Configuration
1. Click **Run** → **Edit Configurations**
2. Click **+** → **Spring Boot**
3. Configure:
   - **Name**: `Spring API`
   - **Main class**: `com.investinghurdle.InvestingHurdleApplication`
   - **Use classpath of module**: `spring-api`
   - **Working directory**: `$MODULE_WORKING_DIR$` (or project root)
4. Click **OK**
5. Click the **Run** button (green play icon) in the toolbar

### Verify Backend is Running
- Open browser: `http://localhost:8080/api/v1/calculations/health`
- You should see: `{"status":"UP","timestamp":"..."}` (404 means the context path `/api/v1` is missing)

---

## Step 4: Run the JavaFX Desktop UI

### Option A: Using Maven (Recommended)
1. Open the **Maven** tool window (View → Tool Windows → Maven)
2. Expand: `tax_hurdle` → `javafx-ui` → `Plugins` → `javafx`
3. **Double-click** `javafx:run`
4. The desktop application window will launch

### Option B: Using Application Run Configuration
1. Click **Run** → **Edit Configurations**
2. Click **+** → **Application**
3. Configure:
   - **Name**: `JavaFX UI`
   - **Main class**: `com.investinghurdle.ui.InvestingHurdleUI`
   - **Use classpath of module**: `javafx-ui`
   - **Working directory**: Project root path
   - **VM options** (only if needed): 
     ```
     --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics
     ```
     *(Usually not needed when running via Maven)*
4. Click **OK**
5. Click **Run**

---

## Step 5: Configure the Desktop App

1. In the running JavaFX app, click **Settings** button
2. Configure:
   - **API Base URL**: `http://localhost:8080/api/v1` (must include `/api/v1`)
   - **Financial Year**: e.g., `2021-22`
   - **Quarter Scheme**: Select from dropdown (e.g., `STANDARD_Q4`)
   - **API Key** (optional): Leave blank unless you've configured authentication
3. Click **Test Connection** to verify backend connectivity
4. Should show success message

---

## Step 6: Use the Application

1. Click **Upload** tab
2. **Drag & drop** or **Browse** to select your Excel file (e.g., from `configuration/` folder)
3. Broker detection runs automatically after you select a file; wait for the status to show the broker name
4. Click **Calculate** to process tax calculations
5. View results in the **Results** panel (summary table, pie chart, quarter charts)
6. Click **Export as Excel** to save the report

---

## Troubleshooting

### Backend Won't Start
- **Port 8080 already in use**: Stop other services or change port in `spring-api/src/main/resources/application.properties`:
  ```properties
  server.port=8081
  ```
  Then update API URL in desktop app settings to `http://localhost:8081`

### JavaFX Module Errors
- Use Maven `javafx:run` instead of direct Application run
- Ensure JavaFX dependencies are downloaded (check Maven → Reload All Maven Projects)

### Maven Not Recognized
- Go to **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Maven**
- Ensure Maven home directory is set (use bundled or specify custom installation)

### Connection Refused in Desktop App
- Ensure backend is running (check Run panel for Spring Boot logs)
- Verify API URL in Settings matches backend port and includes `/api/v1`

### 404 on Health/Calculate/Detect
- Backend context path is `/api/v1`; health URL is `http://localhost:8080/api/v1/calculations/health`
- Calculation endpoint is `/calculations/upload`; 404 means the API base URL is missing `/api/v1` or the backend is stopped

### Compilation Errors
- Click **Build** → **Rebuild Project** to rebuild everything
- If errors persist, go to **File** → **Invalidate Caches** → **Invalidate and Restart**

---

## Running from Terminal (Alternative)

### Backend:
```powershell
cd spring-api
mvn spring-boot:run
```

### Desktop:
```powershell
cd javafx-ui
mvn javafx:run
```

---

## Project Structure

```
tax_hurdle/
├── pom.xml                          (Parent POM)
├── spring-api/                      (Spring Boot API)
│   ├── pom.xml
│   └── src/main/java/com/investinghurdle/
│       ├── InvestingHurdleApplication.java
│       ├── api/
│       │   ├── controller/
│       │   ├── dto/
│       │   └── service/
│       └── config/
├── javafx-ui/                       (JavaFX Desktop)
│   ├── pom.xml
│   └── src/main/java/com/investinghurdle/ui/
│       ├── InvestingHurdleUI.java
│       ├── ApiClient.java
│       ├── SettingsManager.java
│       ├── UploadScene.java
│       ├── ResultsScene.java
│       └── SettingsScene.java
└── configuration/                   (Excel files for testing)
    └── tax_2122_.xlsx
```

---

## Common Workflow

1. **Terminal 1**: Start the backend API
   ```
   mvn spring-boot:run -f spring-api/pom.xml
   ```

2. **Terminal 2** (or IntelliJ Maven panel): Start the JavaFX UI
   ```
   mvn javafx:run -f javafx-ui/pom.xml
   ```

3. **Desktop App**: Upload Excel → Detect Broker → Calculate → View Results → Export

---

## Building for Distribution

### Package Spring API:
```powershell
cd spring-api
mvn clean package
# Creates: target/investing-hurdle-api-1.0.0.jar
```

### Package JavaFX UI:
```powershell
cd javafx-ui
mvn clean package
# Creates: target/investing-hurdle-ui-1.0.0-shaded.jar
```

---

## Success Checklist

- [ ] IntelliJ IDEA opened root `tax_hurdle` folder
- [ ] Maven modules recognized (spring-api and javafx-ui visible)
- [ ] Java 17 SDK configured
- [ ] Backend API compiles and runs without errors
- [ ] Can access `http://localhost:8080/api/v1/calculations/health`
- [ ] Desktop UI launches and connects to API
- [ ] Can upload Excel file, detect broker, and calculate
- [ ] Results display correctly with charts
- [ ] Can export results to Excel

Once all checked, you're ready to proceed! 🎉

---

You're now ready to run both the backend API and desktop application in IntelliJ! Start the backend first, then launch the desktop UI.
