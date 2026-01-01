/**
 * 
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import logging.HurdleLogger;

/**
 * Configuration file manager for managing application settings
 * Supports loading configuration from properties files and command-line arguments
 * @author ajay
 *
 */
public class ConfigFileManager {
	private Properties properties;
	private String configFilePath;
	
	// Default property keys
	private static final String TAX_WORKBOOK_PATH = "tax.workbook.path";
	private static final String CONFIG_WORKBOOK_PATH = "config.workbook.path";
	private static final String OUTPUT_DIRECTORY = "output.directory";
	private static final String LOG_LEVEL = "log.level";
	private static final String LOG_FILE_PATH = "log.file.path";
	private static final String FINANCIAL_YEAR = "financial.year";
	
	// Default values
	private static final String DEFAULT_TAX_WORKBOOK = "./configuration/tax_2122_.xlsx";
	private static final String DEFAULT_CONFIG_WORKBOOK = "./configuration/configuration_stock.xlsx";
	private static final String DEFAULT_OUTPUT_DIR = "./output";
	private static final String DEFAULT_LOG_LEVEL = "INFO";
	private static final String DEFAULT_LOG_FILE = "./logs/hurdle.log";
	private static final String DEFAULT_FY = "FY 2021-22";
	
	public ConfigFileManager() {
		this.properties = new Properties();
		initializeDefaults();
	}
	
	/**
	 * Load configuration from properties file
	 */
	public static ConfigFileManager loadFromFile(String filePath) throws IOException {
		ConfigFileManager manager = new ConfigFileManager();
		manager.configFilePath = filePath;
		
		File configFile = new File(filePath);
		if (!configFile.exists()) {
			HurdleLogger.warn("Configuration file not found: " + filePath + ". Using defaults.");
			return manager;
		}
		
		try (FileInputStream fis = new FileInputStream(configFile)) {
			manager.properties.load(fis);
			HurdleLogger.info("Configuration loaded from: " + filePath);
		} catch (IOException e) {
			HurdleLogger.error("Failed to load configuration file: " + e.getMessage());
			throw e;
		}
		
		return manager;
	}
	
	/**
	 * Load configuration from command-line arguments
	 */
	public static ConfigFileManager fromCommandLine(String[] args) {
		ConfigFileManager manager = new ConfigFileManager();
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				String key = args[i].substring(2);
				String value = (i + 1 < args.length) ? args[i + 1] : null;
				
				if (value != null && !value.startsWith("--")) {
					switch (key) {
						case "tax-file":
							manager.setTaxWorkbookPath(value);
							break;
						case "config-file":
							manager.setConfigWorkbookPath(value);
							break;
						case "output-dir":
							manager.setOutputDirectory(value);
							break;
						case "log-level":
							manager.setLogLevel(value);
							break;
						case "financial-year":
							manager.setFinancialYear(value);
							break;
						default:
							HurdleLogger.warn("Unknown command-line argument: " + key);
					}
					i++; // Skip next argument as it's a value
				}
			}
		}
		
		HurdleLogger.info("Command-line arguments processed");
		return manager;
	}
	
	/**
	 * Initialize default values
	 */
	private void initializeDefaults() {
		properties.setProperty(TAX_WORKBOOK_PATH, DEFAULT_TAX_WORKBOOK);
		properties.setProperty(CONFIG_WORKBOOK_PATH, DEFAULT_CONFIG_WORKBOOK);
		properties.setProperty(OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIR);
		properties.setProperty(LOG_LEVEL, DEFAULT_LOG_LEVEL);
		properties.setProperty(LOG_FILE_PATH, DEFAULT_LOG_FILE);
		properties.setProperty(FINANCIAL_YEAR, DEFAULT_FY);
	}
	
	// Getters
	public String getTaxWorkbookPath() {
		return properties.getProperty(TAX_WORKBOOK_PATH, DEFAULT_TAX_WORKBOOK);
	}
	
	public String getConfigWorkbookPath() {
		return properties.getProperty(CONFIG_WORKBOOK_PATH, DEFAULT_CONFIG_WORKBOOK);
	}
	
	public String getOutputDirectory() {
		return properties.getProperty(OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIR);
	}
	
	public String getLogLevel() {
		return properties.getProperty(LOG_LEVEL, DEFAULT_LOG_LEVEL);
	}
	
	public String getLogFilePath() {
		return properties.getProperty(LOG_FILE_PATH, DEFAULT_LOG_FILE);
	}
	
	public String getFinancialYear() {
		return properties.getProperty(FINANCIAL_YEAR, DEFAULT_FY);
	}
	
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	// Setters
	public void setTaxWorkbookPath(String path) {
		properties.setProperty(TAX_WORKBOOK_PATH, path);
		HurdleLogger.debug("Tax workbook path set to: " + path);
	}
	
	public void setConfigWorkbookPath(String path) {
		properties.setProperty(CONFIG_WORKBOOK_PATH, path);
		HurdleLogger.debug("Config workbook path set to: " + path);
	}
	
	public void setOutputDirectory(String path) {
		properties.setProperty(OUTPUT_DIRECTORY, path);
		HurdleLogger.debug("Output directory set to: " + path);
	}
	
	public void setLogLevel(String level) {
		properties.setProperty(LOG_LEVEL, level);
		HurdleLogger.debug("Log level set to: " + level);
	}
	
	public void setLogFilePath(String path) {
		properties.setProperty(LOG_FILE_PATH, path);
		HurdleLogger.debug("Log file path set to: " + path);
	}
	
	public void setFinancialYear(String fy) {
		properties.setProperty(FINANCIAL_YEAR, fy);
		HurdleLogger.debug("Financial year set to: " + fy);
	}
	
	/**
	 * Validate that required files exist
	 */
	public boolean validatePaths() {
		boolean valid = true;
		
		String taxPath = getTaxWorkbookPath();
		if (!new File(taxPath).exists()) {
			HurdleLogger.error("Tax workbook file not found: " + taxPath);
			valid = false;
		}
		
		String outputDir = getOutputDirectory();
		File outDir = new File(outputDir);
		if (!outDir.exists()) {
			if (outDir.mkdirs()) {
				HurdleLogger.info("Created output directory: " + outputDir);
			} else {
				HurdleLogger.warn("Failed to create output directory: " + outputDir);
			}
		}
		
		return valid;
	}
	
	/**
	 * Get all configuration as string
	 */
	public String getConfigurationSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("Configuration Summary:\n");
		sb.append("  Tax Workbook: ").append(getTaxWorkbookPath()).append("\n");
		sb.append("  Config Workbook: ").append(getConfigWorkbookPath()).append("\n");
		sb.append("  Output Directory: ").append(getOutputDirectory()).append("\n");
		sb.append("  Log Level: ").append(getLogLevel()).append("\n");
		sb.append("  Log File: ").append(getLogFilePath()).append("\n");
		sb.append("  Financial Year: ").append(getFinancialYear());
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "ConfigFileManager [configFilePath=" + configFilePath + ", properties=" + properties.size() + "]";
	}
}
