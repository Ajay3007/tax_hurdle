/**
 * 
 */
package security;

/**
 * @author ajay
 *
 */
public class Security {
	String company, date;
	boolean buy;
	int quantity;
	double buyPrice, sellPrice, ltp;
	double totalBuyValue, totalSellValue;
	double buyAverage, sellAverage;
	double PL;
	
	public Security() {
		
	}

	public Security(String company, boolean buy, double price, int quantity, String date, double ltp) {
		super();
		this.company = company;
		this.date = date;
		this.buy = buy;
		this.quantity = quantity;
		this.ltp = ltp;
		if(buy) {
			this.buyPrice = price;
		} else {
			this.sellPrice = price;
		}
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isBuy() {
		return buy;
	}

	public void setBuy(boolean buy) {
		this.buy = buy;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public double getLtp() {
		return ltp;
	}

	public void setLtp(double ltp) {
		this.ltp = ltp;
	}

	public double getTotalBuyValue() {
		return totalBuyValue;
	}

	public void setTotalBuyValue(double totalBuyValue) {
		this.totalBuyValue = totalBuyValue;
	}

	public double getTotalSellValue() {
		return totalSellValue;
	}

	public void setTotalSellValue(double totalSellValue) {
		this.totalSellValue = totalSellValue;
	}

	public double getBuyAverage() {
		return buyAverage;
	}

	public void setBuyAverage(double buyAverage) {
		this.buyAverage = buyAverage;
	}

	public double getSellAverage() {
		return sellAverage;
	}

	public void setSellAverage(double sellAverage) {
		this.sellAverage = sellAverage;
	}

	public double getPL() {
		return PL;
	}

	public void setPL(double pL) {
		PL = pL;
	}
	
	@Override
	public String toString() {
		if(buy) {
			return "[Company : " + company + " Quantity : " + quantity + " Date : " + date + " Buy Price : " + buyPrice +
					" LTP : " + ltp + " Total Buy Value : " + totalBuyValue + " Buy Average : " + buyAverage + "]";
		} else
			return "[Company : " + company + " Quantity : " + quantity + " Date : " + date + " Sell Price : " + sellPrice +
					" LTP : " + ltp + " Total Sell Value : " + totalSellValue + " Sell Average : " + sellAverage + "]";
	}
	
	
}
