/**
 * 
 */
package util;

import java.time.LocalDate;

/**
 * Represents a buy order for tracking FIFO cost basis calculation
 * @author ajay
 *
 */
public class BuyOrder {
	private LocalDate buyDate;
	private double quantity;
	private double totalAmount;
	private double unitCost;
	private double remainingQuantity;
	
	public BuyOrder(LocalDate buyDate, double quantity, double totalAmount) {
		this.buyDate = buyDate;
		this.quantity = quantity;
		this.totalAmount = totalAmount;
		this.unitCost = quantity > 0 ? totalAmount / quantity : 0;
		this.remainingQuantity = quantity;
	}
	
	public LocalDate getBuyDate() {
		return buyDate;
	}
	
	public void setBuyDate(LocalDate buyDate) {
		this.buyDate = buyDate;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public double getTotalAmount() {
		return totalAmount;
	}
	
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
		this.unitCost = quantity > 0 ? totalAmount / quantity : 0;
	}
	
	public double getUnitCost() {
		return unitCost;
	}
	
	public double getRemainingQuantity() {
		return remainingQuantity;
	}
	
	public void setRemainingQuantity(double remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}
	
	public double allocateQuantity(double qtyToAllocate) {
		if (qtyToAllocate > remainingQuantity) {
			qtyToAllocate = remainingQuantity;
		}
		remainingQuantity -= qtyToAllocate;
		return qtyToAllocate;
	}
	
	public double getCostForQuantity(double qty) {
		return qty * unitCost;
	}
	
	@Override
	public String toString() {
		return "BuyOrder [buyDate=" + buyDate + ", quantity=" + quantity + ", totalAmount=" + totalAmount
				+ ", unitCost=" + unitCost + ", remainingQuantity=" + remainingQuantity + "]";
	}
}
