/**
 * 
 */
package util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import logging.HurdleLogger;

/**
 * Implements FIFO (First-In-First-Out) cost basis calculation for equity trading
 * Tracks buy orders chronologically and matches them against sell orders
 * @author ajay
 *
 */
public class FIFOCalculator {
	private Queue<BuyOrder> pendingBuys;
	private List<FIFOAllocation> completedAllocations;
	private String symbol;
	
	public FIFOCalculator(String symbol) {
		this.symbol = symbol;
		this.pendingBuys = new LinkedList<>();
		this.completedAllocations = new ArrayList<>();
	}
	
	/**
	 * Add a buy order to the pending queue
	 */
	public void addBuyOrder(LocalDate buyDate, double quantity, double totalAmount) {
		if (quantity <= 0) {
			HurdleLogger.warn("Skipping buy order with invalid quantity: " + quantity);
			return;
		}
		BuyOrder order = new BuyOrder(buyDate, quantity, totalAmount);
		pendingBuys.offer(order);
		HurdleLogger.debug("Added buy order: " + order);
	}
	
	/**
	 * Calculate cost basis for a sell order using FIFO method
	 * Matches sell quantity against pending buy orders in chronological order
	 */
	public FIFOAllocation calculateCostBasis(LocalDate sellDate, double sellQuantity, double sellAmount) {
		if (sellQuantity <= 0) {
			HurdleLogger.warn("Skipping sell order with invalid quantity: " + sellQuantity);
			return new FIFOAllocation(sellDate, sellQuantity, sellAmount);
		}
		
		FIFOAllocation allocation = new FIFOAllocation(sellDate, sellQuantity, sellAmount);
		double remainingSellQty = sellQuantity;
		
		while (remainingSellQty > 0 && !pendingBuys.isEmpty()) {
			BuyOrder currentBuy = pendingBuys.peek();
			
			if (currentBuy.getRemainingQuantity() <= 0) {
				pendingBuys.poll();
				continue;
			}
			
			// Allocate quantity from current buy order
			double allocatedQty = currentBuy.allocateQuantity(remainingSellQty);
			BuyOrderMatch match = new BuyOrderMatch(
				currentBuy.getBuyDate(),
				allocatedQty,
				currentBuy.getUnitCost(),
				sellDate
			);
			
			allocation.addMatch(match);
			remainingSellQty -= allocatedQty;
			
			// Remove buy order if fully consumed
			if (currentBuy.getRemainingQuantity() <= 0) {
				pendingBuys.poll();
			}
		}
		
		if (remainingSellQty > 0) {
			HurdleLogger.warn("Symbol: " + symbol + " - Unable to match all sell quantity. " +
				"Unmatched qty: " + remainingSellQty + " out of " + sellQuantity);
		}
		
		allocation.setTotalCostOfAcquisition(allocation.getTotalCostOfAcquisition());
		completedAllocations.add(allocation);
		
		return allocation;
	}
	
	/**
	 * Get all pending buy orders
	 */
	public List<BuyOrder> getPendingBuyOrders() {
		return new ArrayList<>(pendingBuys);
	}
	
	/**
	 * Get all completed allocations
	 */
	public List<FIFOAllocation> getCompletedAllocations() {
		return new ArrayList<>(completedAllocations);
	}
	
	/**
	 * Get total cost of pending buys
	 */
	public double getTotalPendingBuysCost() {
		return pendingBuys.stream()
			.mapToDouble(buy -> buy.getRemainingQuantity() * buy.getUnitCost())
			.sum();
	}
	
	/**
	 * Get total pending buy quantity
	 */
	public double getTotalPendingBuysQuantity() {
		return pendingBuys.stream()
			.mapToDouble(BuyOrder::getRemainingQuantity)
			.sum();
	}
	
	/**
	 * Get number of pending buy orders
	 */
	public int getPendingBuyOrderCount() {
		return pendingBuys.size();
	}
	
	/**
	 * Clear all buy orders and allocations
	 */
	public void reset() {
		pendingBuys.clear();
		completedAllocations.clear();
		HurdleLogger.debug("FIFO Calculator reset for symbol: " + symbol);
	}
	
	/**
	 * Get summary statistics
	 */
	public String getSummary() {
		double totalBuyCost = completedAllocations.stream()
			.mapToDouble(FIFOAllocation::getTotalCostOfAcquisition)
			.sum();
		double totalSellAmount = completedAllocations.stream()
			.mapToDouble(FIFOAllocation::getSellAmount)
			.sum();
		double totalProfit = totalSellAmount - totalBuyCost;
		
		return "FIFO Summary [Symbol: " + symbol + "]" +
			"\n  Total Allocations: " + completedAllocations.size() +
			"\n  Total Buy Cost: " + totalBuyCost +
			"\n  Total Sell Amount: " + totalSellAmount +
			"\n  Total Profit/Loss: " + totalProfit +
			"\n  Pending Buys: " + getPendingBuyOrderCount() +
			"\n  Pending Buy Qty: " + getTotalPendingBuysQuantity();
	}
	
	@Override
	public String toString() {
		return "FIFOCalculator [symbol=" + symbol + ", pendingBuys=" + pendingBuys.size() + ", completedAllocations="
				+ completedAllocations.size() + "]";
	}
}
