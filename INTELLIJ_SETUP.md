# Running InvestingHurdle REST API in IntelliJ IDEA

## Step-by-Step Guide

### 1. Open Project in IntelliJ IDEA

1. **Launch IntelliJ IDEA**

2. **Open the Project:**
   - Click "Open" (or File → Open)
   - Navigate to: `C:\Users\Ajay.Gupt\OneDrive - Reliance Corporate IT Park Limited\Documents\csp\tax_hurdle\spring-api`
   - Click OK

3. **Import as Maven Project:**
   - IntelliJ will detect `pom.xml` automatically
   - You'll see a popup: "Maven projects need to be imported"
   - Click **"Import Changes"** or **"Enable Auto-Import"** (recommended)
   
4. **Wait for Dependencies:**
   - Bottom right corner shows: "Importing Maven projects..."
   - This downloads Spring Boot and all dependencies (1-2 minutes)
   - Progress bar will show completion

### 2. Configure JDK (if needed)

If IntelliJ asks about JDK:

1. **File → Project Structure** (Ctrl+Alt+Shift+S)
2. **Project Settings → Project**
3. **SDK:** Select Java 21 (or click "Add SDK" → "Download JDK" → Choose version 21)
4. **Language Level:** 21
5. Click **Apply** → **OK**

### 3. Run the Application

**Method A - Using Run Button (Easiest):**

1. In the **Project** panel (left side), navigate to:
   ```
   src/main/java/com/investinghurdle/api/InvestingHurdleApiApplication.java
   ```

2. **Right-click** on `InvestingHurdleApiApplication.java`

3. Select **"Run 'InvestingHurdleApiA...'"**

4. Watch the **Run** panel at the bottom:
   ```
   ======================================================================
     InvestingHurdle REST API
     Starting server...
   ======================================================================
   
   ...Spring Boot logs...
   
   Started InvestingHurdleApiApplication in 3.5 seconds (process running for 4.0)
   ```

5. ✅ **Success!** Server is running on **http://localhost:8080**

**Method B - Using Maven Panel:**

1. Open **Maven** panel (right side)
2. Expand: **investing-hurdle-api → Plugins → spring-boot**
3. Double-click: **spring-boot:run**

### 4. Verify API is Running

**Open your browser and visit:**

1. **Swagger UI:**
   ```
   http://localhost:8080/api/v1/swagger-ui.html
   ```
   You should see interactive API documentation!

2. **Health Check:**
   ```
   http://localhost:8080/api/v1/calculations/health
   ```
   Should return:
   ```json
   {
     "status": "UP",
     "service": "Tax Calculation Service",
     "version": "1.0.0"
   }
   ```

### 5. Test the API

**Using Swagger UI (Recommended):**

1. Open: http://localhost:8080/api/v1/swagger-ui.html

2. Expand **"GET /calculations/default"**

3. Click **"Try it out"**

4. Click **"Execute"**

5. See the response with tax calculations!

**Using PowerShell:**
```powershell
# Health check
curl http://localhost:8080/api/v1/calculations/health

# Default calculation
curl http://localhost:8080/api/v1/calculations/default

# Get financial years
curl http://localhost:8080/api/v1/calculations/financial-years
```

## Troubleshooting

### Issue: "Cannot resolve symbol 'SpringBootApplication'"
**Solution:** Maven dependencies not downloaded yet
- Check bottom right: "Importing Maven projects..."
- Wait for it to complete
- Or: Right-click `pom.xml` → Maven → Reload Project

### Issue: "Port 8080 already in use"
**Solution:** 
1. Stop the application (red square button in Run panel)
2. Or change port: Edit `src/main/resources/application.yml`
   ```yaml
   server:
     port: 8081
   ```

### Issue: "Java version mismatch"
**Solution:**
- File → Project Structure → Project → SDK → Select Java 21
- File → Settings → Build → Build Tools → Maven → Maven home directory

### Issue: Build errors
**Solution:**
1. File → Invalidate Caches → Invalidate and Restart
2. Maven panel → Click refresh button (⟳)
3. Or: mvn clean install from Maven panel

## What's Next?

Once the API is running successfully:

### ✅ Immediate Next Steps:
1. Test all endpoints in Swagger UI
2. Verify calculations work correctly
3. Upload a test Excel file

### 🚀 Phase 3.2 - Build Desktop GUI (JavaFX)
Create a desktop application that uses this API

### 🌐 Phase 3.3 - Build Web Frontend
Create React/Angular frontend that calls this API

### 🔗 Integrate Full Calculation Engine
Connect the API to the actual EquityLoader (currently uses mock data)

## Tips for Development

**Enable Auto-Reload:**
1. File → Settings → Build → Compiler → Check "Build project automatically"
2. This recompiles on save for faster development

**View Logs:**
- Run panel shows all logs
- Log file: `./logs/api.log`

**Debug Mode:**
- Right-click `InvestingHurdleApiApplication.java`
- Select "Debug 'InvestingHurdleApiA...'"
- Set breakpoints by clicking line numbers

**Hot Swap:**
- Make code changes
- Build → Recompile (Ctrl+Shift+F9)
- Changes apply without restarting (most of the time)

## Success Checklist

- [ ] IntelliJ IDEA opened spring-api folder
- [ ] Maven dependencies imported (no red underlines)
- [ ] Application runs without errors
- [ ] Can access http://localhost:8080/api/v1/swagger-ui.html
- [ ] Health check returns "UP"
- [ ] Can execute default calculation endpoint
- [ ] See JSON response with STCG and speculation data

Once all checked, you're ready to proceed! 🎉
