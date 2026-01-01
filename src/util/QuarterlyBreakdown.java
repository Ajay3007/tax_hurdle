/**
 * 
 */
package util;

/**
 * Quarterly breakdown of STCG calculations
 * @author ajay
 *
 */
public class QuarterlyBreakdown {
	private double q1;
	private double q2;
	private double q3;
	private double q4;
	private double q5;
	private double total;
	
	public QuarterlyBreakdown() {
		this.q1 = 0;
		this.q2 = 0;
		this.q3 = 0;
		this.q4 = 0;
		this.q5 = 0;
		this.total = 0;
	}
	
	public QuarterlyBreakdown(double q1, double q2, double q3, double q4, double q5) {
		this.q1 = q1;
		this.q2 = q2;
		this.q3 = q3;
		this.q4 = q4;
		this.q5 = q5;
		this.total = q1 + q2 + q3 + q4 + q5;
	}
	
	public double getQ1() {
		return q1;
	}
	
	public void setQ1(double q1) {
		this.q1 = q1;
		updateTotal();
	}
	
	public double getQ2() {
		return q2;
	}
	
	public void setQ2(double q2) {
		this.q2 = q2;
		updateTotal();
	}
	
	public double getQ3() {
		return q3;
	}
	
	public void setQ3(double q3) {
		this.q3 = q3;
		updateTotal();
	}
	
	public double getQ4() {
		return q4;
	}
	
	public void setQ4(double q4) {
		this.q4 = q4;
		updateTotal();
	}
	
	public double getQ5() {
		return q5;
	}
	
	public void setQ5(double q5) {
		this.q5 = q5;
		updateTotal();
	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}
	
	private void updateTotal() {
		this.total = q1 + q2 + q3 + q4 + q5;
	}
	
	public double getQuarterValue(int quarterNumber) {
		switch (quarterNumber) {
			case 1:
				return q1;
			case 2:
				return q2;
			case 3:
				return q3;
			case 4:
				return q4;
			case 5:
				return q5;
			default:
				return 0;
		}
	}
	
	@Override
	public String toString() {
		return "QuarterlyBreakdown [Q1=" + q1 + ", Q2=" + q2 + ", Q3=" + q3 + ", Q4=" + q4 + ", Q5=" + q5
				+ ", Total=" + total + "]";
	}
}
