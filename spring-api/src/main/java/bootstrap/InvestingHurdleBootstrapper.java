/**
 * 
 */
package bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import params.EquityLoader;
import params.WorkbookLoader;
import security.Security;
import exception.InvalidSecurityException;
import logging.HurdleLogger;
import util.ConfigFileManager;
import util.DataValidator;
import util.QuarterConfig;
import util.QuarterlyBreakdown;

/**
 * @author ajay
 *
 */
public class InvestingHurdleBootstrapper {
	
	private static InvestingHurdleBootstrapper INSTANCE = new InvestingHurdleBootstrapper();
	private WorkbookLoader workbookLoader;
	private EquityLoader equityLoader;
	private ConfigFileManager configManager;
	private QuarterConfig quarterConfig;
	private static Logger logger;
	
	public ConcurrentMap<String, Queue<Security>> securityMap = new ConcurrentHashMap<>();

	public ConcurrentMap<String, Queue<Security>> getSecurityMap() {
		return securityMap;
	}


	public void setSecurityMap(ConcurrentMap<String, Queue<Security>> securityMap) {
		INSTANCE.securityMap = securityMap;
	}

	private InvestingHurdleBootstrapper() {
		
	}
	
	public static InvestingHurdleBootstrapper getInstance() {
		return INSTANCE;
	}

	/**
	 * @param args - Command-line arguments:
	 *   --tax-file <path>         : Path to tax workbook
	 *   --config-file <path>      : Path to configuration file
	 *   --financial-year <fy>     : Financial year (e.g., "FY 2021-22")
	 *   --output-dir <path>       : Output directory
	 *   --log-level <level>       : Log level (DEBUG, INFO, WARN, ERROR)
	 *   --config-props <path>     : Properties file for configuration
	 */
	public static void main(String[] args) {
		
		System.out.println("***************** WELCOME TO THE INVESTING WORLD... ********************");
		System.out.println();
		
		try {
			// Initialize configuration
			INSTANCE.initializeConfiguration(args);
			
			// Validate configuration files
			INSTANCE.validateConfiguration();
			
			// Initialize quarter configuration
			INSTANCE.initializeQuarterConfig();
			
			// Load equity data
			INSTANCE.initEquityLoader();
			
			// Print results
			INSTANCE.printEquityDetails();
			INSTANCE.printQuarterlyBreakdown();
			
			System.out.println("\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-* END *-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
			HurdleLogger.info("Application completed successfully");
			
		} catch (InvalidSecurityException e) {
			HurdleLogger.error("Security validation failed [" + e.getErrorCode() + "]: " + e.getMessage());
			System.err.println("\n[ERROR] " + e.getMessage());
			System.exit(e.getExitCode());
		} catch (IOException e) {
			HurdleLogger.error("File I/O error: " + e.getMessage(), e);
			System.err.println("\n[ERROR] File operation failed: " + e.getMessage());
			System.exit(4);
		} catch (Exception e) {
			HurdleLogger.error("Unexpected error during initialization: " + e.getMessage(), e);
			System.err.println("\n[ERROR] Unexpected error: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		
	}


	/**
	 * Initialize configuration from command-line args or properties file
	 */
	private void initializeConfiguration(String[] args) {
		try {
			// Check for properties file argument
			String propsFile = getArgumentValue(args, "--config-props");
			
			if (propsFile != null) {
				HurdleLogger.info("Loading configuration from properties file: " + propsFile);
				this.configManager = ConfigFileManager.loadFromFile(propsFile);
			} else if (args.length > 0) {
				HurdleLogger.info("Loading configuration from command-line arguments");
				this.configManager = ConfigFileManager.fromCommandLine(args);
			} else {
				HurdleLogger.info("Using default configuration");
				this.configManager = new ConfigFileManager();
			}
			
			HurdleLogger.info("Configuration initialized:");
			HurdleLogger.info("  Tax Workbook: " + configManager.getTaxWorkbookPath());
			HurdleLogger.info("  Financial Year: " + configManager.getFinancialYear());
			HurdleLogger.info("  Log Level: " + configManager.getLogLevel());
			
		} catch (Exception e) {
			HurdleLogger.warn("Failed to load configuration, using defaults: " + e.getMessage());
			this.configManager = new ConfigFileManager();
		}
	}
	
	/**
	 * Validate that required configuration files exist
	 */
	private void validateConfiguration() throws InvalidSecurityException {
		String taxWorkbook = configManager.getTaxWorkbookPath();
		
		// Validate tax workbook exists
		File taxFile = new File(taxWorkbook);
		if (!taxFile.exists()) {
			throw new InvalidSecurityException(
				InvalidSecurityException.ErrorCode.MISSING_FILE,
				"Tax workbook not found: " + taxWorkbook
			);
		}
		
		// Create output directory if needed
		String outputDir = configManager.getOutputDirectory();
		File outDir = new File(outputDir);
		if (!outDir.exists()) {
			boolean created = outDir.mkdirs();
			if (created) {
				HurdleLogger.info("Created output directory: " + outputDir);
			} else {
				HurdleLogger.warn("Failed to create output directory: " + outputDir);
			}
		}
		
		HurdleLogger.info("Configuration validation completed");
	}
	
	/**
	 * Initialize quarter configuration based on financial year
	 */
	private void initializeQuarterConfig() {
		String fy = configManager.getFinancialYear();
		
		if (fy.contains("2021-22") || fy.contains("21-22")) {
			this.quarterConfig = QuarterConfig.createFY202122();
			HurdleLogger.info("Using FY 2021-22 quarter configuration");
		} else if (fy.contains("2022-23") || fy.contains("22-23")) {
			this.quarterConfig = QuarterConfig.createFY202223();
			HurdleLogger.info("Using FY 2022-23 quarter configuration");
		} else {
			// Default to FY 2021-22
			this.quarterConfig = QuarterConfig.createFY202122();
			HurdleLogger.warn("Unknown financial year '" + fy + "', defaulting to FY 2021-22");
		}
		
		System.out.println("Quarter configuration: " + quarterConfig);
	}
	
	/**
	 * Extract argument value from command-line args
	 */
	private String getArgumentValue(String[] args, String argName) {
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals(argName)) {
				return args[i + 1];
			}
		}
		return null;
	}

	public void printEquityDetails() {
		// TODO Auto-generated method stub
		//this.equityLoader = new EquityLoader();
		System.out.println("$$$$$$$$$********  STCG  ********$$$$$$$$$\n");
		System.out.println("Full Value of consideration : " + this.equityLoader.getTotalStcgSell());
		System.out.println("Cost of acquisition : " + this.equityLoader.getTotalStcgBuy());
		System.out.println("STCG = " + (this.equityLoader.getTotalStcgSell()-this.equityLoader.getTotalStcgBuy()));
		System.out.println("STCG total : " + this.equityLoader.getTotalStcg());
		System.out.println("STCG Q1 = " + this.equityLoader.getStcgQ1());
		System.out.println("STCG Q2 = " + this.equityLoader.getStcgQ2());
		System.out.println("STCG Q3 = " + this.equityLoader.getStcgQ3());
		System.out.println("STCG Q4 = " + this.equityLoader.getStcgQ4());
		System.out.println("STCG Q5 = " + this.equityLoader.getStcgQ5());
		
		System.out.println("\n$$$$$$$$$********  SPECULATION  ********$$$$$$$$$\n");
		System.out.println("Full Value of consideration : " + this.equityLoader.getTotalIntraSell());
		System.out.println("Cost of acquisition : " + this.equityLoader.getTotalIntraBuy());
		System.out.println("PL = " + (this.equityLoader.getTotalIntraSell()-this.equityLoader.getTotalIntraBuy()));
		System.out.println("Turnover total intraday : " + this.equityLoader.getTotalIntraTurnover());
	}
	
	/**
	 * Print detailed quarterly breakdown using QuarterlyBreakdown utility
	 */
	public void printQuarterlyBreakdown() {
		if (this.equityLoader == null) {
			return;
		}
		
		System.out.println("\n$$$$$$$$$********  QUARTERLY BREAKDOWN  ********$$$$$$$$$\n");
		
		// Create quarterly breakdown object
		QuarterlyBreakdown breakdown = new QuarterlyBreakdown(
			this.equityLoader.getStcgQ1(),
			this.equityLoader.getStcgQ2(),
			this.equityLoader.getStcgQ3(),
			this.equityLoader.getStcgQ4(),
			this.equityLoader.getStcgQ5()
		);
		
		// Print detailed breakdown
		if (quarterConfig != null) {
			for (int i = 1; i <= 5; i++) {
				util.Quarter q = quarterConfig.getQuarterByCode("Q" + i);
				if (q != null) {
					System.out.printf("Quarter %d (%s): ₹%.2f%n", 
						i, q.getName(), breakdown.getQuarterValue(i));
					System.out.printf("  Period: %s to %s%n", 
						q.getStartDate(), q.getEndDate());
				} else {
					System.out.printf("Quarter %d: ₹%.2f%n", 
						i, breakdown.getQuarterValue(i));
				}
			}
		} else {
			// Fallback if quarterConfig not available
			System.out.println("Q1: " + breakdown.getQ1());
			System.out.println("Q2: " + breakdown.getQ2());
			System.out.println("Q3: " + breakdown.getQ3());
			System.out.println("Q4: " + breakdown.getQ4());
			System.out.println("Q5: " + breakdown.getQ5());
		}
		
		System.out.println("\nTotal STCG across all quarters: ₹" + breakdown.getTotal());
	}


	private void initEquityLoader() throws Exception {
		// TODO Auto-generated method stub
		this.equityLoader = new EquityLoader();
		try {
			this.equityLoader.initialize();
		} catch (InvalidSecurityException e) {
			throw e;
		} catch (IOException e) {
			throw new InvalidSecurityException(InvalidSecurityException.ErrorCode.IO_ERROR, 
				"Failed to read Excel file: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new InvalidSecurityException(InvalidSecurityException.ErrorCode.PARSE_ERROR, 
				"Exception during equity loader initialization: " + e.getMessage(), e);
		}
	}


	private static void printSecurities() {
		//INSTANCE.getLogger().info("Into printSecurity method...");
		LogManager.getRootLogger().info("Into printSecurity method...");
		Iterator<Entry<String, Queue<Security>>> itr = INSTANCE.securityMap.entrySet().iterator();
		Queue<Security> q;
		while(itr.hasNext()) {
			Entry<String, Queue<Security>> entry = itr.next();
			q = entry.getValue();
			
			setAveragePrices();
			
			Iterator<Security> iterator = q.iterator();
			Security stock = null;
			System.out.println("Key = " + entry.getKey() + ", Value = ");
			
			while(iterator.hasNext()) {
				stock = iterator.next();
				System.out.println(stock.toString() + "\n");
			}
			System.out.println("\n");
		}
	}


	private static void setAveragePrices() {
		
	}


	private void initWorkbookLoader() {
		this.workbookLoader = new WorkbookLoader();
		try {
			this.workbookLoader.initialize();
		} catch (Exception e) {
			System.out.println("Exception in initialization of workbook loader...\n");
			e.printStackTrace();
		}
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public ConfigFileManager getConfigManager() {
		return configManager;
	}
	
	public void setConfigManager(ConfigFileManager configManager) {
		this.configManager = configManager;
	}
	
	public QuarterConfig getQuarterConfig() {
		return quarterConfig;
	}
	
	public void setQuarterConfig(QuarterConfig quarterConfig) {
		this.quarterConfig = quarterConfig;
	}

}
