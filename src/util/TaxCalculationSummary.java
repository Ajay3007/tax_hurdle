/**
 * 
 */
package util;

import java.time.LocalDateTime;

/**
 * Summary of tax calculations for export
 * @author ajay
 *
 */
public class TaxCalculationSummary {
	private String financialYear;
	private LocalDateTime generatedDate;
	private int totalTransactions;
	private int validTransactions;
	private int invalidTransactions;
	
	// STCG Summary
	private double stcgTotalSellValue;
	private double stcgTotalCostOfAcquisition;
	private double stcgTotalProfit;
	private double stcgQ1;
	private double stcgQ2;
	private double stcgQ3;
	private double stcgQ4;
	private double stcgQ5;
	
	// Speculation Summary
	private double speculationTotalTurnover;
	private double speculationTotalProfit;
	private int speculationTransactionCount;
	
	public TaxCalculationSummary(String financialYear) {
		this.financialYear = financialYear;
		this.generatedDate = LocalDateTime.now();
	}
	
	// Getters and Setters
	public String getFinancialYear() {
		return financialYear;
	}
	
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	
	public LocalDateTime getGeneratedDate() {
		return generatedDate;
	}
	
	public void setGeneratedDate(LocalDateTime generatedDate) {
		this.generatedDate = generatedDate;
	}
	
	public int getTotalTransactions() {
		return totalTransactions;
	}
	
	public void setTotalTransactions(int totalTransactions) {
		this.totalTransactions = totalTransactions;
	}
	
	public int getValidTransactions() {
		return validTransactions;
	}
	
	public void setValidTransactions(int validTransactions) {
		this.validTransactions = validTransactions;
	}
	
	public int getInvalidTransactions() {
		return invalidTransactions;
	}
	
	public void setInvalidTransactions(int invalidTransactions) {
		this.invalidTransactions = invalidTransactions;
	}
	
	public double getStcgTotalSellValue() {
		return stcgTotalSellValue;
	}
	
	public void setStcgTotalSellValue(double stcgTotalSellValue) {
		this.stcgTotalSellValue = stcgTotalSellValue;
	}
	
	public double getStcgTotalCostOfAcquisition() {
		return stcgTotalCostOfAcquisition;
	}
	
	public void setStcgTotalCostOfAcquisition(double stcgTotalCostOfAcquisition) {
		this.stcgTotalCostOfAcquisition = stcgTotalCostOfAcquisition;
	}
	
	public double getStcgTotalProfit() {
		return stcgTotalProfit;
	}
	
	public void setStcgTotalProfit(double stcgTotalProfit) {
		this.stcgTotalProfit = stcgTotalProfit;
	}
	
	public double getStcgQ1() {
		return stcgQ1;
	}
	
	public void setStcgQ1(double stcgQ1) {
		this.stcgQ1 = stcgQ1;
	}
	
	public double getStcgQ2() {
		return stcgQ2;
	}
	
	public void setStcgQ2(double stcgQ2) {
		this.stcgQ2 = stcgQ2;
	}
	
	public double getStcgQ3() {
		return stcgQ3;
	}
	
	public void setStcgQ3(double stcgQ3) {
		this.stcgQ3 = stcgQ3;
	}
	
	public double getStcgQ4() {
		return stcgQ4;
	}
	
	public void setStcgQ4(double stcgQ4) {
		this.stcgQ4 = stcgQ4;
	}
	
	public double getStcgQ5() {
		return stcgQ5;
	}
	
	public void setStcgQ5(double stcgQ5) {
		this.stcgQ5 = stcgQ5;
	}
	
	public double getSpeculationTotalTurnover() {
		return speculationTotalTurnover;
	}
	
	public void setSpeculationTotalTurnover(double speculationTotalTurnover) {
		this.speculationTotalTurnover = speculationTotalTurnover;
	}
	
	public double getSpeculationTotalProfit() {
		return speculationTotalProfit;
	}
	
	public void setSpeculationTotalProfit(double speculationTotalProfit) {
		this.speculationTotalProfit = speculationTotalProfit;
	}
	
	public int getSpeculationTransactionCount() {
		return speculationTransactionCount;
	}
	
	public void setSpeculationTransactionCount(int speculationTransactionCount) {
		this.speculationTransactionCount = speculationTransactionCount;
	}
}
