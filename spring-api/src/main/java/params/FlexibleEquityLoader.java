package params;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import exception.InvalidSecurityException;
import logging.HurdleLogger;
import util.ColumnMapping;
import util.ExcelHeaderDetector;
import util.QuarterConfig;

/**
 * Flexible Equity Loader that can handle multiple broker formats
 * Uses ColumnMapping to dynamically read data from any Excel structure
 */
public class FlexibleEquityLoader {
    
    private XSSFSheet dataSheet;
    private XSSFWorkbook workbook;
    private ColumnMapping columnMapping;
    private QuarterConfig quarterConfig;
    
    private double totalStcgBuy;
    private double totalStcgSell;
    private double totalStcg;
    private double totalLtcgBuy;
    private double totalLtcgSell;
    private double totalLtcg;
    private double totalIntraBuy;
    private double totalIntraSell;
    private double totalIntraTurnover;
    private double stcgQ1, stcgQ2, stcgQ3, stcgQ4, stcgQ5;
    private double ltcgQ1, ltcgQ2, ltcgQ3, ltcgQ4, ltcgQ5;
    private double intraQ1, intraQ2, intraQ3, intraQ4, intraQ5;

    private final double[] stcgBuyQuarter = new double[5];
    private final double[] stcgSellQuarter = new double[5];
    private final double[] ltcgBuyQuarter = new double[5];
    private final double[] ltcgSellQuarter = new double[5];
    private final double[] intraBuyQuarter = new double[5];
    private final double[] intraSellQuarter = new double[5];
    private final double[] intraTurnoverQuarter = new double[5];

    private enum QuarterType { STCG, LTCG, INTRADAY }
    
    /**
     * Constructor with auto-detection
     */
    public FlexibleEquityLoader(String filePath, QuarterConfig quarterConfig) throws Exception {
        this.quarterConfig = quarterConfig;
        
        // Auto-detect broker format
        ExcelHeaderDetector.BrokerDetectionResult detection = 
            ExcelHeaderDetector.detectBrokerFormat(filePath);
        
        this.columnMapping = detection.getColumnMapping();
        
        HurdleLogger.info("Detected broker: " + detection.getBrokerType().getDisplayName());
        HurdleLogger.info("Detection message: " + detection.getMessage());
        HurdleLogger.info("Column mapping: " + columnMapping);
        
        // Load workbook and find appropriate sheet
        initializeWorkbook(filePath);
    }
    
    /**
     * Constructor with explicit column mapping
     */
    public FlexibleEquityLoader(String filePath, QuarterConfig quarterConfig, ColumnMapping mapping) throws Exception {
        this.quarterConfig = quarterConfig;
        this.columnMapping = mapping;
        
        HurdleLogger.info("Using explicit column mapping for: " + mapping.getBrokerType().getDisplayName());
        
        initializeWorkbook(filePath);
    }
    
    private void initializeWorkbook(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            this.workbook = new XSSFWorkbook(fis);
            
            // Use the sheet index from columnMapping (no auto-switching)
            this.dataSheet = this.workbook.getSheetAt(columnMapping.getSheetIndex());
            
            if (this.dataSheet == null) {
                throw new InvalidSecurityException(
                    InvalidSecurityException.ErrorCode.INVALID_DATA,
                    "Data sheet not found at index: " + columnMapping.getSheetIndex()
                );
            }
            
            HurdleLogger.info("Reading from sheet: " + this.dataSheet.getSheetName() + " (index " + columnMapping.getSheetIndex() + ")");
        }
    }
    
    /**
     * Load and calculate all equity data
     */
    public void initialize() throws IOException {
        try {
            HurdleLogger.info("Initializing Flexible Equity Loader...");
            System.out.println("Initializing Flexible Equity Loader with " + 
                columnMapping.getBrokerType().getDisplayName() + " format...");
            
            loadEquities();
            
            HurdleLogger.info("Flexible equity loader initialized successfully");
            System.out.println("\nFlexible equity loader initialized SUCCESSFULLY :)\n");
            
        } catch (Exception e) {
            HurdleLogger.error("Error during initialization: " + e.getMessage(), e);
            throw e;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }
    
    private void loadEquities() {
        Iterator<Row> rowIterator = this.dataSheet.iterator();
        
        double totalBuySTCG = 0;
        double totalSellSTCG = 0;
        double totalBuyLTCG = 0;
        double totalSellLTCG = 0;
        double totalBuyINTRA = 0;
        double totalSellINTRA = 0;
        double totalIntraTurnoverAbs = 0;
        
        int startRow = columnMapping.getDataStartRow();
        Integer endRow = columnMapping.getDataEndRow();
        int rowNum = 0;
        int processedCount = 0;
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            rowNum = row.getRowNum();
            
            // Skip until start row
            if (rowNum < startRow) continue;
            
            // Stop at end row if specified
            if (endRow != null && rowNum > endRow) break;
            
            try {
                // Check if row is empty
                if (isRowEmpty(row)) continue;
                
                // Read values using flexible column mapping
                int daysHeld = getDaysHeld(row);
                double buyAmount = getCellValueAsDouble(row, columnMapping.getBuyAmountColumn());
                double sellAmount = getCellValueAsDouble(row, columnMapping.getSellAmountColumn());
                
                // Skip if both buy and sell are zero
                if (buyAmount == 0 && sellAmount == 0) continue;
                
                processedCount++;
                
                LocalDate sellDate = getSellDate(row);
                int quarterNum = getQuarterNumber(sellDate);

                if (daysHeld == 0) {
                    // Intraday/Speculation (bought and sold same day)
                    totalBuyINTRA += buyAmount;
                    totalSellINTRA += sellAmount;
                    double profit = sellAmount - buyAmount;
                    double absTurnover = Math.abs(profit); // turnover = abs(realized P&L)
                    totalIntraTurnoverAbs += absTurnover;
                    if (quarterNum > 0) {
                        assignToQuarter(quarterNum, profit, QuarterType.INTRADAY);
                        intraBuyQuarter[quarterNum - 1] += buyAmount;
                        intraSellQuarter[quarterNum - 1] += sellAmount;
                        intraTurnoverQuarter[quarterNum - 1] += absTurnover;
                    }
                } else if (daysHeld <= 365) {
                    // STCG - Short-Term Capital Gains (held ≤ 12 months)
                    totalBuySTCG += buyAmount;
                    totalSellSTCG += sellAmount;
                    
                    // Calculate quarterly STCG
                    double stcgValue = columnMapping.getStcgColumn() >= 0
                        ? getCellValueAsDouble(row, columnMapping.getStcgColumn())
                        : (sellAmount - buyAmount);
                    if (quarterNum > 0) {
                        assignToQuarter(quarterNum, stcgValue, QuarterType.STCG);
                        stcgBuyQuarter[quarterNum - 1] += buyAmount;
                        stcgSellQuarter[quarterNum - 1] += sellAmount;
                    }
                } else {
                    // LTCG - Long-Term Capital Gains (held > 12 months)
                    totalBuyLTCG += buyAmount;
                    totalSellLTCG += sellAmount;
                    double profit = sellAmount - buyAmount; // derive directly to avoid mixing STCG column
                    if (quarterNum > 0) {
                        assignToQuarter(quarterNum, profit, QuarterType.LTCG);
                        ltcgBuyQuarter[quarterNum - 1] += buyAmount;
                        ltcgSellQuarter[quarterNum - 1] += sellAmount;
                    }
                }
                
            } catch (Exception e) {
                HurdleLogger.warn("Error processing row " + rowNum + ": " + e.getMessage());
                // Continue processing other rows
            }
        }
        
        // Calculate totals
        this.totalStcgBuy = totalBuySTCG;
        this.totalStcgSell = totalSellSTCG;
        this.totalStcg = totalSellSTCG - totalBuySTCG;
        
        this.totalLtcgBuy = totalBuyLTCG;
        this.totalLtcgSell = totalSellLTCG;
        this.totalLtcg = totalSellLTCG - totalBuyLTCG;
        
        this.totalIntraBuy = totalBuyINTRA;
        this.totalIntraSell = totalSellINTRA;
        this.totalIntraTurnover = totalIntraTurnoverAbs;
        
        HurdleLogger.info("Processed " + processedCount + " transaction rows");
        HurdleLogger.info("STCG: Buy=" + totalStcgBuy + ", Sell=" + totalStcgSell + ", Total=" + totalStcg);
        HurdleLogger.info("LTCG: Buy=" + totalLtcgBuy + ", Sell=" + totalLtcgSell + ", Total=" + totalLtcg);
        HurdleLogger.info("Intraday: Buy=" + totalIntraBuy + ", Sell=" + totalIntraSell + ", Turnover=" + totalIntraTurnover);
    }
    
    private boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
    
    private int getDaysHeld(Row row) {
        try {
            return (int) getCellValueAsDouble(row, columnMapping.getDaysHeldColumn());
        } catch (Exception e) {
            return 0; // Default to intraday if can't read
        }
    }
    
    private LocalDate getSellDate(Row row) {
        try {
            Cell cell = row.getCell(columnMapping.getSellDateColumn());
            if (cell == null) return null;
            
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue();
                return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (Exception e) {
            HurdleLogger.warn("Could not parse sell date: " + e.getMessage());
        }
        return null;
    }
    
    private double getCellValueAsDouble(Row row, int columnIndex) {
        if (columnIndex < 0) return 0.0;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return 0.0;
        
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    if (value.isEmpty()) return 0.0;
                    // Remove common formatting characters
                    value = value.replaceAll("[,₹$]", "");
                    return Double.parseDouble(value);
                case FORMULA:
                    return cell.getNumericCellValue();
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            HurdleLogger.warn("Error reading cell at column " + columnIndex + ": " + e.getMessage());
            return 0.0;
        }
    }

    private int getQuarterNumber(LocalDate sellDate) {
        if (sellDate == null || quarterConfig == null) return -1;
        util.Quarter quarter = quarterConfig.getQuarterForDate(sellDate);
        if (quarter == null || quarter.getCode() == null || quarter.getCode().length() < 2) return -1;
        try {
            return Integer.parseInt(quarter.getCode().substring(1));
        } catch (Exception e) {
            return -1;
        }
    }
    
    private void assignToQuarter(int quarter, double value, QuarterType type) {
        switch (type) {
            case STCG:
                switch (quarter) {
                    case 1: stcgQ1 += value; break;
                    case 2: stcgQ2 += value; break;
                    case 3: stcgQ3 += value; break;
                    case 4: stcgQ4 += value; break;
                    case 5: stcgQ5 += value; break;
                }
                break;
            case LTCG:
                switch (quarter) {
                    case 1: ltcgQ1 += value; break;
                    case 2: ltcgQ2 += value; break;
                    case 3: ltcgQ3 += value; break;
                    case 4: ltcgQ4 += value; break;
                    case 5: ltcgQ5 += value; break;
                }
                break;
            case INTRADAY:
                switch (quarter) {
                    case 1: intraQ1 += value; break;
                    case 2: intraQ2 += value; break;
                    case 3: intraQ3 += value; break;
                    case 4: intraQ4 += value; break;
                    case 5: intraQ5 += value; break;
                }
                break;
        }
    }
    
    // Getters
    public double getTotalStcgBuy() { return totalStcgBuy; }
    public double getTotalStcgSell() { return totalStcgSell; }
    public double getTotalStcg() { return totalStcg; }
    public double getTotalLtcgBuy() { return totalLtcgBuy; }
    public double getTotalLtcgSell() { return totalLtcgSell; }
    public double getTotalLtcg() { return totalLtcg; }
    public double getTotalIntraBuy() { return totalIntraBuy; }
    public double getTotalIntraSell() { return totalIntraSell; }
    public double getTotalIntraTurnover() { return totalIntraTurnover; }
    public double getStcgQ1() { return stcgQ1; }
    public double getStcgQ2() { return stcgQ2; }
    public double getStcgQ3() { return stcgQ3; }
    public double getStcgQ4() { return stcgQ4; }
    public double getStcgQ5() { return stcgQ5; }

    public double getLtcgQ1() { return ltcgQ1; }
    public double getLtcgQ2() { return ltcgQ2; }
    public double getLtcgQ3() { return ltcgQ3; }
    public double getLtcgQ4() { return ltcgQ4; }
    public double getLtcgQ5() { return ltcgQ5; }

    public double getIntraQ1() { return intraQ1; }
    public double getIntraQ2() { return intraQ2; }
    public double getIntraQ3() { return intraQ3; }
    public double getIntraQ4() { return intraQ4; }
    public double getIntraQ5() { return intraQ5; }
    public double getStcgBuyQ1() { return stcgBuyQuarter[0]; }
    public double getStcgBuyQ2() { return stcgBuyQuarter[1]; }
    public double getStcgBuyQ3() { return stcgBuyQuarter[2]; }
    public double getStcgBuyQ4() { return stcgBuyQuarter[3]; }
    public double getStcgBuyQ5() { return stcgBuyQuarter[4]; }
    public double getStcgSellQ1() { return stcgSellQuarter[0]; }
    public double getStcgSellQ2() { return stcgSellQuarter[1]; }
    public double getStcgSellQ3() { return stcgSellQuarter[2]; }
    public double getStcgSellQ4() { return stcgSellQuarter[3]; }
    public double getStcgSellQ5() { return stcgSellQuarter[4]; }
    public double getLtcgBuyQ1() { return ltcgBuyQuarter[0]; }
    public double getLtcgBuyQ2() { return ltcgBuyQuarter[1]; }
    public double getLtcgBuyQ3() { return ltcgBuyQuarter[2]; }
    public double getLtcgBuyQ4() { return ltcgBuyQuarter[3]; }
    public double getLtcgBuyQ5() { return ltcgBuyQuarter[4]; }
    public double getLtcgSellQ1() { return ltcgSellQuarter[0]; }
    public double getLtcgSellQ2() { return ltcgSellQuarter[1]; }
    public double getLtcgSellQ3() { return ltcgSellQuarter[2]; }
    public double getLtcgSellQ4() { return ltcgSellQuarter[3]; }
    public double getLtcgSellQ5() { return ltcgSellQuarter[4]; }
    public double getIntraBuyQ1() { return intraBuyQuarter[0]; }
    public double getIntraBuyQ2() { return intraBuyQuarter[1]; }
    public double getIntraBuyQ3() { return intraBuyQuarter[2]; }
    public double getIntraBuyQ4() { return intraBuyQuarter[3]; }
    public double getIntraBuyQ5() { return intraBuyQuarter[4]; }
    public double getIntraSellQ1() { return intraSellQuarter[0]; }
    public double getIntraSellQ2() { return intraSellQuarter[1]; }
    public double getIntraSellQ3() { return intraSellQuarter[2]; }
    public double getIntraSellQ4() { return intraSellQuarter[3]; }
    public double getIntraSellQ5() { return intraSellQuarter[4]; }
    public double getIntraTurnoverQ1() { return intraTurnoverQuarter[0]; }
    public double getIntraTurnoverQ2() { return intraTurnoverQuarter[1]; }
    public double getIntraTurnoverQ3() { return intraTurnoverQuarter[2]; }
    public double getIntraTurnoverQ4() { return intraTurnoverQuarter[3]; }
    public double getIntraTurnoverQ5() { return intraTurnoverQuarter[4]; }
    public ColumnMapping getColumnMapping() { return columnMapping; }
    public QuarterConfig getQuarterConfig() { return quarterConfig; }
}
