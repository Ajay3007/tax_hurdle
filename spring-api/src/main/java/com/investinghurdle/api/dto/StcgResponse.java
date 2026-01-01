package com.investinghurdle.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for STCG (Short-Term Capital Gains) calculations
 */
@Schema(description = "Short-Term Capital Gains calculation results")
public class StcgResponse {
    
    @Schema(description = "Full value of consideration (total sell amount)", example = "447343.86")
    @JsonProperty("full_value_of_consideration")
    private double fullValueOfConsideration;
    
    @Schema(description = "Cost of acquisition (total buy amount)", example = "446831.45")
    @JsonProperty("cost_of_acquisition")
    private double costOfAcquisition;
    
    @Schema(description = "Total STCG (profit/loss)", example = "512.41")
    @JsonProperty("total_stcg")
    private double totalStcg;

    @Schema(description = "Indicates if total STCG is positive", example = "true")
    @JsonProperty("is_positive")
    private boolean positive;

    @Schema(description = "Display color hint for total STCG", example = "green")
    @JsonProperty("display_color")
    private String displayColor;
    
    // Constructors
    public StcgResponse() {}
    
    public StcgResponse(double fullValueOfConsideration, double costOfAcquisition, double totalStcg) {
        this.fullValueOfConsideration = fullValueOfConsideration;
        this.costOfAcquisition = costOfAcquisition;
        this.totalStcg = totalStcg;
        this.positive = totalStcg > 0;
        this.displayColor = this.positive ? "green" : "red";
    }
    
    // Getters and Setters
    public double getFullValueOfConsideration() { return fullValueOfConsideration; }
    public void setFullValueOfConsideration(double fullValueOfConsideration) { 
        this.fullValueOfConsideration = fullValueOfConsideration; 
    }
    
    public double getCostOfAcquisition() { return costOfAcquisition; }
    public void setCostOfAcquisition(double costOfAcquisition) { 
        this.costOfAcquisition = costOfAcquisition; 
    }
    
    public double getTotalStcg() { return totalStcg; }
    public void setTotalStcg(double totalStcg) { 
        this.totalStcg = totalStcg; 
        this.positive = totalStcg > 0;
        this.displayColor = this.positive ? "green" : "red";
    }

    public boolean isPositive() { return positive; }
    public void setPositive(boolean positive) { this.positive = positive; }

    public String getDisplayColor() { return displayColor; }
    public void setDisplayColor(String displayColor) { this.displayColor = displayColor; }
}
