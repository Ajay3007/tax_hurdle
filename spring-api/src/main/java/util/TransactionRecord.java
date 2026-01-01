/**
 * 
 */
package util;

import java.time.LocalDate;

/**
 * Represents a single transaction record for reporting
 * @author ajay
 *
 */
public class TransactionRecord {
	private LocalDate buyDate;
	private LocalDate sellDate;
	private String symbol;
	private double buyQuantity;
	private double buyPrice;
	private double buyAmount;
	private double sellQuantity;
	private double sellPrice;
	private double sellAmount;
	private int holdingDays;
	private double stcg;
	private double speculation;
	private String transactionType; // "STCG" or "Speculation"
	
	public TransactionRecord() {
	}
	
	public TransactionRecord(LocalDate buyDate, LocalDate sellDate, String symbol, double quantity,
			double buyPrice, double buyAmount, double sellPrice, double sellAmount, int holdingDays,
			double profit, String transactionType) {
		this.buyDate = buyDate;
		this.sellDate = sellDate;
		this.symbol = symbol;
		this.buyQuantity = quantity;
		this.buyPrice = buyPrice;
		this.buyAmount = buyAmount;
		this.sellQuantity = quantity;
		this.sellPrice = sellPrice;
		this.sellAmount = sellAmount;
		this.holdingDays = holdingDays;
		this.transactionType = transactionType;
		
		if ("STCG".equalsIgnoreCase(transactionType)) {
			this.stcg = profit;
			this.speculation = 0;
		} else {
			this.stcg = 0;
			this.speculation = profit;
		}
	}
	
	// Getters and Setters
	public LocalDate getBuyDate() {
		return buyDate;
	}
	
	public void setBuyDate(LocalDate buyDate) {
		this.buyDate = buyDate;
	}
	
	public LocalDate getSellDate() {
		return sellDate;
	}
	
	public void setSellDate(LocalDate sellDate) {
		this.sellDate = sellDate;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public double getBuyQuantity() {
		return buyQuantity;
	}
	
	public void setBuyQuantity(double buyQuantity) {
		this.buyQuantity = buyQuantity;
	}
	
	public double getBuyPrice() {
		return buyPrice;
	}
	
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	public double getBuyAmount() {
		return buyAmount;
	}
	
	public void setBuyAmount(double buyAmount) {
		this.buyAmount = buyAmount;
	}
	
	public double getSellQuantity() {
		return sellQuantity;
	}
	
	public void setSellQuantity(double sellQuantity) {
		this.sellQuantity = sellQuantity;
	}
	
	public double getSellPrice() {
		return sellPrice;
	}
	
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
	
	public double getSellAmount() {
		return sellAmount;
	}
	
	public void setSellAmount(double sellAmount) {
		this.sellAmount = sellAmount;
	}
	
	public int getHoldingDays() {
		return holdingDays;
	}
	
	public void setHoldingDays(int holdingDays) {
		this.holdingDays = holdingDays;
	}
	
	public double getStcg() {
		return stcg;
	}
	
	public void setStcg(double stcg) {
		this.stcg = stcg;
	}
	
	public double getSpeculation() {
		return speculation;
	}
	
	public void setSpeculation(double speculation) {
		this.speculation = speculation;
	}
	
	public String getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public double getProfit() {
		return stcg + speculation;
	}
	
	@Override
	public String toString() {
		return "TransactionRecord [buyDate=" + buyDate + ", sellDate=" + sellDate + ", symbol=" + symbol
				+ ", buyQuantity=" + buyQuantity + ", buyPrice=" + buyPrice + ", sellPrice=" + sellPrice
				+ ", holdingDays=" + holdingDays + ", profit=" + getProfit() + "]";
	}
}
