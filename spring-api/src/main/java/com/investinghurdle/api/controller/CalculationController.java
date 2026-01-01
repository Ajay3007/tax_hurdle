package com.investinghurdle.api.controller;

import com.investinghurdle.api.dto.TaxCalculationResponse;
import com.investinghurdle.api.service.TaxCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for tax calculation endpoints
 */
@RestController
@RequestMapping("/calculations")
@Tag(name = "Tax Calculations", description = "Endpoints for tax calculation operations")
public class CalculationController {
    
    @Autowired
    private TaxCalculationService calculationService;
    
    /**
     * Upload Excel workbook and calculate taxes
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload workbook and calculate taxes",
        description = "Upload an Excel workbook (.xlsx) containing transaction data and calculate STCG, speculation, and quarterly breakdown"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculation successful",
            content = @Content(schema = @Schema(implementation = TaxCalculationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file or request"),
        @ApiResponse(responseCode = "500", description = "Calculation error")
    })
    public ResponseEntity<?> calculateFromUpload(
            @Parameter(description = "Excel workbook file (.xlsx)")
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Financial year (e.g., 'FY 2021-22', 'FY 2024-25')", example = "FY 2024-25")
            @RequestParam(value = "financial_year", required = true) 
            String financialYear,
            
            @Parameter(description = "Quarter scheme: Q5_IT_PORTAL or STANDARD_Q4", example = "Q5_IT_PORTAL")
            @RequestParam(value = "quarter_scheme", required = false, defaultValue = "Q5_IT_PORTAL")
            String quarterScheme) {
        
        try {
            // Validate file type
            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Only .xlsx files are supported"));
            }
            
            TaxCalculationResponse response = calculationService.calculateFromFile(file, financialYear, quarterScheme);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Calculation failed: " + e.getMessage()));
        }
    }

    /**
     * Upload Excel workbook and download an Excel summary report.
     */
    @PostMapping(value = "/export", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload workbook and download Excel summary",
        description = "Upload an Excel workbook (.xlsx) and receive a compiled Excel summary with STCG, LTCG, and Speculation breakdowns"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Export successful"),
        @ApiResponse(responseCode = "400", description = "Invalid file or request"),
        @ApiResponse(responseCode = "500", description = "Export error")
    })
    public ResponseEntity<?> exportExcel(
            @Parameter(description = "Excel workbook file (.xlsx)")
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Financial year (e.g., 'FY 2021-22', 'FY 2024-25')", example = "FY 2024-25")
            @RequestParam(value = "financial_year", required = true)
            String financialYear,

            @Parameter(description = "Quarter scheme: Q5_IT_PORTAL or STANDARD_Q4", example = "Q5_IT_PORTAL")
            @RequestParam(value = "quarter_scheme", required = false, defaultValue = "Q5_IT_PORTAL")
            String quarterScheme) {

        try {
            if (!file.getOriginalFilename().endsWith(".xlsx")) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Only .xlsx files are supported"));
            }

            byte[] bytes = calculationService.exportExcel(file, financialYear, quarterScheme);
            String filename = "tax-summary-" + System.currentTimeMillis() + ".xlsx";
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentLength(bytes.length)
                .body(new ByteArrayResource(bytes));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Export failed: " + e.getMessage()));
        }
    }
    
    /**
     * Calculate using default configuration file
     */
    @GetMapping("/default")
    @Operation(
        summary = "Calculate using default configuration",
        description = "Perform tax calculation using the default configuration file (./configuration/tax_2122_.xlsx)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Calculation successful",
            content = @Content(schema = @Schema(implementation = TaxCalculationResponse.class))),
        @ApiResponse(responseCode = "500", description = "Calculation error")
    })
    public ResponseEntity<?> calculateDefault(
            @Parameter(description = "Financial year", example = "FY 2021-22")
            @RequestParam(value = "financial_year", required = false) String financialYear) {
        
        try {
            TaxCalculationResponse response = calculationService.calculateDefault(financialYear);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Calculation failed: " + e.getMessage()));
        }
    }
    
    /**
     * Get supported financial years
     */
    @GetMapping("/financial-years")
    @Operation(
        summary = "Get supported financial years",
        description = "Returns list of supported financial years for tax calculations"
    )
    public ResponseEntity<Map<String, Object>> getSupportedFinancialYears() {
        Map<String, Object> response = new HashMap<>();
        response.put("supported_years", new String[]{
            "FY 2021-22",
            "FY 2022-23",
            "FY 2023-24",
            "FY 2024-25"
        });
        response.put("default_year", "FY 2024-25");
        response.put("note", "System supports any financial year in format FY YYYY-YY");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Detect broker format from uploaded file
     */
    @PostMapping(value = "/detect-broker", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Detect broker format",
        description = "Upload an Excel file to detect the broker format and see how columns will be mapped"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Broker detected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file"),
        @ApiResponse(responseCode = "500", description = "Detection error")
    })
    public ResponseEntity<?> detectBroker(
            @Parameter(description = "Excel workbook file (.xlsx)")
            @RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = calculationService.detectBrokerFormat(file);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Broker detection failed: " + e.getMessage()));
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if calculation service is operational")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Tax Calculation Service");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create error response map
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
