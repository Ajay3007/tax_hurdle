/**
 * 
 */
package util;

import java.time.LocalDate;

/**
 * Represents a match between a buy order and a sell order for FIFO tracking
 * @author ajay
 *
 */
public class BuyOrderMatch {
	private LocalDate buyDate;
	private double buyQuantity;
	private double unitCost;
	private double costOfAcquisition;
	private LocalDate sellDate;
	private double holdingDays;
	
	public BuyOrderMatch(LocalDate buyDate, double buyQuantity, double unitCost, LocalDate sellDate) {
		this.buyDate = buyDate;
		this.buyQuantity = buyQuantity;
		this.unitCost = unitCost;
		this.costOfAcquisition = buyQuantity * unitCost;
		this.sellDate = sellDate;
		this.holdingDays = java.time.temporal.ChronoUnit.DAYS.between(buyDate, sellDate);
	}
	
	public LocalDate getBuyDate() {
		return buyDate;
	}
	
	public void setBuyDate(LocalDate buyDate) {
		this.buyDate = buyDate;
	}
	
	public double getBuyQuantity() {
		return buyQuantity;
	}
	
	public void setBuyQuantity(double buyQuantity) {
		this.buyQuantity = buyQuantity;
	}
	
	public double getUnitCost() {
		return unitCost;
	}
	
	public void setUnitCost(double unitCost) {
		this.unitCost = unitCost;
	}
	
	public double getCostOfAcquisition() {
		return costOfAcquisition;
	}
	
	public void setCostOfAcquisition(double costOfAcquisition) {
		this.costOfAcquisition = costOfAcquisition;
	}
	
	public LocalDate getSellDate() {
		return sellDate;
	}
	
	public void setSellDate(LocalDate sellDate) {
		this.sellDate = sellDate;
		if (buyDate != null) {
			this.holdingDays = java.time.temporal.ChronoUnit.DAYS.between(buyDate, sellDate);
		}
	}
	
	public double getHoldingDays() {
		return holdingDays;
	}
	
	public void setHoldingDays(double holdingDays) {
		this.holdingDays = holdingDays;
	}
	
	@Override
	public String toString() {
		return "BuyOrderMatch [buyDate=" + buyDate + ", buyQuantity=" + buyQuantity + ", unitCost=" + unitCost
				+ ", costOfAcquisition=" + costOfAcquisition + ", sellDate=" + sellDate + ", holdingDays="
				+ holdingDays + "]";
	}
}
