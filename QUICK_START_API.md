# Quick Start Guide - InvestingHurdle REST API

## Prerequisites Check

Before starting, ensure you have:
- [ ] Java 21 installed (`java -version`)
- [ ] Maven 3.8+ installed (`mvn -version`)
- [ ] Existing calculation engine compiled in `../bin`

## Step-by-Step Setup

### Step 1: Navigate to API Directory
```powershell
cd spring-api
```

### Step 2: Build the Project
```powershell
mvn clean install
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 30 s
```

### Step 3: Run the Application
```powershell
mvn spring-boot:run
```

**Expected output:**
```
======================================================================
  InvestingHurdle REST API
  Starting server...
======================================================================

...Spring Boot initialization logs...

Started InvestingHurdleApiApplication in 3.5 seconds
```

### Step 4: Verify Server is Running

Open your browser and visit:
```
http://localhost:8080/api/v1/swagger-ui.html
```

You should see the Swagger UI with API documentation.

## Test the API

### Test 1: Health Check
```powershell
curl http://localhost:8080/api/v1/calculations/health
```

**Expected response:**
```json
{
  "status": "UP",
  "service": "Tax Calculation Service",
  "version": "1.0.0"
}
```

### Test 2: Get Financial Years
```powershell
curl http://localhost:8080/api/v1/calculations/financial-years
```

**Expected response:**
```json
{
  "supported_years": ["FY 2021-22", "FY 2022-23"],
  "default_year": "FY 2021-22"
}
```

### Test 3: Default Calculation
```powershell
curl http://localhost:8080/api/v1/calculations/default
```

**Expected response:**
```json
{
  "financial_year": "FY 2021-22",
  "stcg": {
    "full_value_of_consideration": 447343.86,
    "cost_of_acquisition": 446831.45,
    "total_stcg": 512.41,
    ...
  },
  ...
}
```

### Test 4: Upload File
```powershell
curl -X POST http://localhost:8080/api/v1/calculations/upload `
  -F "file=@../configuration/tax_2122_.xlsx" `
  -F "financial_year=FY 2021-22"
```

## Using Swagger UI (Recommended)

1. **Open Swagger UI:**  
   http://localhost:8080/api/v1/swagger-ui.html

2. **Expand an endpoint** (e.g., GET /calculations/default)

3. **Click "Try it out"**

4. **Click "Execute"**

5. **View the response** in the "Response body" section

## Troubleshooting

### Issue: Maven not found
**Solution:** Install Maven or use the Maven wrapper:
```powershell
./mvnw clean install
```

### Issue: Port 8080 already in use
**Solution:** Change port in `src/main/resources/application.yml`:
```yaml
server:
  port: 8081  # Change to any available port
```

### Issue: Java version mismatch
**Solution:** Ensure Java 21 is installed and set in PATH:
```powershell
java -version  # Should show 21.x.x
```

### Issue: Build fails with dependencies
**Solution:** Clean Maven cache and rebuild:
```powershell
mvn clean install -U
```

## Next Steps

1. ✅ API is running successfully
2. ✅ Test all endpoints via Swagger UI
3. 🔜 Integrate full calculation engine (see PHASE3_IMPLEMENTATION.md)
4. 🔜 Build frontend application (React/Angular)
5. 🔜 Deploy to production server

## Development Mode

For development with auto-reload:
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Production Build

To create a standalone JAR:
```powershell
mvn clean package
java -jar target/investing-hurdle-api-1.0.0.jar
```

## Configuration

Edit `src/main/resources/application.yml` to customize:
- Server port
- File upload directory
- Log levels
- Default financial year
- CORS origins

## API Endpoints Summary

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/calculations/upload` | POST | Upload workbook and calculate |
| `/calculations/default` | GET | Calculate using default file |
| `/calculations/financial-years` | GET | Get supported years |
| `/calculations/health` | GET | Health check |

## Support

- **Documentation:** See [spring-api/README.md](spring-api/README.md)
- **API Docs:** http://localhost:8080/api/v1/swagger-ui.html
- **Logs:** Check `./logs/api.log`

## Success! 🎉

If you can access Swagger UI and see the API endpoints, you're all set!

The REST API is now ready for:
- Frontend development (React/Angular)
- Desktop GUI integration (JavaFX)
- Mobile app integration
- Third-party integrations
