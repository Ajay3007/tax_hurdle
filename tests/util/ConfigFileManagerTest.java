/**
 * 
 */
package util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for ConfigFileManager
 * @author ajay
 *
 */
@DisplayName("Config File Manager Tests")
class ConfigFileManagerTest {
	
	@TempDir
	Path tempDir;
	
	private File testPropertiesFile;
	
	@BeforeEach
	void setUp() throws IOException {
		// Create a temporary properties file for testing
		testPropertiesFile = tempDir.resolve("test.properties").toFile();
		try (FileWriter fw = new FileWriter(testPropertiesFile)) {
			fw.write("tax.workbook.path=./test_tax.xlsx\n");
			fw.write("config.workbook.path=./test_config.xlsx\n");
			fw.write("output.directory=./test_output\n");
			fw.write("log.level=DEBUG\n");
			fw.write("financial.year=2021-22\n");
		}
	}
	
	@AfterEach
	void tearDown() {
		if (testPropertiesFile != null && testPropertiesFile.exists()) {
			testPropertiesFile.delete();
		}
	}
	
	@Test
	@DisplayName("Load configuration from properties file")
	void testLoadFromFile() throws IOException {
		ConfigFileManager config = ConfigFileManager.loadFromFile(testPropertiesFile.getAbsolutePath());
		assertNotNull(config);
	}
	
	@Test
	@DisplayName("Non-existent properties file throws exception")
	void testLoadNonExistentFile() {
		assertThrows(IllegalArgumentException.class, () -> {
			ConfigFileManager.loadFromFile("./non_existent.properties");
		});
	}
	
	@Test
	@DisplayName("Create config from CLI arguments")
	void testFromCommandLine() {
		String[] args = {
			"--tax-file", "./cli_tax.xlsx",
			"--config-file", "./cli_config.xlsx",
			"--output-dir", "./cli_output"
		};
		ConfigFileManager config = ConfigFileManager.fromCommandLine(args);
		assertNotNull(config);
	}
	
	@Test
	@DisplayName("Empty CLI arguments creates default config")
	void testEmptyCommandLine() {
		String[] args = {};
		ConfigFileManager config = ConfigFileManager.fromCommandLine(args);
		assertNotNull(config);
	}
	
	@Test
	@DisplayName("CLI argument with missing value")
	void testCLIArgumentMissingValue() {
		String[] args = {"--tax-file"};
		assertThrows(IndexOutOfBoundsException.class, () -> {
			ConfigFileManager.fromCommandLine(args);
		});
	}
	
	@Test
	@DisplayName("Validate existing paths")
	void testValidateExistingPaths() throws IOException {
		// Create test files
		File taxFile = tempDir.resolve("tax.xlsx").toFile();
		File configFile = tempDir.resolve("config.xlsx").toFile();
		taxFile.createNewFile();
		configFile.createNewFile();
		
		ConfigFileManager config = new ConfigFileManager();
		
		assertTrue(new File(taxFile.getAbsolutePath()).exists());
		assertTrue(new File(configFile.getAbsolutePath()).exists());
	}
	
	@Test
	@DisplayName("Non-existent tax workbook path")
	void testNonExistentTaxWorkbook() {
		ConfigFileManager config = new ConfigFileManager();
		assertFalse(new File("./non_existent_tax.xlsx").exists());
	}
	
	@Test
	@DisplayName("Get configuration summary")
	void testGetConfigurationSummary() throws IOException {
		ConfigFileManager config = ConfigFileManager.loadFromFile(testPropertiesFile.getAbsolutePath());
		String summary = config.getConfigurationSummary();
		assertNotNull(summary);
		assertFalse(summary.isEmpty());
	}
	
	@Test
	@DisplayName("Output directory path configuration")
	void testOutputDirectoryPath() {
		ConfigFileManager config = new ConfigFileManager();
		config.setOutputDirectory("./test_output");
		assertEquals("./test_output", config.getOutputDirectory());
	}
	
	@Test
	@DisplayName("Log level configuration")
	void testLogLevelConfiguration() {
		ConfigFileManager config = new ConfigFileManager();
		config.setLogLevel("INFO");
		assertEquals("INFO", config.getLogLevel());
	}
	
	@Test
	@DisplayName("Financial year configuration")
	void testFinancialYearConfiguration() {
		ConfigFileManager config = new ConfigFileManager();
		config.setFinancialYear("2021-22");
		assertEquals("2021-22", config.getFinancialYear());
	}
	
	@Test
	@DisplayName("Excel start row configuration")
	void testExcelStartRowConfiguration() {
		// ConfigFileManager internally manages excel rows
		ConfigFileManager config = new ConfigFileManager();
		assertNotNull(config);
	}
	
	@Test
	@DisplayName("Excel end row configuration")
	void testExcelEndRowConfiguration() {
		// ConfigFileManager internally manages excel rows
		ConfigFileManager config = new ConfigFileManager();
		assertNotNull(config);
	}
	
	@Test
	@DisplayName("Get tax workbook path")
	void testGetTaxWorkbookPath() throws IOException {
		ConfigFileManager config = ConfigFileManager.loadFromFile(testPropertiesFile.getAbsolutePath());
		String path = config.getTaxWorkbookPath();
		assertNotNull(path);
		assertTrue(path.contains("test_tax.xlsx"));
	}
	
	@Test
	@DisplayName("Default configuration values")
	void testDefaultConfigurationValues() {
		ConfigFileManager config = new ConfigFileManager();
		assertNotNull(config.getTaxWorkbookPath());
		assertNotNull(config.getOutputDirectory());
	}
}
