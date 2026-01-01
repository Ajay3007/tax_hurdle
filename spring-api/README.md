# InvestingHurdle REST API

## Overview
REST API for the InvestingHurdle Tax Calculator. Provides endpoints for calculating Indian equity taxes (STCG, LTCG, Speculation/Intraday) from Excel workbooks.

## Quick Start

### Prerequisites
- Java 21
- Maven 3.8+
- Existing InvestingHurdle calculation engine (compiled in ../bin)

### Build and Run

```bash
cd spring-api
mvn clean install
mvn spring-boot:run
```

The API will start on `http://localhost:8080/api/v1`

### Access Swagger UI
Once running, access the interactive API documentation at:
```
http://localhost:8080/api/v1/swagger-ui.html
```

## API Endpoints

### 1. Calculate from Uploaded File
**POST** `/calculations/upload`

Upload an Excel workbook and calculate taxes.

**Parameters:**
- `file` (multipart file) - Excel workbook (.xlsx)
- `financial_year` (query param, optional) - Financial year (default: "FY 2021-22")

**Example (curl):**
```bash
curl -X POST "http://localhost:8080/api/v1/calculations/upload?financial_year=FY%202021-22" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@./configuration/tax_2122_.xlsx"
```

**Response:**
```json
{
  "financial_year": "FY 2021-22",
  "stcg": {
    "full_value_of_consideration": 447343.86,
    "cost_of_acquisition": 446831.45,
    "total_stcg": 512.41,
    "stcg_q1": 0.0,
    "stcg_q2": 0.0,
    "stcg_q3": 1644.20,
    "stcg_q4": -1131.79,
    "stcg_q5": 0.0
  },
  "speculation": {
    "full_value_of_consideration": 12447914.65,
    "cost_of_acquisition": 12464533.25,
    "profit_loss": -16618.60,
    "total_turnover": 39280.20
  },
  "quarterly_breakdown": [
    {
      "quarter_number": 1,
      "quarter_code": "Q1",
      "quarter_name": "Apr-Jun",
      "start_date": "2021-04-01",
      "end_date": "2021-06-15",
      "stcg_amount": 0.0
    },
    ...
  ],
  "calculated_at": "2025-12-31T14:30:00",
  "processing_time_ms": 250
}
```

### 2. Calculate Using Default File
**GET** `/calculations/default`

Calculate using the default configuration file (./configuration/tax_2122_.xlsx).

**Parameters:**
- `financial_year` (query param, optional) - Financial year

**Example:**
```bash
curl "http://localhost:8080/api/v1/calculations/default?financial_year=FY%202022-23"
```

### 3. Get Supported Financial Years
**GET** `/calculations/financial-years`

Returns list of supported financial years.

**Example:**
```bash
curl "http://localhost:8080/api/v1/calculations/financial-years"
```

**Response:**
```json
{
  "supported_years": ["FY 2021-22", "FY 2022-23"],
  "default_year": "FY 2021-22"
}
```

### 4. Health Check
**GET** `/calculations/health`

Check if the calculation service is operational.

**Example:**
```bash
curl "http://localhost:8080/api/v1/calculations/health"
```

## Response Models

### TaxCalculationResponse
Complete tax calculation result.

| Field | Type | Description |
|-------|------|-------------|
| financial_year | string | Financial year |
| stcg | StcgResponse | STCG calculation details |
| speculation | SpeculationResponse | Speculation/intraday details |
| quarterly_breakdown | QuarterDetailResponse[] | Quarter-wise breakdown |
| calculated_at | string | ISO timestamp |
| processing_time_ms | number | Processing time |

### StcgResponse
Short-Term Capital Gains calculation.

| Field | Type | Description |
|-------|------|-------------|
| full_value_of_consideration | number | Total sell amount |
| cost_of_acquisition | number | Total buy amount |
| total_stcg | number | Total STCG (profit/loss) |
| stcg_q1 | number | Q1 STCG |
| stcg_q2 | number | Q2 STCG |
| stcg_q3 | number | Q3 STCG |
| stcg_q4 | number | Q4 STCG |
| stcg_q5 | number | Q5 STCG |

### SpeculationResponse
Speculation/Intraday trading calculation.

| Field | Type | Description |
|-------|------|-------------|
| full_value_of_consideration | number | Total sell amount |
| cost_of_acquisition | number | Total buy amount |
| profit_loss | number | Profit or loss |
| total_turnover | number | Total intraday turnover |

### QuarterDetailResponse
Quarterly breakdown with date ranges.

| Field | Type | Description |
|-------|------|-------------|
| quarter_number | number | Quarter number (1-5) |
| quarter_code | string | Quarter code (Q1-Q5) |
| quarter_name | string | Quarter period name |
| start_date | string | Start date (YYYY-MM-DD) |
| end_date | string | End date (YYYY-MM-DD) |
| stcg_amount | number | STCG for this quarter |

## Configuration

Edit `src/main/resources/application.yml`:

```yaml
investing-hurdle:
  upload-dir: ./uploads              # File upload directory
  output-dir: ./output               # Output directory
  default-financial-year: FY 2021-22 # Default FY
  max-processing-threads: 4          # Max parallel processing threads

server:
  port: 8080                         # Server port

logging:
  level:
    com.investinghurdle: DEBUG       # Log level
```

## CORS Configuration

The API is configured to allow CORS from:
- `http://localhost:3000` (React default)
- `http://localhost:4200` (Angular default)

To add more origins, edit `InvestingHurdleApiApplication.java`:

```java
.allowedOrigins("http://localhost:3000", "http://localhost:4200", "https://your-domain.com")
```

## Error Handling

All errors return a consistent format:

```json
{
  "error": true,
  "message": "Error description",
  "timestamp": 1704034200000
}
```

**HTTP Status Codes:**
- `200` - Success
- `400` - Bad request (invalid file, parameters)
- `500` - Internal server error (calculation failure)

## Integration with Existing Engine

The API wraps the existing InvestingHurdle calculation engine located in `../src` and `../bin`. The service layer (`TaxCalculationService`) handles:

1. File upload and validation
2. Temporary file storage
3. Invocation of calculation engine
4. Result transformation to API DTOs
5. Cleanup of temporary files

## Future Enhancements

### Phase 3.1 - In Progress
- [x] Basic REST API structure
- [x] File upload endpoint
- [x] STCG/Speculation calculation endpoints
- [x] Swagger/OpenAPI documentation
- [x] CORS configuration

### Phase 3.2 - Planned
- [ ] Full integration with existing EquityLoader
- [ ] Async processing with WebSockets
- [ ] Report generation endpoints (PDF/Excel)
- [ ] Batch processing support
- [ ] JWT authentication
- [ ] Rate limiting
- [ ] Caching layer

### Phase 3.3 - Future
- [ ] Database persistence
- [ ] Historical calculations
- [ ] Multi-user support
- [ ] Real-time progress updates
- [ ] Export to tax filing formats

## Testing

### Manual Testing with curl

```bash
# Health check
curl http://localhost:8080/api/v1/calculations/health

# Get supported years
curl http://localhost:8080/api/v1/calculations/financial-years

# Calculate with default file
curl http://localhost:8080/api/v1/calculations/default

# Upload and calculate
curl -X POST http://localhost:8080/api/v1/calculations/upload \
  -F "file=@./test-data.xlsx" \
  -F "financial_year=FY 2021-22"
```

### Using Swagger UI

1. Start the application
2. Open http://localhost:8080/api/v1/swagger-ui.html
3. Use the interactive interface to test endpoints

## Development

### Project Structure
```
spring-api/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/investinghurdle/api/
│       │       ├── InvestingHurdleApiApplication.java
│       │       ├── controller/
│       │       │   └── CalculationController.java
│       │       ├── service/
│       │       │   └── TaxCalculationService.java
│       │       ├── dto/
│       │       │   ├── TaxCalculationResponse.java
│       │       │   ├── StcgResponse.java
│       │       │   ├── SpeculationResponse.java
│       │       │   └── QuarterDetailResponse.java
│       │       └── config/
│       └── resources/
│           └── application.yml
├── pom.xml
└── README.md (this file)
```

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn package

# Run the JAR
java -jar target/investing-hurdle-api-1.0.0.jar
```

## Deployment

### Standalone JAR
```bash
mvn clean package
java -jar target/investing-hurdle-api-1.0.0.jar
```

### Docker (Future)
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/investing-hurdle-api-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Support

For issues or questions:
- Check Swagger documentation: http://localhost:8080/api/v1/swagger-ui.html
- Review logs in `./logs/api.log`
- See main project documentation in `../README.md`

## License

MIT License - See project root for details.
