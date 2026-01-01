/**
 * 
 */
package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logging.HurdleLogger;

/**
 * JSON format report exporter
 * Exports tax calculation results to JSON files
 * Uses simple JSON building without external library dependencies
 * @author ajay
 *
 */
public class JSONReportExporter implements ReportExporter {
	private static final String FILE_EXTENSION = ".json";
	private static final String FORMAT_NAME = "JSON";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
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
			StringBuilder json = new StringBuilder();
			json.append("{\n");
			
			// Report metadata
			json.append("  \"reportMetadata\": {\n");
			json.append("    \"reportType\": \"Summary Report\",\n");
			json.append("    \"generatedDate\": \"").append(DATE_TIME_FORMATTER.format(summary.getGeneratedDate())).append("\",\n");
			json.append("    \"financialYear\": \"").append(summary.getFinancialYear()).append("\",\n");
			json.append("    \"totalTransactions\": ").append(summary.getTotalTransactions()).append(",\n");
			json.append("    \"validTransactions\": ").append(summary.getValidTransactions()).append(",\n");
			json.append("    \"invalidTransactions\": ").append(summary.getInvalidTransactions()).append("\n");
			json.append("  },\n");
			
			// STCG Summary
			json.append("  \"stcgSummary\": {\n");
			json.append("    \"totalSellValue\": ").append(summary.getStcgTotalSellValue()).append(",\n");
			json.append("    \"totalCostOfAcquisition\": ").append(summary.getStcgTotalCostOfAcquisition()).append(",\n");
			json.append("    \"totalSTCG\": ").append(summary.getStcgTotalProfit()).append(",\n");
			json.append("    \"quarterlyBreakdown\": {\n");
			json.append("      \"Q1\": ").append(summary.getStcgQ1()).append(",\n");
			json.append("      \"Q2\": ").append(summary.getStcgQ2()).append(",\n");
			json.append("      \"Q3\": ").append(summary.getStcgQ3()).append(",\n");
			json.append("      \"Q4\": ").append(summary.getStcgQ4()).append(",\n");
			json.append("      \"Q5\": ").append(summary.getStcgQ5()).append("\n");
			json.append("    }\n");
			json.append("  },\n");
			
			// Speculation Summary
			json.append("  \"speculationSummary\": {\n");
			json.append("    \"totalTurnover\": ").append(summary.getSpeculationTotalTurnover()).append(",\n");
			json.append("    \"totalProfit\": ").append(summary.getSpeculationTotalProfit()).append(",\n");
			json.append("    \"transactionCount\": ").append(summary.getSpeculationTransactionCount()).append("\n");
			json.append("  }\n");
			
			json.append("}\n");
			
			writer.write(json.toString());
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
			StringBuilder json = new StringBuilder();
			json.append("{\n");
			json.append("  \"reportMetadata\": {\n");
			json.append("    \"reportType\": \"Detailed Transactions\",\n");
			json.append("    \"generatedDate\": \"").append(DATE_TIME_FORMATTER.format(LocalDateTime.now())).append("\",\n");
			json.append("    \"totalRecords\": ").append(transactions.size()).append("\n");
			json.append("  },\n");
			
			json.append("  \"transactions\": [\n");
			
			for (int i = 0; i < transactions.size(); i++) {
				TransactionRecord t = transactions.get(i);
				json.append("    {\n");
				json.append("      \"buyDate\": \"").append(formatDate(t.getBuyDate())).append("\",\n");
				json.append("      \"sellDate\": \"").append(formatDate(t.getSellDate())).append("\",\n");
				json.append("      \"symbol\": \"").append(escapeJson(t.getSymbol())).append("\",\n");
				json.append("      \"buyQuantity\": ").append(t.getBuyQuantity()).append(",\n");
				json.append("      \"buyPrice\": ").append(t.getBuyPrice()).append(",\n");
				json.append("      \"buyAmount\": ").append(t.getBuyAmount()).append(",\n");
				json.append("      \"sellQuantity\": ").append(t.getSellQuantity()).append(",\n");
				json.append("      \"sellPrice\": ").append(t.getSellPrice()).append(",\n");
				json.append("      \"sellAmount\": ").append(t.getSellAmount()).append(",\n");
				json.append("      \"holdingDays\": ").append(t.getHoldingDays()).append(",\n");
				json.append("      \"stcg\": ").append(t.getStcg()).append(",\n");
				json.append("      \"speculation\": ").append(t.getSpeculation()).append(",\n");
				json.append("      \"transactionType\": \"").append(t.getTransactionType()).append("\"\n");
				json.append("    }");
				
				if (i < transactions.size() - 1) {
					json.append(",");
				}
				json.append("\n");
			}
			
			json.append("  ]\n");
			json.append("}\n");
			
			writer.write(json.toString());
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
			StringBuilder json = new StringBuilder();
			json.append("{\n");
			json.append("  \"reportMetadata\": {\n");
			json.append("    \"reportType\": \"Quarterly Breakdown\",\n");
			json.append("    \"generatedDate\": \"").append(DATE_TIME_FORMATTER.format(LocalDateTime.now())).append("\",\n");
			json.append("    \"financialYear\": \"FY 2021-22\"\n");
			json.append("  },\n");
			
			json.append("  \"quarterlyBreakdown\": {\n");
			json.append("    \"Q1\": ").append(breakdown.getQ1()).append(",\n");
			json.append("    \"Q2\": ").append(breakdown.getQ2()).append(",\n");
			json.append("    \"Q3\": ").append(breakdown.getQ3()).append(",\n");
			json.append("    \"Q4\": ").append(breakdown.getQ4()).append(",\n");
			json.append("    \"Q5\": ").append(breakdown.getQ5()).append(",\n");
			json.append("    \"total\": ").append(breakdown.getTotal()).append("\n");
			json.append("  }\n");
			json.append("}\n");
			
			writer.write(json.toString());
			writer.flush();
			HurdleLogger.info("Quarterly report exported successfully");
		} catch (IOException e) {
			HurdleLogger.error("Failed to export quarterly report: " + e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * Format date as yyyy-MM-dd
	 */
	private String formatDate(java.time.LocalDate date) {
		return date != null ? DATE_FORMATTER.format(date) : "";
	}
	
	/**
	 * Escape special characters in JSON strings
	 */
	private String escapeJson(String input) {
		if (input == null) {
			return "";
		}
		return input.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\b", "\\b")
			.replace("\f", "\\f")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}
	
	@Override
	public String toString() {
		return "JSONReportExporter [formatName=" + FORMAT_NAME + ", fileExtension=" + FILE_EXTENSION + "]";
	}
}
