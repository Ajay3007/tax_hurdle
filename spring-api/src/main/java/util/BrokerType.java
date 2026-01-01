package util;

/**
 * Enumeration of supported broker types
 * Each broker has different Excel format/structure
 */
public enum BrokerType {
    UPSTOX("Upstox", "Upstox Securities"),
    ZERODHA("Zerodha", "Zerodha Broking"),
    ICICI_DIRECT("ICICI Direct", "ICICI Securities"),
    GROWW("Groww", "Groww"),
    ANGEL_ONE("Angel One", "Angel Broking"),
    HDFC_SECURITIES("HDFC Securities", "HDFC Securities"),
    GENERIC("Generic", "Generic Format"),
    UNKNOWN("Unknown", "Unknown Broker");
    
    private final String displayName;
    private final String fullName;
    
    BrokerType(String displayName, String fullName) {
        this.displayName = displayName;
        this.fullName = fullName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getFullName() {
        return fullName;
    }
}
