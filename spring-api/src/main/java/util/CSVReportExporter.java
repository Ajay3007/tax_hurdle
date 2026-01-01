/**
 * 
 */
package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import logging.HurdleLogger;

/**
 * CSV format report exporter
 * Exports tax calculation results to CSV files
 * @author ajay
 *
 */
public class CSVReportExporter implements ReportExporter {
	private static final String FILE_EXTENSION = ".csv";
	private static final String FORMAT_NAME = "CSV";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public String getFileExtension() {
		return FILE_EXTENSION;
	}
	
	@Override
	public String getFormatName() {
		return FORMAT_NAME;
	}
	
	@Override
	public void exportSummaryReport(File outputFile, TaxCalculationSummary summary) throws IOException {
		HurdleLogger.info("Exporting summary report to: " + outputFile.getAbsolutePath());
		
		try (FileWriter writer = new FileWriter(outputFile)) {
			// Header section
			writer.append("InvestingHurdle - Tax Calculation Summary Report\n");
			writer.append("Generated,").append(DATE_TIME_FORMATTER.format(summary.getGeneratedDate())).append("\n");
			writer.append("Financial Year,").append(summary.getFinancialYear()).append("\n");
			writer.append("\n");
			
			// Transaction counts
			writer.append("Transaction Statistics\n");
			writer.append("Metric,Value\n");
			writer.append("Total Transactions,").append(String.valueOf(summary.getTotalTransactions())).append("\n");
			writer.append("Valid Transactions,").append(String.valueOf(summary.getValidTransactions())).append("\n");
			writer.append("Invalid Transactions,").append(String.valueOf(summary.getInvalidTransactions())).append("\n");
			writer.append("\n");
			
			// STCG Summary
			writer.append("Short-Term Capital Gains (STCG)\n");
			writer.append("Metric,Amount\n");
			writer.append("Total Sell Value,").append(formatAmount(summary.getStcgTotalSellValue())).append("\n");
			writer.append("Total Cost of Acquisition,").append(formatAmount(summary.getStcgTotalCostOfAcquisition())).append("\n");
			writer.append("Total STCG,").append(formatAmount(summary.getStcgTotalProfit())).append("\n");
			writer.append("\n");
			
			// Quarterly breakdown
			writer.append("Quarterly STCG Breakdown\n");
			writer.append("Quarter,Amount\n");
			writer.append("Q1,").append(formatAmount(summary.getStcgQ1())).append("\n");
			writer.append("Q2,").append(formatAmount(summary.getStcgQ2())).append("\n");
			writer.append("Q3,").append(formatAmount(summary.getStcgQ3())).append("\n");
			writer.append("Q4,").append(formatAmount(summary.getStcgQ4())).append("\n");
			writer.append("Q5,").append(formatAmount(summary.getStcgQ5())).append("\n");
			writer.append("\n");
			
			// Speculation Summary
			writer.append("Speculation / Intraday Trading\n");
			writer.append("Metric,Value\n");
			writer.append("Total Turnover,").append(formatAmount(summary.getSpeculationTotalTurnover())).append("\n");
			writer.append("Total Profit/Loss,").append(formatAmount(summary.getSpeculationTotalProfit())).append("\n");
			writer.append("Transaction Count,").append(String.valueOf(summary.getSpeculationTransactionCount())).append("\n");
			
			writer.flush();
			HurdleLogger.info("Summary report exported successfully");
		} catch (IOException e) {
			HurdleLogger.error("Failed to export summary report: " + e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public void exportDetailedTransactions(File outputFile, List<TransactionRecord> transactions) throws IOException {
		HurdleLogger.info("Exporting detailed transactions to: " + outputFile.getAbsolutePath());
		
		try (FileWriter writer = new FileWriter(outputFile)) {
			// Header
			writer.append("InvestingHurdle - Detailed Transaction Report\n\n");
			
			// Column headers
			writer.append("Buy Date,Sell Date,Symbol,Buy Qty,Buy Price,Buy Amount,");
			writer.append("Sell Qty,Sell Price,Sell Amount,Holding Days,");
			writer.append("STCG,Speculation,Transaction Type\n");
			
			// Data rows
			for (TransactionRecord transaction : transactions) {
				writer.append(formatDate(transaction.getBuyDate())).append(",");
				writer.append(formatDate(transaction.getSellDate())).append(",");
				writer.append(transaction.getSymbol()).append(",");
				writer.append(formatNumber(transaction.getBuyQuantity())).append(",");
				writer.append(formatAmount(transaction.getBuyPrice())).append(",");
				writer.append(formatAmount(transaction.getBuyAmount())).append(",");
				writer.append(formatNumber(transaction.getSellQuantity())).append(",");
				writer.append(formatAmount(transaction.getSellPrice())).append(",");
				writer.append(formatAmount(transaction.getSellAmount())).append(",");
				writer.append(String.valueOf(transaction.getHoldingDays())).append(",");
				writer.append(formatAmount(transaction.getStcg())).append(",");
				writer.append(formatAmount(transaction.getSpeculation())).append(",");
				writer.append(transaction.getTransactionType()).append("\n");
			}
			
			writer.flush();
			HurdleLogger.info("Detailed transactions exported successfully: " + transactions.size() + " records");
		} catch (IOException e) {
			HurdleLogger.error("Failed to export detailed transactions: " + e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public void exportQuarterlyReport(File outputFile, QuarterlyBreakdown breakdown) throws IOException {
		HurdleLogger.info("Exporting quarterly report to: " + outputFile.getAbsolutePath());
		
		try (FileWriter writer = new FileWriter(outputFile)) {
			// Header
			writer.append("InvestingHurdle - Quarterly STCG Breakdown\n");
			writer.append("Financial Year 2021-22\n\n");
			
			// Quarterly data
			writer.append("Quarter,STCG Amount\n");
			writer.append("Q1 (Apr-Jun),").append(formatAmount(breakdown.getQ1())).append("\n");
			writer.append("Q2 (Jun-Sep),").append(formatAmount(breakdown.getQ2())).append("\n");
			writer.append("Q3 (Sep-Dec),").append(formatAmount(breakdown.getQ3())).append("\n");
			writer.append("Q4 (Dec-Mar),").append(formatAmount(breakdown.getQ4())).append("\n");
			writer.append("Q5 (Mar-Mar),").append(formatAmount(breakdown.getQ5())).append("\n");
			writer.append("\n");
			writer.append("Total STCG,").append(formatAmount(breakdown.getTotal())).append("\n");
			
			writer.flush();
			HurdleLogger.info("Quarterly report exported successfully");
		} catch (IOException e) {
			HurdleLogger.error("Failed to export quarterly report: " + e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * Format amount with 2 decimal places
	 */
	private String formatAmount(double amount) {
		return String.format("%.2f", amount);
	}
	
	/**
	 * Format number with 4 decimal places for quantities
	 */
	private String formatNumber(double number) {
		return String.format("%.4f", number);
	}
	
	/**
	 * Format date as yyyy-MM-dd
	 */
	private String formatDate(java.time.LocalDate date) {
		return date != null ? DATE_FORMATTER.format(date) : "";
	}
	
	@Override
	public String toString() {
		return "CSVReportExporter [formatName=" + FORMAT_NAME + ", fileExtension=" + FILE_EXTENSION + "]";
	}
}
