package com.investinghurdle.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for Speculation (Intraday) calculations
 */
@Schema(description = "Speculation/Intraday trading calculation results")
public class SpeculationResponse {
    
    @Schema(description = "Full value of consideration (total sell amount)", example = "12447914.65")
    @JsonProperty("full_value_of_consideration")
    private double fullValueOfConsideration;
    
    @Schema(description = "Cost of acquisition (total buy amount)", example = "12464533.25")
    @JsonProperty("cost_of_acquisition")
    private double costOfAcquisition;
    
    @Schema(description = "Profit or Loss", example = "-16618.60")
    @JsonProperty("profit_loss")
    private double profitLoss;
    
    @Schema(description = "Total intraday turnover", example = "39280.20")
    @JsonProperty("total_turnover")
    private double totalTurnover;

    @Schema(description = "Indicates if profit/loss is positive", example = "false")
    @JsonProperty("is_positive")
    private boolean positive;

    @Schema(description = "Display color hint for profit/loss", example = "red")
    @JsonProperty("display_color")
    private String displayColor;
    
    // Constructors
    public SpeculationResponse() {}
    
    public SpeculationResponse(double fullValueOfConsideration, double costOfAcquisition, 
                              double profitLoss, double totalTurnover) {
        this.fullValueOfConsideration = fullValueOfConsideration;
        this.costOfAcquisition = costOfAcquisition;
        this.profitLoss = profitLoss;
        this.totalTurnover = totalTurnover;
        this.positive = profitLoss > 0;
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
    
    public double getProfitLoss() { return profitLoss; }
    public void setProfitLoss(double profitLoss) { this.profitLoss = profitLoss; }
    
    public double getTotalTurnover() { return totalTurnover; }
    public void setTotalTurnover(double totalTurnover) { this.totalTurnover = totalTurnover; }

    public boolean isPositive() { return positive; }
    public void setPositive(boolean positive) { this.positive = positive; }

    public String getDisplayColor() { return displayColor; }
    public void setDisplayColor(String displayColor) { this.displayColor = displayColor; }
}
