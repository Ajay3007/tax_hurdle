package util;

/**
 * Defines column positions and structure for a specific broker's Excel format
 * This allows flexible parsing of different broker formats
 */
public class ColumnMapping {
    private BrokerType brokerType;
    private int sheetIndex;
    private int headerRow;
    private int dataStartRow;
    private Integer dataEndRow; // null = read till end
    
    // Column indices (0-based)
    private int tradeDateColumn;
    private int symbolColumn;
    private int buyAmountColumn;
    private int sellAmountColumn;
    private int sellDateColumn;
    private int daysHeldColumn;
    private int stcgColumn;
    private int speculationColumn;
    
    // Optional columns
    private Integer quantityColumn;
    private Integer priceColumn;
    private Integer isinColumn;
    
    public ColumnMapping(BrokerType brokerType) {
        this.brokerType = brokerType;
        this.sheetIndex = 1; // Default
        this.headerRow = 24; // Default (row 25 in 1-based)
        this.dataStartRow = 25; // Default

        // unset markers
        this.tradeDateColumn = -1;
        this.symbolColumn = -1;
        this.buyAmountColumn = -1;
        this.sellAmountColumn = -1;
        this.sellDateColumn = -1;
        this.daysHeldColumn = -1;
        this.stcgColumn = -1;
        this.speculationColumn = -1;
    }
    
    // Getters and Setters
    public BrokerType getBrokerType() {
        return brokerType;
    }
    
    public void setBrokerType(BrokerType brokerType) {
        this.brokerType = brokerType;
    }
    
    public int getSheetIndex() {
        return sheetIndex;
    }
    
    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
    
    public int getHeaderRow() {
        return headerRow;
    }
    
    public void setHeaderRow(int headerRow) {
        this.headerRow = headerRow;
    }
    
    public int getDataStartRow() {
        return dataStartRow;
    }
    
    public void setDataStartRow(int dataStartRow) {
        this.dataStartRow = dataStartRow;
    }
    
    public Integer getDataEndRow() {
        return dataEndRow;
    }
    
    public void setDataEndRow(Integer dataEndRow) {
        this.dataEndRow = dataEndRow;
    }
    
    public int getTradeDateColumn() {
        return tradeDateColumn;
    }
    
    public void setTradeDateColumn(int tradeDateColumn) {
        this.tradeDateColumn = tradeDateColumn;
    }
    
    public int getSymbolColumn() {
        return symbolColumn;
    }
    
    public void setSymbolColumn(int symbolColumn) {
        this.symbolColumn = symbolColumn;
    }
    
    public int getBuyAmountColumn() {
        return buyAmountColumn;
    }
    
    public void setBuyAmountColumn(int buyAmountColumn) {
        this.buyAmountColumn = buyAmountColumn;
    }
    
    public int getSellAmountColumn() {
        return sellAmountColumn;
    }
    
    public void setSellAmountColumn(int sellAmountColumn) {
        this.sellAmountColumn = sellAmountColumn;
    }
    
    public int getSellDateColumn() {
        return sellDateColumn;
    }
    
    public void setSellDateColumn(int sellDateColumn) {
        this.sellDateColumn = sellDateColumn;
    }
    
    public int getDaysHeldColumn() {
        return daysHeldColumn;
    }
    
    public void setDaysHeldColumn(int daysHeldColumn) {
        this.daysHeldColumn = daysHeldColumn;
    }
    
    public int getStcgColumn() {
        return stcgColumn;
    }
    
    public void setStcgColumn(int stcgColumn) {
        this.stcgColumn = stcgColumn;
    }
    
    public int getSpeculationColumn() {
        return speculationColumn;
    }
    
    public void setSpeculationColumn(int speculationColumn) {
        this.speculationColumn = speculationColumn;
    }
    
    public Integer getQuantityColumn() {
        return quantityColumn;
    }
    
    public void setQuantityColumn(Integer quantityColumn) {
        this.quantityColumn = quantityColumn;
    }
    
    public Integer getPriceColumn() {
        return priceColumn;
    }
    
    public void setPriceColumn(Integer priceColumn) {
        this.priceColumn = priceColumn;
    }
    
    public Integer getIsinColumn() {
        return isinColumn;
    }
    
    public void setIsinColumn(Integer isinColumn) {
        this.isinColumn = isinColumn;
    }
    
    @Override
    public String toString() {
        return "ColumnMapping{" +
                "brokerType=" + brokerType +
                ", sheetIndex=" + sheetIndex +
                ", dataStartRow=" + dataStartRow +
                ", dataEndRow=" + dataEndRow +
                '}';
    }
    
    /**
     * Create default mapping for Upstox format (current system)
     */
    public static ColumnMapping createUpstoxMapping() {
        ColumnMapping mapping = new ColumnMapping(BrokerType.UPSTOX);
        mapping.setSheetIndex(1);
        mapping.setHeaderRow(25); // Header row with column names (0-based)
        mapping.setDataStartRow(26); // First data row after header (0-based)
        mapping.setDataEndRow(null); // Read until the end of sheet
        
        // Column indices based on latest Upstox contract note export
        mapping.setBuyAmountColumn(8);   // "Buy Amt"
        mapping.setSellAmountColumn(11); // "Sell Amt"
        mapping.setSellDateColumn(9);    // "Sell Date"
        mapping.setDaysHeldColumn(12);   // "Days"
        mapping.setStcgColumn(14);       // "Short Term" profit
        mapping.setSpeculationColumn(16); // "Speculation" profit (not currently used)
        
        return mapping;
    }
    
    /**
     * Create mapping for Zerodha format
     * Uses Sheet 0 "Tradewise Exits from YYYY-MM-DD"
     * Note: Excel data starts at Column B (index 1), not Column A
     */
    public static ColumnMapping createZerodhaMapping() {
        ColumnMapping mapping = new ColumnMapping(BrokerType.ZERODHA);
        mapping.setSheetIndex(0); // Sheet 0 - "Tradewise Exits from..."
        mapping.setHeaderRow(23); // Row 24 in 1-based (0-indexed: 23)
        mapping.setDataStartRow(24); // Row 25 in 1-based (0-indexed: 24)
        
        // Zerodha column positions (0-indexed, data starts at Column B = index 1)
        // Row 24: [Skip A] | Symbol | ISIN | Entry Date | Exit Date | Quantity | Buy Value | Sell Value | Profit | Period of Holding...
        mapping.setSymbolColumn(1); // Column B - Symbol
        mapping.setTradeDateColumn(3); // Column D - Entry Date
        mapping.setSellDateColumn(4); // Column E - Exit Date
        mapping.setQuantityColumn(5); // Column F - Quantity
        mapping.setBuyAmountColumn(6); // Column G - Buy Value
        mapping.setSellAmountColumn(7); // Column H - Sell Value
        mapping.setStcgColumn(8); // Column I - Profit
        mapping.setDaysHeldColumn(9); // Column J - Period of Holding
        mapping.setSpeculationColumn(12); // Column M - Turnover (for intraday)
        
        return mapping;
    }
    
    /**
     * Create generic mapping - will attempt auto-detection
     */
    public static ColumnMapping createGenericMapping() {
        ColumnMapping mapping = new ColumnMapping(BrokerType.GENERIC);
        mapping.setSheetIndex(0);
        mapping.setHeaderRow(0);
        mapping.setDataStartRow(1);
        
        return mapping;
    }
}
