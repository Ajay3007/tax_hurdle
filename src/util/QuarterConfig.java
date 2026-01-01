/**
 * 
 */
package util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logging.HurdleLogger;

/**
 * Configuration manager for financial year quarters
 * Maintains quarter definitions and provides methods to find quarters for given dates
 * @author ajay
 *
 */
public class QuarterConfig {
	private String financialYear;
	private List<Quarter> quarters;
	
	public QuarterConfig(String financialYear) {
		this.financialYear = financialYear;
		this.quarters = new ArrayList<>();
	}
	
	public void addQuarter(Quarter quarter) {
		quarters.add(quarter);
		HurdleLogger.debug("Added quarter: " + quarter);
	}
	
	public String getFinancialYear() {
		return financialYear;
	}
	
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	
	public List<Quarter> getQuarters() {
		return new ArrayList<>(quarters);
	}
	
	/**
	 * Get quarter number (1-5) for a given date, or 0 if not found
	 */
	public int getQuarterNumber(LocalDate date) {
		if (date == null) {
			return 0;
		}
		for (int i = 0; i < quarters.size(); i++) {
			if (quarters.get(i).contains(date)) {
				return i + 1;
			}
		}
		return 0;
	}
	
	/**
	 * Get quarter object for a given date
	 */
	public Quarter getQuarterForDate(LocalDate date) {
		if (date == null) {
			return null;
		}
		for (Quarter q : quarters) {
			if (q.contains(date)) {
				return q;
			}
		}
		return null;
	}
	
	/**
	 * Get quarter by code (Q1, Q2, etc.)
	 */
	public Quarter getQuarterByCode(String code) {
		for (Quarter q : quarters) {
			if (q.getCode().equalsIgnoreCase(code)) {
				return q;
			}
		}
		return null;
	}
	
	public int getQuarterCount() {
		return quarters.size();
	}
	
	/**
	 * Factory method to create default FY 2021-22 config
	 */
	public static QuarterConfig createFY202122() {
		QuarterConfig config = new QuarterConfig("FY 2021-22");
		
		config.addQuarter(new Quarter("Q1", "Apr-Jun", 
			LocalDate.of(2021, 4, 1), 
			LocalDate.of(2021, 6, 15)));
		
		config.addQuarter(new Quarter("Q2", "Jun-Sep", 
			LocalDate.of(2021, 6, 16), 
			LocalDate.of(2021, 9, 15)));
		
		config.addQuarter(new Quarter("Q3", "Sep-Dec", 
			LocalDate.of(2021, 9, 16), 
			LocalDate.of(2021, 12, 15)));
		
		config.addQuarter(new Quarter("Q4", "Dec-Mar", 
			LocalDate.of(2021, 12, 16), 
			LocalDate.of(2022, 3, 15)));
		
		config.addQuarter(new Quarter("Q5", "Mar-Mar", 
			LocalDate.of(2022, 3, 16), 
			LocalDate.of(2022, 3, 31)));
		
		return config;
	}
	
	/**
	 * Factory method to create default FY 2022-23 config
	 */
	public static QuarterConfig createFY202223() {
		QuarterConfig config = new QuarterConfig("FY 2022-23");
		
		config.addQuarter(new Quarter("Q1", "Apr-Jun", 
			LocalDate.of(2022, 4, 1), 
			LocalDate.of(2022, 6, 15)));
		
		config.addQuarter(new Quarter("Q2", "Jun-Sep", 
			LocalDate.of(2022, 6, 16), 
			LocalDate.of(2022, 9, 15)));
		
		config.addQuarter(new Quarter("Q3", "Sep-Dec", 
			LocalDate.of(2022, 9, 16), 
			LocalDate.of(2022, 12, 15)));
		
		config.addQuarter(new Quarter("Q4", "Dec-Mar", 
			LocalDate.of(2022, 12, 16), 
			LocalDate.of(2023, 3, 15)));
		
		config.addQuarter(new Quarter("Q5", "Mar-Mar", 
			LocalDate.of(2023, 3, 16), 
			LocalDate.of(2023, 3, 31)));
		
		return config;
	}
	
	/**
	 * Factory method to create custom config
	 */
	public static QuarterConfig createCustom(String fy, List<Quarter> quarterList) {
		QuarterConfig config = new QuarterConfig(fy);
		for (Quarter q : quarterList) {
			config.addQuarter(q);
		}
		return config;
	}
	
	@Override
	public String toString() {
		return "QuarterConfig [financialYear=" + financialYear + ", quarters=" + quarters.size() + "]";
	}
}
