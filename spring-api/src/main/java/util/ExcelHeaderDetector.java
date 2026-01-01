package util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import logging.HurdleLogger;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Auto-detects broker format by analyzing Excel file headers
 * Attempts to identify column positions for required fields
 */
public class ExcelHeaderDetector {
    
    // Common header patterns for different fields
    private static final Pattern BUY_PATTERN = Pattern.compile("(?i).*(buy|purchase|bought).*(amount|value|amt).*");
    private static final Pattern SELL_PATTERN = Pattern.compile("(?i).*(sell|sale|sold).*(amount|value|amt).*");
    private static final Pattern DATE_PATTERN = Pattern.compile("(?i).*(date|dt).*");
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("(?i).*(symbol|scrip|stock|security).*");
    private static final Pattern DAYS_PATTERN = Pattern.compile("(?i).*(days|hold|holding|period).*");
    private static final Pattern STCG_PATTERN = Pattern.compile("(?i).*(stcg|profit|p&l|pnl|pl|short\\s*term).*");
    private static final Pattern SPECULATION_PATTERN = Pattern.compile("(?i).*(speculation|intraday|specul|turnover).*");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("(?i).*(quantity|qty|units).*");
    
    /**
     * Detect broker type and column mapping from Excel file
     */
    public static BrokerDetectionResult detectBrokerFormat(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            
            // Try to detect broker from workbook metadata or sheet names
            BrokerType detectedBroker = detectBrokerFromMetadata(workbook);
            
            // Try multiple sheets to find data
            for (int sheetIdx = 0; sheetIdx < Math.min(3, workbook.getNumberOfSheets()); sheetIdx++) {
                XSSFSheet sheet = workbook.getSheetAt(sheetIdx);
                
                // Try to find header row (look in first 30 rows)
                for (int rowIdx = 0; rowIdx < Math.min(30, sheet.getPhysicalNumberOfRows()); rowIdx++) {
                    Row row = sheet.getRow(rowIdx);
                    if (row == null) continue;
                    
                    Map<String, Integer> headerMap = analyzeHeaderRow(row);
                    
                    // Check if this looks like a valid header
                    if (headerMap.containsKey("BUY") && headerMap.containsKey("SELL")) {
                        ColumnMapping mapping = buildColumnMapping(
                            detectedBroker, 
                            sheetIdx, 
                            rowIdx, 
                            headerMap,
                            sheet
                        );
                        
                        return new BrokerDetectionResult(
                            detectedBroker,
                            mapping,
                            true,
                            "Successfully detected " + detectedBroker.getDisplayName() + " format"
                        );
                    }
                }
            }
            
            // Fallback: If broker detected from metadata but no header match, use broker template
            if (detectedBroker != BrokerType.UNKNOWN) {
                HurdleLogger.info("Using template for detected broker: " + detectedBroker.getDisplayName());
                ColumnMapping brokerMapping;
                switch (detectedBroker) {
                    case ZERODHA:
                        brokerMapping = ColumnMapping.createZerodhaMapping();
                        break;
                    case UPSTOX:
                        brokerMapping = ColumnMapping.createUpstoxMapping();
                        break;
                    default:
                        brokerMapping = ColumnMapping.createGenericMapping();
                }
                return new BrokerDetectionResult(
                    detectedBroker,
                    brokerMapping,
                    true,
                    "Detected " + detectedBroker.getDisplayName() + " from file metadata, using template mapping"
                );
            }
            
            // Fallback: Use Upstox format if no detection worked
            HurdleLogger.warn("Could not auto-detect format, using Upstox default");
            return new BrokerDetectionResult(
                BrokerType.UPSTOX,
                ColumnMapping.createUpstoxMapping(),
                false,
                "Auto-detection failed, using Upstox default format"
            );
            
        }
    }
    
    /**
     * Try to detect broker from workbook properties or sheet names
     */
    private static BrokerType detectBrokerFromMetadata(XSSFWorkbook workbook) {
        // Check sheet names
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String sheetName = workbook.getSheetName(i).toLowerCase();
            
            if (sheetName.contains("upstox")) return BrokerType.UPSTOX;
            if (sheetName.contains("zerodha") || sheetName.contains("tradewise exits")) return BrokerType.ZERODHA;
            if (sheetName.contains("icici")) return BrokerType.ICICI_DIRECT;
            if (sheetName.contains("groww")) return BrokerType.GROWW;
            if (sheetName.contains("angel")) return BrokerType.ANGEL_ONE;
            if (sheetName.contains("hdfc")) return BrokerType.HDFC_SECURITIES;
        }
        
        // Check for specific patterns in first few rows
        XSSFSheet firstSheet = workbook.getSheetAt(0);
        for (int i = 0; i < Math.min(10, firstSheet.getPhysicalNumberOfRows()); i++) {
            Row row = firstSheet.getRow(i);
            if (row == null) continue;
            
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING) {
                    String value = cell.getStringCellValue().toLowerCase();
                    
                    if (value.contains("upstox")) return BrokerType.UPSTOX;
                    if (value.contains("zerodha") || value.contains("view zerodha")) return BrokerType.ZERODHA;
                    if (value.contains("icici direct")) return BrokerType.ICICI_DIRECT;
                    if (value.contains("groww")) return BrokerType.GROWW;
                    if (value.contains("angel one") || value.contains("angel broking")) 
                        return BrokerType.ANGEL_ONE;
                    if (value.contains("hdfc securities")) return BrokerType.HDFC_SECURITIES;
                }
            }
        }
        
        return BrokerType.UNKNOWN;
    }
    
    /**
     * Analyze a row to see if it contains header information
     */
    private static Map<String, Integer> analyzeHeaderRow(Row row) {
        Map<String, Integer> headerMap = new HashMap<>();
        
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.STRING) continue;
            
            String header = cell.getStringCellValue().trim();
            if (header.isEmpty()) continue;
            
            int colIdx = cell.getColumnIndex();
            
            // Match against known patterns
            if (BUY_PATTERN.matcher(header).matches()) {
                headerMap.put("BUY", colIdx);
            } else if (SELL_PATTERN.matcher(header).matches()) {
                headerMap.put("SELL", colIdx);
            } else if (DATE_PATTERN.matcher(header).matches()) {
                if (!headerMap.containsKey("DATE")) {
                    headerMap.put("DATE", colIdx);
                } else if (!headerMap.containsKey("SELL_DATE")) {
                    headerMap.put("SELL_DATE", colIdx);
                }
            } else if (SYMBOL_PATTERN.matcher(header).matches()) {
                headerMap.put("SYMBOL", colIdx);
            } else if (DAYS_PATTERN.matcher(header).matches()) {
                headerMap.put("DAYS", colIdx);
            } else if (STCG_PATTERN.matcher(header).matches()) {
                headerMap.put("STCG", colIdx);
            } else if (SPECULATION_PATTERN.matcher(header).matches()) {
                headerMap.put("SPECULATION", colIdx);
            } else if (QUANTITY_PATTERN.matcher(header).matches()) {
                headerMap.put("QUANTITY", colIdx);
            }
        }
        
        return headerMap;
    }
    
    /**
     * Build ColumnMapping from detected headers
     */
    private static ColumnMapping buildColumnMapping(
            BrokerType broker,
            int sheetIdx,
            int headerRow,
            Map<String, Integer> headerMap,
            XSSFSheet sheet) {
        
        ColumnMapping mapping = new ColumnMapping(broker);
        mapping.setSheetIndex(sheetIdx);
        mapping.setHeaderRow(headerRow);
        mapping.setDataStartRow(headerRow + 1);
        
        // Set detected column indices
        if (headerMap.containsKey("BUY")) {
            mapping.setBuyAmountColumn(headerMap.get("BUY"));
        }
        if (headerMap.containsKey("SELL")) {
            mapping.setSellAmountColumn(headerMap.get("SELL"));
        }
        if (headerMap.containsKey("DATE")) {
            mapping.setTradeDateColumn(headerMap.get("DATE"));
        }
        if (headerMap.containsKey("SELL_DATE")) {
            mapping.setSellDateColumn(headerMap.get("SELL_DATE"));
        }
        if (headerMap.containsKey("SYMBOL")) {
            mapping.setSymbolColumn(headerMap.get("SYMBOL"));
        }
        if (headerMap.containsKey("DAYS")) {
            mapping.setDaysHeldColumn(headerMap.get("DAYS"));
        }
        if (headerMap.containsKey("STCG")) {
            mapping.setStcgColumn(headerMap.get("STCG"));
        }
        if (headerMap.containsKey("SPECULATION")) {
            mapping.setSpeculationColumn(headerMap.get("SPECULATION"));
        }
        if (headerMap.containsKey("QUANTITY")) {
            mapping.setQuantityColumn(headerMap.get("QUANTITY"));
        }
        
        // Detect end row (last row with data)
        int lastRow = sheet.getLastRowNum();
        mapping.setDataEndRow(lastRow);
        
        HurdleLogger.info("Built column mapping: " + mapping);
        
        return mapping;
    }
    
    /**
     * Result of broker detection
     */
    public static class BrokerDetectionResult {
        private final BrokerType brokerType;
        private final ColumnMapping columnMapping;
        private final boolean autoDetected;
        private final String message;
        
        public BrokerDetectionResult(BrokerType brokerType, ColumnMapping columnMapping, 
                                     boolean autoDetected, String message) {
            this.brokerType = brokerType;
            this.columnMapping = columnMapping;
            this.autoDetected = autoDetected;
            this.message = message;
        }
        
        public BrokerType getBrokerType() {
            return brokerType;
        }
        
        public ColumnMapping getColumnMapping() {
            return columnMapping;
        }
        
        public boolean isAutoDetected() {
            return autoDetected;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
