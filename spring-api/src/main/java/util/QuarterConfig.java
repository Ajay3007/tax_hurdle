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
	 * Factory method to create default FY 2023-24 config
	 */
	public static QuarterConfig createFY202324() {
		QuarterConfig config = new QuarterConfig("FY 2023-24");
		
		config.addQuarter(new Quarter("Q1", "Apr-Jun", 
			LocalDate.of(2023, 4, 1), 
			LocalDate.of(2023, 6, 15)));
		
		config.addQuarter(new Quarter("Q2", "Jun-Sep", 
			LocalDate.of(2023, 6, 16), 
			LocalDate.of(2023, 9, 15)));
		
		config.addQuarter(new Quarter("Q3", "Sep-Dec", 
			LocalDate.of(2023, 9, 16), 
			LocalDate.of(2023, 12, 15)));
		
		config.addQuarter(new Quarter("Q4", "Dec-Mar", 
			LocalDate.of(2023, 12, 16), 
			LocalDate.of(2024, 3, 15)));
		
		config.addQuarter(new Quarter("Q5", "Mar-Mar", 
			LocalDate.of(2024, 3, 16), 
			LocalDate.of(2024, 3, 31)));
		
		return config;
	}
	
	/**
	 * Factory method to create default FY 2024-25 config
	 */
	public static QuarterConfig createFY202425() {
		QuarterConfig config = new QuarterConfig("FY 2024-25");
		
		config.addQuarter(new Quarter("Q1", "Apr-Jun", 
			LocalDate.of(2024, 4, 1), 
			LocalDate.of(2024, 6, 15)));
		
		config.addQuarter(new Quarter("Q2", "Jun-Sep", 
			LocalDate.of(2024, 6, 16), 
			LocalDate.of(2024, 9, 15)));
		
		config.addQuarter(new Quarter("Q3", "Sep-Dec", 
			LocalDate.of(2024, 9, 16), 
			LocalDate.of(2024, 12, 15)));
		
		config.addQuarter(new Quarter("Q4", "Dec-Mar", 
			LocalDate.of(2024, 12, 16), 
			LocalDate.of(2025, 3, 15)));
		
		config.addQuarter(new Quarter("Q5", "Mar-Mar", 
			LocalDate.of(2025, 3, 16), 
			LocalDate.of(2025, 3, 31)));
		
		return config;
	}
	
	/**
	 * Factory method to create config for any financial year
	 * Dynamically generates quarters based on FY start year
	 */
	public static QuarterConfig createForFinancialYear(String fy) {
		return createForFinancialYear(fy, QuarterScheme.Q5_IT_PORTAL);
	}

	/**
	 * Factory to create config for any FY with selectable quarter scheme.
	 */
	public static QuarterConfig createForFinancialYear(String fy, QuarterScheme scheme) {
		// Try predefined configs first
		if ("FY 2021-22".equals(fy)) return createFY202122(scheme);
		if ("FY 2022-23".equals(fy)) return createFY202223(scheme);
		if ("FY 2023-24".equals(fy)) return createFY202324(scheme);
		if ("FY 2024-25".equals(fy)) return createFY202425(scheme);
		
		// Parse dynamic year (e.g., "FY 2025-26" -> 2025)
		try {
			String[] parts = fy.trim().replace("FY ", "").split("-");
			if (parts.length == 2) {
				int startYear = Integer.parseInt(parts[0]);
				int endYear = Integer.parseInt(parts[1]);
				
				return buildDynamic(fy, startYear, endYear, scheme);
			}
		} catch (Exception e) {
			// Fall back to default
		}
		
		// Default fallback to FY 2024-25
		return createFY202425(scheme);
	}

	private static QuarterConfig buildDynamic(String fy, int startYear, int endYear, QuarterScheme scheme) {
		QuarterConfig config = new QuarterConfig(fy);
		if (scheme == QuarterScheme.Q5_IT_PORTAL) {
			config.addQuarter(new Quarter("Q1", "Apr-Jun", 
				LocalDate.of(startYear, 4, 1), LocalDate.of(startYear, 6, 15)));
			config.addQuarter(new Quarter("Q2", "Jun-Sep", 
				LocalDate.of(startYear, 6, 16), LocalDate.of(startYear, 9, 15)));
			config.addQuarter(new Quarter("Q3", "Sep-Dec", 
				LocalDate.of(startYear, 9, 16), LocalDate.of(startYear, 12, 15)));
			config.addQuarter(new Quarter("Q4", "Dec-Mar", 
				LocalDate.of(startYear, 12, 16), LocalDate.of(endYear, 3, 15)));
			config.addQuarter(new Quarter("Q5", "Mar-Mar", 
				LocalDate.of(endYear, 3, 16), LocalDate.of(endYear, 3, 31)));
		} else {
			// Standard 4 quarters
			config.addQuarter(new Quarter("Q1", "Apr-Jun", 
				LocalDate.of(startYear, 4, 1), LocalDate.of(startYear, 6, 30)));
			config.addQuarter(new Quarter("Q2", "Jul-Sep", 
				LocalDate.of(startYear, 7, 1), LocalDate.of(startYear, 9, 30)));
			config.addQuarter(new Quarter("Q3", "Oct-Dec", 
				LocalDate.of(startYear, 10, 1), LocalDate.of(startYear, 12, 31)));
			config.addQuarter(new Quarter("Q4", "Jan-Mar", 
				LocalDate.of(endYear, 1, 1), LocalDate.of(endYear, 3, 31)));
		}
		return config;
	}

	// Overloads to honor scheme for predefined configs
	public static QuarterConfig createFY202122(QuarterScheme scheme) {
		return buildDynamic("FY 2021-22", 2021, 2022, scheme);
	}
	public static QuarterConfig createFY202223(QuarterScheme scheme) {
		return buildDynamic("FY 2022-23", 2022, 2023, scheme);
	}
	public static QuarterConfig createFY202324(QuarterScheme scheme) {
		return buildDynamic("FY 2023-24", 2023, 2024, scheme);
	}
	public static QuarterConfig createFY202425(QuarterScheme scheme) {
		return buildDynamic("FY 2024-25", 2024, 2025, scheme);
	}
	
	@Override
	public String toString() {
		return "QuarterConfig [financialYear=" + financialYear + ", quarters=" + quarters.size() + "]";
	}
}
