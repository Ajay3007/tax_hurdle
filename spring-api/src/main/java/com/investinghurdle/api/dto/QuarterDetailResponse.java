package com.investinghurdle.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for quarterly breakdown details
 */
@Schema(description = "Quarterly breakdown with date ranges")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuarterDetailResponse {
    
    @Schema(description = "Quarter number", example = "1")
    @JsonProperty("quarter_number")
    private int quarterNumber;
    
    @Schema(description = "Quarter code", example = "Q1")
    @JsonProperty("quarter_code")
    private String quarterCode;
    
    @Schema(description = "Quarter name/period", example = "Apr-Jun")
    @JsonProperty("quarter_name")
    private String quarterName;
    
    @Schema(description = "Start date of quarter", example = "2021-04-01")
    @JsonProperty("start_date")
    private String startDate;
    
    @Schema(description = "End date of quarter", example = "2021-06-15")
    @JsonProperty("end_date")
    private String endDate;
    
    @Schema(description = "STCG amount for this quarter", example = "1644.20")
    @JsonProperty("stcg_amount")
    private Double stcgAmount;

    @Schema(description = "LTCG amount for this quarter", example = "25000.00")
    @JsonProperty("ltcg_amount")
    private Double ltcgAmount;

    @Schema(description = "Speculation (intraday) profit/loss for this quarter", example = "-1250.00")
    @JsonProperty("speculation_amount")
    private Double speculationAmount;

    @Schema(description = "Speculation turnover for this quarter", example = "104188.72")
    @JsonProperty("speculation_turnover")
    private Double speculationTurnover;

    @Schema(description = "Full value of consideration (total sell) for this quarter", example = "250000.00")
    @JsonProperty("full_value_of_consideration")
    private Double fullValueOfConsideration;

    @Schema(description = "Cost of acquisition (total buy) for this quarter", example = "240000.00")
    @JsonProperty("cost_of_acquisition")
    private Double costOfAcquisition;

    @Schema(description = "Indicates if the reported amount is positive", example = "true")
    @JsonProperty("is_positive")
    private Boolean positive;

    @Schema(description = "Display color hint based on sign", example = "green")
    @JsonProperty("display_color")
    private String displayColor;
    
    // Constructors
    public QuarterDetailResponse() {}

    private QuarterDetailResponse(int quarterNumber, String quarterCode, String quarterName,
                                  String startDate, String endDate) {
        this.quarterNumber = quarterNumber;
        this.quarterCode = quarterCode;
        this.quarterName = quarterName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static QuarterDetailResponse forStcg(int quarterNumber, String quarterCode, String quarterName,
                                                String startDate, String endDate, double stcgAmount,
                                                double totalSell, double totalBuy) {
        QuarterDetailResponse response = new QuarterDetailResponse(quarterNumber, quarterCode, quarterName, startDate, endDate);
        response.stcgAmount = stcgAmount;
        response.fullValueOfConsideration = totalSell;
        response.costOfAcquisition = totalBuy;
        response.positive = stcgAmount > 0;
        response.displayColor = response.positive ? "green" : "red";
        return response;
    }

    public static QuarterDetailResponse forLtcg(int quarterNumber, String quarterCode, String quarterName,
                                                String startDate, String endDate, double ltcgAmount,
                                                double totalSell, double totalBuy) {
        QuarterDetailResponse response = new QuarterDetailResponse(quarterNumber, quarterCode, quarterName, startDate, endDate);
        response.ltcgAmount = ltcgAmount;
        response.fullValueOfConsideration = totalSell;
        response.costOfAcquisition = totalBuy;
        response.positive = ltcgAmount > 0;
        response.displayColor = response.positive ? "green" : "red";
        return response;
    }

    public static QuarterDetailResponse forSpeculation(int quarterNumber, String quarterCode, String quarterName,
                                                       String startDate, String endDate, double speculationAmount,
                                                       double totalSell, double totalBuy, double turnover) {
        QuarterDetailResponse response = new QuarterDetailResponse(quarterNumber, quarterCode, quarterName, startDate, endDate);
        response.speculationAmount = speculationAmount;
        response.fullValueOfConsideration = totalSell;
        response.costOfAcquisition = totalBuy;
        response.speculationTurnover = turnover;
        response.positive = speculationAmount > 0;
        response.displayColor = response.positive ? "green" : "red";
        return response;
    }
    
    // Getters and Setters
    public int getQuarterNumber() { return quarterNumber; }
    public void setQuarterNumber(int quarterNumber) { this.quarterNumber = quarterNumber; }
    
    public String getQuarterCode() { return quarterCode; }
    public void setQuarterCode(String quarterCode) { this.quarterCode = quarterCode; }
    
    public String getQuarterName() { return quarterName; }
    public void setQuarterName(String quarterName) { this.quarterName = quarterName; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public Double getStcgAmount() { return stcgAmount; }
    public void setStcgAmount(Double stcgAmount) { this.stcgAmount = stcgAmount; }

    public Double getLtcgAmount() { return ltcgAmount; }
    public void setLtcgAmount(Double ltcgAmount) { this.ltcgAmount = ltcgAmount; }

    public Double getSpeculationAmount() { return speculationAmount; }
    public void setSpeculationAmount(Double speculationAmount) { this.speculationAmount = speculationAmount; }

    public Double getSpeculationTurnover() { return speculationTurnover; }
    public void setSpeculationTurnover(Double speculationTurnover) { this.speculationTurnover = speculationTurnover; }

    public Double getFullValueOfConsideration() { return fullValueOfConsideration; }
    public void setFullValueOfConsideration(Double fullValueOfConsideration) { this.fullValueOfConsideration = fullValueOfConsideration; }

    public Double getCostOfAcquisition() { return costOfAcquisition; }
    public void setCostOfAcquisition(Double costOfAcquisition) { this.costOfAcquisition = costOfAcquisition; }

    public Boolean getPositive() { return positive; }
    public void setPositive(Boolean positive) { this.positive = positive; }

    public String getDisplayColor() { return displayColor; }
    public void setDisplayColor(String displayColor) { this.displayColor = displayColor; }
}
