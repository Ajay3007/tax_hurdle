package com.investinghurdle.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Complete tax calculation response including STCG, Speculation, and Quarterly breakdown
 */
@Schema(description = "Complete tax calculation results")
public class TaxCalculationResponse {
    
    @Schema(description = "Financial year for the calculation", example = "FY 2021-22")
    @JsonProperty("financial_year")
    private String financialYear;

    @Schema(description = "Detected broker type code", example = "UPSTOX")
    @JsonProperty("broker_type")
    private String brokerType;

    @Schema(description = "Detected broker display name", example = "Upstox")
    @JsonProperty("broker_name")
    private String brokerName;
    
    @Schema(description = "Short-Term Capital Gains results")
    @JsonProperty("stcg")
    private StcgResponse stcg;
    
    @Schema(description = "Long-Term Capital Gains results")
    @JsonProperty("ltcg")
    private LtcgResponse ltcg;
    
    @Schema(description = "Speculation/Intraday trading results")
    @JsonProperty("speculation")
    private SpeculationResponse speculation;
    
    @Schema(description = "STCG quarterly breakdown with date ranges")
    @JsonProperty("stcg_quarterly_breakdown")
    private List<QuarterDetailResponse> stcgQuarterlyBreakdown;

    @Schema(description = "LTCG quarterly breakdown")
    @JsonProperty("ltcg_quarterly_breakdown")
    private List<QuarterDetailResponse> ltcgQuarterlyBreakdown;

    @Schema(description = "Speculation quarterly breakdown")
    @JsonProperty("speculation_quarterly_breakdown")
    private List<QuarterDetailResponse> speculationQuarterlyBreakdown;
    
    @Schema(description = "Calculation timestamp", example = "2025-12-31T14:30:00")
    @JsonProperty("calculated_at")
    private String calculatedAt;
    
    @Schema(description = "Processing time in milliseconds", example = "250")
    @JsonProperty("processing_time_ms")
    private long processingTimeMs;
    
    // Constructors
    public TaxCalculationResponse() {}
    
    public TaxCalculationResponse(String financialYear, String brokerType, String brokerName,
                                 StcgResponse stcg, 
                                 LtcgResponse ltcg,
                                 SpeculationResponse speculation, 
                                 List<QuarterDetailResponse> stcgQuarterlyBreakdown,
                                 List<QuarterDetailResponse> ltcgQuarterlyBreakdown,
                                 List<QuarterDetailResponse> speculationQuarterlyBreakdown,
                                 String calculatedAt, long processingTimeMs) {
        this.financialYear = financialYear;
        this.brokerType = brokerType;
        this.brokerName = brokerName;
        this.stcg = stcg;
        this.ltcg = ltcg;
        this.speculation = speculation;
        this.stcgQuarterlyBreakdown = stcgQuarterlyBreakdown;
        this.ltcgQuarterlyBreakdown = ltcgQuarterlyBreakdown;
        this.speculationQuarterlyBreakdown = speculationQuarterlyBreakdown;
        this.calculatedAt = calculatedAt;
        this.processingTimeMs = processingTimeMs;
    }
    
    // Getters and Setters
    public String getFinancialYear() { return financialYear; }
    public void setFinancialYear(String financialYear) { this.financialYear = financialYear; }

    public String getBrokerType() { return brokerType; }
    public void setBrokerType(String brokerType) { this.brokerType = brokerType; }

    public String getBrokerName() { return brokerName; }
    public void setBrokerName(String brokerName) { this.brokerName = brokerName; }
    
    public StcgResponse getStcg() { return stcg; }
    public void setStcg(StcgResponse stcg) { this.stcg = stcg; }
    
    public LtcgResponse getLtcg() { return ltcg; }
    public void setLtcg(LtcgResponse ltcg) { this.ltcg = ltcg; }
    
    public SpeculationResponse getSpeculation() { return speculation; }
    public void setSpeculation(SpeculationResponse speculation) { this.speculation = speculation; }
    
    public List<QuarterDetailResponse> getLtcgQuarterlyBreakdown() { return ltcgQuarterlyBreakdown; }
    public void setLtcgQuarterlyBreakdown(List<QuarterDetailResponse> ltcgQuarterlyBreakdown) { this.ltcgQuarterlyBreakdown = ltcgQuarterlyBreakdown; }
    
    public List<QuarterDetailResponse> getSpeculationQuarterlyBreakdown() { return speculationQuarterlyBreakdown; }
    public void setSpeculationQuarterlyBreakdown(List<QuarterDetailResponse> speculationQuarterlyBreakdown) { this.speculationQuarterlyBreakdown = speculationQuarterlyBreakdown; }
    
    public List<QuarterDetailResponse> getStcgQuarterlyBreakdown() { return stcgQuarterlyBreakdown; }
    public void setStcgQuarterlyBreakdown(List<QuarterDetailResponse> stcgQuarterlyBreakdown) {
        this.stcgQuarterlyBreakdown = stcgQuarterlyBreakdown;
    }
    
    public String getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(String calculatedAt) { this.calculatedAt = calculatedAt; }
    
    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
}
