# InvestingHurdle Phase 3 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                              │
├─────────────────────────────────────────────────────────────────┤
│  Web Browser  │  Mobile App  │  Desktop GUI  │  API Clients    │
│  (React/Ang)  │  (Flutter)   │  (JavaFX)     │  (curl/Postman) │
└────────┬──────┴──────┬───────┴───────┬───────┴─────────┬────────┘
         │             │               │                 │
         └─────────────┴───────────────┴─────────────────┘
                           HTTP/REST (JSON)
                                  │
┌─────────────────────────────────▼───────────────────────────────┐
│                    SPRING BOOT REST API                          │
│                   (Port 8080, Context: /api/v1)                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐    │
│  │              CONTROLLER LAYER                           │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │  CalculationController                                  │    │
│  │  ├─ POST /calculations/upload                          │    │
│  │  ├─ GET  /calculations/default                         │    │
│  │  ├─ GET  /calculations/financial-years                 │    │
│  │  └─ GET  /calculations/health                          │    │
│  └────────────────────┬───────────────────────────────────┘    │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐    │
│  │              SERVICE LAYER                              │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │  TaxCalculationService                                  │    │
│  │  ├─ File Upload Handling                               │    │
│  │  ├─ Validation Logic                                   │    │
│  │  ├─ Calculation Orchestration                          │    │
│  │  └─ Response Transformation                            │    │
│  └────────────────────┬───────────────────────────────────┘    │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐    │
│  │              DTO LAYER                                  │    │
│  ├────────────────────────────────────────────────────────┤    │
│  │  TaxCalculationResponse                                 │    │
│  │  ├─ StcgResponse                                       │    │
│  │  ├─ SpeculationResponse                                │    │
│  │  └─ QuarterDetailResponse[]                            │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                   │
│  ┌──────────────────────────────────────────────────────┐      │
│  │         INFRASTRUCTURE                                │      │
│  ├──────────────────────────────────────────────────────┤      │
│  │  • Swagger/OpenAPI Documentation                      │      │
│  │  • CORS Configuration                                 │      │
│  │  • Error Handling                                     │      │
│  │  • Logging (Log4j2)                                   │      │
│  │  • Actuator (Health checks)                           │      │
│  └──────────────────────────────────────────────────────┘      │
│                                                                   │
└───────────────────────────────┬─────────────────────────────────┘
                                │ Java Integration
┌───────────────────────────────▼─────────────────────────────────┐
│              EXISTING CALCULATION ENGINE                          │
│                    (../src and ../bin)                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  InvestingHurdleBootstrapper                            │    │
│  │  ├─ Configuration Management (ConfigFileManager)       │    │
│  │  ├─ Quarter Configuration (QuarterConfig)              │    │
│  │  └─ Initialization & Orchestration                     │    │
│  └────────────────────┬───────────────────────────────────┘    │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐    │
│  │  Data Processing Layer                                  │    │
│  │  ├─ EquityLoader (Excel processing)                    │    │
│  │  ├─ WorkbookLoader (alternate loader)                  │    │
│  │  └─ Security (transaction model)                       │    │
│  └────────────────────┬───────────────────────────────────┘    │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐    │
│  │  Calculation Utilities                                  │    │
│  │  ├─ FIFOCalculator (cost basis)                        │    │
│  │  ├─ DataValidator (input validation)                   │    │
│  │  ├─ QuarterlyBreakdown (quarter management)            │    │
│  │  └─ HurdleConstant (configuration constants)           │    │
│  └────────────────────┬───────────────────────────────────┘    │
│                       │                                          │
│  ┌────────────────────▼───────────────────────────────────┐    │
│  │  External Libraries                                     │    │
│  │  ├─ Apache POI 5.2.2 (Excel processing)               │    │
│  │  ├─ Log4j 2.17.2 (logging)                             │    │
│  │  └─ Apache Commons (utilities)                         │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                   │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                        DATA LAYER                                │
├─────────────────────────────────────────────────────────────────┤
│  Excel Files        │  Configuration   │  Temporary Files        │
│  (tax_2122_.xlsx)   │  (properties)    │  (uploads/)            │
│  ./configuration/   │  ./configuration/│  ./uploads/            │
└─────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════
                         DATA FLOW EXAMPLE
═══════════════════════════════════════════════════════════════════

1. User uploads Excel file via POST /calculations/upload
   ↓
2. CalculationController receives multipart file
   ↓
3. TaxCalculationService validates and saves file temporarily
   ↓
4. Service invokes EquityLoader with file path
   ↓
5. EquityLoader reads Excel using Apache POI
   ↓
6. QuarterConfig determines quarters from financial year
   ↓
7. Calculations performed (STCG, Speculation, Quarterly breakdown)
   ↓
8. Results mapped to TaxCalculationResponse DTO
   ↓
9. JSON response returned to client
   ↓
10. Temporary file cleaned up

═══════════════════════════════════════════════════════════════════
                         TECHNOLOGY STACK
═══════════════════════════════════════════════════════════════════

Backend Framework:    Spring Boot 3.2.1
Programming Language: Java 21
Build Tool:           Maven 3.8+
API Documentation:    SpringDoc OpenAPI 3.0 (Swagger)
Excel Processing:     Apache POI 5.2.2
Logging:              Log4j 2.17.2
Validation:           Spring Validation
Monitoring:           Spring Actuator
Code Generation:      Lombok

═══════════════════════════════════════════════════════════════════
                    DEPLOYMENT OPTIONS
═══════════════════════════════════════════════════════════════════

┌──────────────────┐     ┌──────────────────┐     ┌──────────────┐
│  Standalone JAR  │     │   Docker         │     │  Cloud       │
│                  │     │                  │     │              │
│  java -jar       │     │  docker run      │     │  AWS/GCP     │
│  api.jar         │     │  investing-api   │     │  Kubernetes  │
└──────────────────┘     └──────────────────┘     └──────────────┘

═══════════════════════════════════════════════════════════════════
                    FUTURE ENHANCEMENTS
═══════════════════════════════════════════════════════════════════

Phase 3.2 - Desktop GUI (JavaFX)
├─ Native file browser
├─ Real-time progress display
├─ Interactive charts/graphs
└─ Export wizard

Phase 3.3 - Web Portal (React/Angular)
├─ Modern web UI
├─ User authentication
├─ Multi-tenant support
└─ Cloud deployment

Phase 4 - Database Integration
├─ PostgreSQL/MySQL backend
├─ Historical data storage
├─ Query and analytics
└─ Audit trails

Phase 5 - Advanced Features
├─ Real-time market data
├─ Tax optimization ML
├─ Report generation (PDF/Excel)
└─ Tax form pre-filling

═══════════════════════════════════════════════════════════════════
```
