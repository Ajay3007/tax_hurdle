/**
 * 
 */
package util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a FIFO cost basis calculation for a sell transaction
 * @author ajay
 *
 */
public class FIFOAllocation {
	private LocalDate sellDate;
	private double sellQuantity;
	private double sellAmount;
	private List<BuyOrderMatch> matches;
	private double totalCostOfAcquisition;
	private double profitOrLoss;
	
	public FIFOAllocation(LocalDate sellDate, double sellQuantity, double sellAmount) {
		this.sellDate = sellDate;
		this.sellQuantity = sellQuantity;
		this.sellAmount = sellAmount;
		this.matches = new ArrayList<>();
		this.totalCostOfAcquisition = 0;
		this.profitOrLoss = 0;
	}
	
	public void addMatch(BuyOrderMatch match) {
		matches.add(match);
		totalCostOfAcquisition += match.getCostOfAcquisition();
	}
	
	public LocalDate getSellDate() {
		return sellDate;
	}
	
	public void setSellDate(LocalDate sellDate) {
		this.sellDate = sellDate;
	}
	
	public double getSellQuantity() {
		return sellQuantity;
	}
	
	public void setSellQuantity(double sellQuantity) {
		this.sellQuantity = sellQuantity;
	}
	
	public double getSellAmount() {
		return sellAmount;
	}
	
	public void setSellAmount(double sellAmount) {
		this.sellAmount = sellAmount;
	}
	
	public List<BuyOrderMatch> getMatches() {
		return matches;
	}
	
	public double getTotalCostOfAcquisition() {
		return totalCostOfAcquisition;
	}
	
	public void setTotalCostOfAcquisition(double totalCostOfAcquisition) {
		this.totalCostOfAcquisition = totalCostOfAcquisition;
		calculateProfitOrLoss();
	}
	
	public double getProfitOrLoss() {
		return profitOrLoss;
	}
	
	private void calculateProfitOrLoss() {
		this.profitOrLoss = sellAmount - totalCostOfAcquisition;
	}
	
	public int getNumberOfBuysMatched() {
		return matches.size();
	}
	
	@Override
	public String toString() {
		return "FIFOAllocation [sellDate=" + sellDate + ", sellQuantity=" + sellQuantity + ", sellAmount="
				+ sellAmount + ", matches=" + matches.size() + ", totalCostOfAcquisition=" + totalCostOfAcquisition
				+ ", profitOrLoss=" + profitOrLoss + "]";
	}
}
