package com.investinghurdle.api.dto;

/**
 * Response DTO for Long-Term Capital Gains (LTCG)
 */
public class LtcgResponse {
    private double fullValueOfConsideration;
    private double costOfAcquisition;
    private double totalLtcg;
    private double exemptionLimit;
    private double taxableLtcg;

    private boolean positive;
    private String displayColor;
    
    public LtcgResponse(double fullValueOfConsideration, double costOfAcquisition, 
                        double totalLtcg, double exemptionLimit, double taxableLtcg) {
        this.fullValueOfConsideration = fullValueOfConsideration;
        this.costOfAcquisition = costOfAcquisition;
        this.totalLtcg = totalLtcg;
        this.exemptionLimit = exemptionLimit;
        this.taxableLtcg = taxableLtcg;
        this.positive = totalLtcg > 0;
        this.displayColor = this.positive ? "green" : "red";
    }
    
    // Getters and Setters
    public double getFullValueOfConsideration() {
        return fullValueOfConsideration;
    }
    
    public void setFullValueOfConsideration(double fullValueOfConsideration) {
        this.fullValueOfConsideration = fullValueOfConsideration;
    }
    
    public double getCostOfAcquisition() {
        return costOfAcquisition;
    }
    
    public void setCostOfAcquisition(double costOfAcquisition) {
        this.costOfAcquisition = costOfAcquisition;
    }
    
    public double getTotalLtcg() {
        return totalLtcg;
    }
    
    public void setTotalLtcg(double totalLtcg) {
        this.totalLtcg = totalLtcg;
    }
    
    public double getExemptionLimit() {
        return exemptionLimit;
    }
    
    public void setExemptionLimit(double exemptionLimit) {
        this.exemptionLimit = exemptionLimit;
    }
    
    public double getTaxableLtcg() {
        return taxableLtcg;
    }
    
    public void setTaxableLtcg(double taxableLtcg) {
        this.taxableLtcg = taxableLtcg;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    public String getDisplayColor() {
        return displayColor;
    }

    public void setDisplayColor(String displayColor) {
        this.displayColor = displayColor;
    }
}
