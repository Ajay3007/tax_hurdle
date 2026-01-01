package com.investinghurdle.api.service;

import com.investinghurdle.api.dto.*;
import params.FlexibleEquityLoader;
import util.Quarter;
import util.QuarterConfig;
import util.QuarterScheme;
import util.ExcelHeaderDetector;
import util.ColumnMapping;
import util.ExcelSummaryExporter;
import logging.HurdleLogger;
import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for tax calculations
 * Integrates with the existing InvestingHurdle calculation engine
 */
@Service
public class TaxCalculationService {
    
    @Value("${investing-hurdle.upload-dir:./uploads}")
    private String uploadDir;
    
    @Value("${investing-hurdle.default-financial-year:FY 2021-22}")
    private String defaultFinancialYear;
    
    /**
     * Process uploaded Excel workbook and calculate taxes
     */
    public TaxCalculationResponse calculateFromFile(MultipartFile file, String financialYear, String quarterScheme) throws Exception {
        long startTime = System.currentTimeMillis();
        
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Save uploaded file temporarily
        String uploadedFilePath = saveUploadedFile(file);
        
        try {
            // Use existing EquityLoader logic (will integrate in next step)
            // For now, return mock data based on existing calculations
            TaxCalculationResponse response = performCalculation(uploadedFilePath, financialYear, quarterScheme);
            
            long processingTime = System.currentTimeMillis() - startTime;
            response.setProcessingTimeMs(processingTime);
            response.setCalculatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return response;
            
        } finally {
            // Clean up temporary file
            cleanupFile(uploadedFilePath);
        }
    }
    
    /**
     * Calculate using default configuration file
     */
    public TaxCalculationResponse calculateDefault(String financialYear) throws Exception {
        long startTime = System.currentTimeMillis();
        
        String fy = (financialYear != null) ? financialYear : defaultFinancialYear;
        
        // Use existing default configuration file
        String defaultFilePath = "./configuration/tax_2122_.xlsx";
        
        TaxCalculationResponse response = performCalculation(defaultFilePath, fy, "STANDARD_Q4");
        
        long processingTime = System.currentTimeMillis() - startTime;
        response.setProcessingTimeMs(processingTime);
        response.setCalculatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return response;
    }

    /**
     * Generate Excel summary report for an uploaded workbook.
     */
    public byte[] exportExcel(MultipartFile file, String financialYear, String quarterScheme) throws Exception {
        TaxCalculationResponse response = calculateFromFile(file, financialYear, quarterScheme);

        File temp = File.createTempFile("tax-summary-", ".xlsx");
        try {
            new ExcelSummaryExporter().export(temp, response);
            return Files.readAllBytes(temp.toPath());
        } finally {
            Files.deleteIfExists(temp.toPath());
        }
    }
    
    /**
     * Perform actual calculation using FlexibleEquityLoader (supports multiple brokers)
     */
    private TaxCalculationResponse performCalculation(String filePath, String financialYear, String quarterScheme) throws Exception {
        QuarterScheme scheme = QuarterScheme.STANDARD_Q4;
        try {
            scheme = QuarterScheme.valueOf(quarterScheme);
        } catch (Exception ignored) {}
        // Create QuarterConfig dynamically for any financial year
        QuarterConfig quarterConfig = util.QuarterConfig.createForFinancialYear(financialYear, scheme);
        
        // Create FlexibleEquityLoader with auto-detection
        FlexibleEquityLoader loader = new FlexibleEquityLoader(filePath, quarterConfig);
        
        // Initialize and load data
        loader.initialize();
        
        // Build STCG Response
        StcgResponse stcg = new StcgResponse(
            loader.getTotalStcgSell(),
            loader.getTotalStcgBuy(),
            loader.getTotalStcg()
        );
        
        // Build LTCG Response with exemption based on financial year
        double ltcgExemption = getLtcgExemptionLimit(financialYear);
        double taxableLtcg = Math.max(0, loader.getTotalLtcg() - ltcgExemption);
        LtcgResponse ltcg = new LtcgResponse(
            loader.getTotalLtcgSell(),
            loader.getTotalLtcgBuy(),
            loader.getTotalLtcg(),
            ltcgExemption,
            taxableLtcg
        );
        
        // Build Speculation Response
        double intraProfitLoss = loader.getTotalIntraSell() - loader.getTotalIntraBuy();
        SpeculationResponse speculation = new SpeculationResponse(
            loader.getTotalIntraSell(),
            loader.getTotalIntraBuy(),
            intraProfitLoss,
            loader.getTotalIntraTurnover()
        );
        
        // Build STCG quarterly breakdown from QuarterConfig
        List<QuarterDetailResponse> stcgQuarters = new ArrayList<>();
        List<Quarter> configQuarters = quarterConfig.getQuarters();
        
        for (int i = 0; i < configQuarters.size(); i++) {
            Quarter q = configQuarters.get(i);
            double stcgAmount = 0.0;
            double stcgBuy = 0.0;
            double stcgSell = 0.0;
            
            // Get STCG amount for each quarter
            switch (i) {
                case 0: stcgAmount = loader.getStcgQ1(); stcgBuy = loader.getStcgBuyQ1(); stcgSell = loader.getStcgSellQ1(); break;
                case 1: stcgAmount = loader.getStcgQ2(); stcgBuy = loader.getStcgBuyQ2(); stcgSell = loader.getStcgSellQ2(); break;
                case 2: stcgAmount = loader.getStcgQ3(); stcgBuy = loader.getStcgBuyQ3(); stcgSell = loader.getStcgSellQ3(); break;
                case 3: stcgAmount = loader.getStcgQ4(); stcgBuy = loader.getStcgBuyQ4(); stcgSell = loader.getStcgSellQ4(); break;
                case 4: stcgAmount = loader.getStcgQ5(); stcgBuy = loader.getStcgBuyQ5(); stcgSell = loader.getStcgSellQ5(); break;
            }
            
            stcgQuarters.add(QuarterDetailResponse.forStcg(
                i + 1,
                q.getCode(),
                q.getName(),
                q.getStartDate().toString(),
                q.getEndDate().toString(),
                stcgAmount,
                stcgSell,
                stcgBuy
            ));
        }

        // LTCG quarterly breakdown
        List<QuarterDetailResponse> ltcgQuarters = new ArrayList<>();
        for (int i = 0; i < configQuarters.size(); i++) {
            Quarter q = configQuarters.get(i);
            double amt = switch (i) {
                case 0 -> loader.getLtcgQ1();
                case 1 -> loader.getLtcgQ2();
                case 2 -> loader.getLtcgQ3();
                case 3 -> loader.getLtcgQ4();
                case 4 -> loader.getLtcgQ5();
                default -> 0.0;
            };
            double buy = switch (i) {
                case 0 -> loader.getLtcgBuyQ1();
                case 1 -> loader.getLtcgBuyQ2();
                case 2 -> loader.getLtcgBuyQ3();
                case 3 -> loader.getLtcgBuyQ4();
                case 4 -> loader.getLtcgBuyQ5();
                default -> 0.0;
            };
            double sell = switch (i) {
                case 0 -> loader.getLtcgSellQ1();
                case 1 -> loader.getLtcgSellQ2();
                case 2 -> loader.getLtcgSellQ3();
                case 3 -> loader.getLtcgSellQ4();
                case 4 -> loader.getLtcgSellQ5();
                default -> 0.0;
            };
            ltcgQuarters.add(QuarterDetailResponse.forLtcg(
                i + 1,
                q.getCode(),
                q.getName(),
                q.getStartDate().toString(),
                q.getEndDate().toString(),
                amt,
                sell,
                buy
            ));
        }

        // Speculation quarterly breakdown (profit/loss per quarter)
        List<QuarterDetailResponse> intraQuarters = new ArrayList<>();
        for (int i = 0; i < configQuarters.size(); i++) {
            Quarter q = configQuarters.get(i);
            double amt = switch (i) {
                case 0 -> loader.getIntraQ1();
                case 1 -> loader.getIntraQ2();
                case 2 -> loader.getIntraQ3();
                case 3 -> loader.getIntraQ4();
                case 4 -> loader.getIntraQ5();
                default -> 0.0;
            };
            double buy = switch (i) {
                case 0 -> loader.getIntraBuyQ1();
                case 1 -> loader.getIntraBuyQ2();
                case 2 -> loader.getIntraBuyQ3();
                case 3 -> loader.getIntraBuyQ4();
                case 4 -> loader.getIntraBuyQ5();
                default -> 0.0;
            };
            double sell = switch (i) {
                case 0 -> loader.getIntraSellQ1();
                case 1 -> loader.getIntraSellQ2();
                case 2 -> loader.getIntraSellQ3();
                case 3 -> loader.getIntraSellQ4();
                case 4 -> loader.getIntraSellQ5();
                default -> 0.0;
            };
            double turnover = switch (i) {
                case 0 -> loader.getIntraTurnoverQ1();
                case 1 -> loader.getIntraTurnoverQ2();
                case 2 -> loader.getIntraTurnoverQ3();
                case 3 -> loader.getIntraTurnoverQ4();
                case 4 -> loader.getIntraTurnoverQ5();
                default -> 0.0;
            };
            intraQuarters.add(QuarterDetailResponse.forSpeculation(
                i + 1,
                q.getCode(),
                q.getName(),
                q.getStartDate().toString(),
                q.getEndDate().toString(),
                amt,
                sell,
                buy,
                turnover
            ));
        }
        
        return new TaxCalculationResponse(
            financialYear,
            loader.getColumnMapping().getBrokerType().name(),
            loader.getColumnMapping().getBrokerType().getDisplayName(),
            stcg,
            ltcg,
            speculation,
            stcgQuarters,
            ltcgQuarters,
            intraQuarters,
            null, // Will be set by caller
            0     // Will be set by caller
        );
    }
    
    /**
     * Detect broker format from uploaded file
     */
    public Map<String, Object> detectBrokerFormat(MultipartFile file) throws Exception {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Save uploaded file temporarily
        String uploadedFilePath = saveUploadedFile(file);
        
        try {
            // Detect broker format
            ExcelHeaderDetector.BrokerDetectionResult result = 
                ExcelHeaderDetector.detectBrokerFormat(uploadedFilePath);
            
            ColumnMapping mapping = result.getColumnMapping();
            
            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("broker_type", result.getBrokerType().name());
            response.put("broker_name", result.getBrokerType().getDisplayName());
            response.put("auto_detected", result.isAutoDetected());
            response.put("message", result.getMessage());
            
            // Column mapping details
            Map<String, Object> mappingDetails = new HashMap<>();
            mappingDetails.put("sheet_index", mapping.getSheetIndex());
            mappingDetails.put("header_row", mapping.getHeaderRow());
            mappingDetails.put("data_start_row", mapping.getDataStartRow());
            mappingDetails.put("data_end_row", mapping.getDataEndRow());
            mappingDetails.put("buy_amount_column", mapping.getBuyAmountColumn());
            mappingDetails.put("sell_amount_column", mapping.getSellAmountColumn());
            mappingDetails.put("sell_date_column", mapping.getSellDateColumn());
            mappingDetails.put("days_held_column", mapping.getDaysHeldColumn());
            mappingDetails.put("stcg_column", mapping.getStcgColumn());
            mappingDetails.put("speculation_column", mapping.getSpeculationColumn());
            
            response.put("column_mapping", mappingDetails);

            // Header previews (rows around detected header) to debug mismatches
            try (FileInputStream fis2 = new FileInputStream(new File(uploadedFilePath));
                 XSSFWorkbook wb = new XSSFWorkbook(fis2)) {
                XSSFSheet sheet = wb.getSheetAt(mapping.getSheetIndex());
                int[] rowsToRead = new int[] {
                    Math.max(0, mapping.getHeaderRow() - 1),
                    mapping.getHeaderRow(),
                    mapping.getHeaderRow() + 1,
                    Math.max(mapping.getHeaderRow(), mapping.getDataStartRow() - 1)
                };
                Map<String, Object> headerPreview = new HashMap<>();
                headerPreview.put("sheet_index", mapping.getSheetIndex());
                Map<Integer, String[]> rows = new HashMap<>();
                for (int rIdx : rowsToRead) {
                    Row r = sheet.getRow(rIdx);
                    if (r == null) continue;
                    int last = r.getLastCellNum();
                    String[] cols = new String[last];
                    for (int i = 0; i < last; i++) {
                        Cell c = r.getCell(i);
                        cols[i] = c == null ? "" : c.toString();
                    }
                    rows.put(rIdx, cols);
                }
                headerPreview.put("rows", rows);
                response.put("header_preview", headerPreview);
            } catch (Exception e) {
                HurdleLogger.warn("Failed to read header preview: " + e.getMessage());
            }
            
            return response;
            
        } finally {
            // Clean up temporary file
            cleanupFile(uploadedFilePath);
        }
    }
    
    /**
     * Save uploaded file to temporary location
     */
    private String saveUploadedFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filePath.toString();
    }
    
    /**
     * Get LTCG exemption limit based on financial year
     * As per Indian tax law:
     * - FY 2024-25 onwards: ₹1,25,000
     * - FY 2023-24 and earlier: ₹1,00,000
     */
    private double getLtcgExemptionLimit(String financialYear) {
        if (financialYear == null) return 100000.0;
        
        // Extract start year from "FY YYYY-YY" format
        try {
            String[] parts = financialYear.replace("FY ", "").split("-");
            int startYear = Integer.parseInt(parts[0]);
            
            // FY 2024-25 onwards: ₹1.25 lakh exemption
            if (startYear >= 2024) {
                return 125000.0;
            }
        } catch (Exception e) {
            // Fallback to default
        }
        
        // Default: ₹1 lakh exemption (for FY 2023-24 and earlier)
        return 100000.0;
    }
    
    /**
     * Clean up temporary file
     */
    private void cleanupFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            // Log but don't fail if cleanup fails
            System.err.println("Failed to cleanup file: " + filePath);
        }
    }
}
