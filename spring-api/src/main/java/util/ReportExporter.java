/**
 * 
 */
package util;

import java.io.File;
import java.util.List;

/**
 * Interface for tax report exporters
 * Defines methods for exporting tax calculation results in various formats
 * @author ajay
 *
 */
public interface ReportExporter {
	
	/**
	 * Export summary report containing totals and quarterly breakdowns
	 */
	void exportSummaryReport(File outputFile, TaxCalculationSummary summary) throws Exception;
	
	/**
	 * Export detailed transaction report
	 */
	void exportDetailedTransactions(File outputFile, List<TransactionRecord> transactions) throws Exception;
	
	/**
	 * Export quarterly breakdown report
	 */
	void exportQuarterlyReport(File outputFile, QuarterlyBreakdown breakdown) throws Exception;
	
	/**
	 * Get file extension for this report format
	 */
	String getFileExtension();
	
	/**
	 * Get display name for this report format
	 */
	String getFormatName();
}
