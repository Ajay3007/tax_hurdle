/**
 * 
 */
package util;

/**
 * @author ajay
 *
 */
public class HurdleConstant {
	// Default file paths (can be overridden via ConfigFileManager)
	public static final String CONFIGURATION_FILE_PATH;
	public static final String TAX_CONFIG_FILE_PATH;
	public static final String CONFIG_FILE_PATH = "./configuration/app.properties";
	
	static {
		// Initialize default paths
		// These can be overridden by loading configuration from app.properties
		TAX_CONFIG_FILE_PATH = "./configuration/tax_2122_.xlsx";
		CONFIGURATION_FILE_PATH = "./configuration/configuration_stock.xlsx";
	}
}

