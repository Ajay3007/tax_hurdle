/**
 * 
 */
package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import logging.HurdleLogger;

/**
 * Data validation utility for Excel data processing
 * Provides validation methods for dates, amounts, holding days, and other transaction data
 * @author ajay
 *
 */
public class DataValidator {
	
	public enum ValidationError {
		INVALID_DATE("Invalid date format or out of range"),
		INVALID_AMOUNT("Amount must be a positive number"),
		INVALID_HOLDING_DAYS("Holding days must be a non-negative integer"),
		INVALID_QUANTITY("Quantity must be a positive number"),
		INVALID_PRICE("Price must be a positive number"),
		MISSING_VALUE("Required value is missing or null"),
		INVALID_SYMBOL("Stock symbol is invalid or empty"),
		OUT_OF_RANGE("Value is outside acceptable range");
		
		private final String message;
		
		ValidationError(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
	}
	
	public static class ValidationResult {
		private boolean valid;
		private ValidationError error;
		private String details;
		
		public ValidationResult(boolean valid) {
			this.valid = valid;
			this.error = null;
			this.details = null;
		}
		
		public ValidationResult(ValidationError error, String details) {
			this.valid = false;
			this.error = error;
			this.details = details;
		}
		
		public boolean isValid() {
			return valid;
		}
		
		public ValidationError getError() {
			return error;
		}
		
		public String getDetails() {
			return details;
		}
		
		public String getErrorMessage() {
			if (valid) {
				return "No errors";
			}
			return error.getMessage() + (details != null ? ": " + details : "");
		}
		
		@Override
		public String toString() {
			return "ValidationResult [valid=" + valid + ", error=" + (error != null ? error.name() : "null")
					+ ", details=" + details + "]";
		}
	}
	
	/**
	 * Validate date string in format dd/MM/yyyy or dd-MM-yyyy
	 */
	public static ValidationResult validateDate(String dateString) {
		if (dateString == null || dateString.trim().isEmpty()) {
			return new ValidationResult(ValidationError.MISSING_VALUE, "Date string is null or empty");
		}
		
		String trimmed = dateString.trim();
		
		// Try parsing as dd/MM/yyyy
		try {
			String[] parts = trimmed.split("/");
			if (parts.length == 3) {
				int day = Integer.parseInt(parts[0]);
				int month = Integer.parseInt(parts[1]);
				int year = Integer.parseInt(parts[2]);
				
				// Validate ranges
				if (day < 1 || day > 31) {
					return new ValidationResult(ValidationError.INVALID_DATE, "Day out of range: " + day);
				}
				if (month < 1 || month > 12) {
					return new ValidationResult(ValidationError.INVALID_DATE, "Month out of range: " + month);
				}
				if (year < 1900 || year > 2100) {
					return new ValidationResult(ValidationError.INVALID_DATE, "Year out of range: " + year);
				}
				
				// Try to create LocalDate
				try {
					LocalDate.of(year, month, day);
					return new ValidationResult(true);
				} catch (java.time.DateTimeException e) {
					return new ValidationResult(ValidationError.INVALID_DATE, 
						"Invalid date: " + dateString + " (" + e.getMessage() + ")");
				}
			}
		} catch (NumberFormatException e) {
			return new ValidationResult(ValidationError.INVALID_DATE, 
				"Date contains non-numeric values: " + dateString);
		}
		
		return new ValidationResult(ValidationError.INVALID_DATE, "Invalid date format: " + dateString);
	}
	
	/**
	 * Validate date from parsed components
	 */
	public static ValidationResult validateDate(int day, int month, int year) {
		try {
			LocalDate.of(year, month, day);
			return new ValidationResult(true);
		} catch (java.time.DateTimeException e) {
			return new ValidationResult(ValidationError.INVALID_DATE, 
				"Invalid date: " + day + "/" + month + "/" + year);
		}
	}
	
	/**
	 * Validate amount (must be positive number)
	 */
	public static ValidationResult validateAmount(String amountString) {
		if (amountString == null || amountString.trim().isEmpty()) {
			return new ValidationResult(ValidationError.MISSING_VALUE, "Amount is null or empty");
		}
		
		try {
			double amount = Double.parseDouble(amountString.trim());
			if (amount < 0) {
				return new ValidationResult(ValidationError.INVALID_AMOUNT, 
					"Amount must be positive: " + amount);
			}
			return new ValidationResult(true);
		} catch (NumberFormatException e) {
			return new ValidationResult(ValidationError.INVALID_AMOUNT, 
				"Amount is not a valid number: " + amountString);
		}
	}
	
	/**
	 * Validate quantity (must be positive number)
	 */
	public static ValidationResult validateQuantity(String quantityString) {
		if (quantityString == null || quantityString.trim().isEmpty()) {
			return new ValidationResult(ValidationError.MISSING_VALUE, "Quantity is null or empty");
		}
		
		try {
			double quantity = Double.parseDouble(quantityString.trim());
			if (quantity <= 0) {
				return new ValidationResult(ValidationError.INVALID_QUANTITY, 
					"Quantity must be positive: " + quantity);
			}
			return new ValidationResult(true);
		} catch (NumberFormatException e) {
			return new ValidationResult(ValidationError.INVALID_QUANTITY, 
				"Quantity is not a valid number: " + quantityString);
		}
	}
	
	/**
	 * Validate holding days (must be non-negative integer)
	 */
	public static ValidationResult validateHoldingDays(String daysString) {
		if (daysString == null || daysString.trim().isEmpty()) {
			return new ValidationResult(ValidationError.MISSING_VALUE, "Holding days is null or empty");
		}
		
		try {
			int days = Integer.parseInt(daysString.trim());
			if (days < 0) {
				return new ValidationResult(ValidationError.INVALID_HOLDING_DAYS, 
					"Holding days must be non-negative: " + days);
			}
			if (days > 36500) { // Approximately 100 years
				return new ValidationResult(ValidationError.OUT_OF_RANGE, 
					"Holding days unreasonably large: " + days);
			}
			return new ValidationResult(true);
		} catch (NumberFormatException e) {
			return new ValidationResult(ValidationError.INVALID_HOLDING_DAYS, 
				"Holding days is not a valid integer: " + daysString);
		}
	}
	
	/**
	 * Validate stock symbol (must not be empty)
	 */
	public static ValidationResult validateSymbol(String symbol) {
		if (symbol == null || symbol.trim().isEmpty()) {
			return new ValidationResult(ValidationError.INVALID_SYMBOL, "Stock symbol is empty");
		}
		
		String trimmed = symbol.trim().toUpperCase();
		
		// Check length
		if (trimmed.length() < 1 || trimmed.length() > 10) {
			return new ValidationResult(ValidationError.INVALID_SYMBOL, 
				"Symbol length invalid: " + trimmed.length());
		}
		
		// Check contains only alphanumeric and hyphen
		if (!trimmed.matches("^[A-Z0-9\\-&]+$")) {
			return new ValidationResult(ValidationError.INVALID_SYMBOL, 
				"Symbol contains invalid characters: " + trimmed);
		}
		
		return new ValidationResult(true);
	}
	
	/**
	 * Validate price (must be positive number)
	 */
	public static ValidationResult validatePrice(String priceString) {
		if (priceString == null || priceString.trim().isEmpty()) {
			return new ValidationResult(ValidationError.MISSING_VALUE, "Price is null or empty");
		}
		
		try {
			double price = Double.parseDouble(priceString.trim());
			if (price <= 0) {
				return new ValidationResult(ValidationError.INVALID_PRICE, 
					"Price must be positive: " + price);
			}
			if (price > 999999.99) {
				return new ValidationResult(ValidationError.OUT_OF_RANGE, 
					"Price unreasonably high: " + price);
			}
			return new ValidationResult(true);
		} catch (NumberFormatException e) {
			return new ValidationResult(ValidationError.INVALID_PRICE, 
				"Price is not a valid number: " + priceString);
		}
	}
	
	/**
	 * Validate double value is within range
	 */
	public static ValidationResult validateInRange(double value, double min, double max, String fieldName) {
		if (value < min || value > max) {
			return new ValidationResult(ValidationError.OUT_OF_RANGE, 
				fieldName + " out of range [" + min + "," + max + "]: " + value);
		}
		return new ValidationResult(true);
	}
	
	/**
	 * Validate value is not null
	 */
	public static ValidationResult validateNotNull(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			return new ValidationResult(ValidationError.MISSING_VALUE, 
				fieldName + " is null or empty");
		}
		return new ValidationResult(true);
	}
	
	/**
	 * Log validation result
	 */
	public static void logValidationResult(ValidationResult result, int rowNum, String context) {
		if (!result.isValid()) {
			HurdleLogger.warn("Validation failed at row " + rowNum + " (" + context + "): " + 
				result.getErrorMessage());
		}
	}
}
