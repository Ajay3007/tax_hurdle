package com.investinghurdle.ui;

import java.io.*;
import java.util.Properties;

/**
 * Manages application settings (API URL, financial year, quarter scheme, theme)
 */
public class SettingsManager {
    
    private static final String SETTINGS_FILE = "investing-hurdle.properties";
    private static final String DEFAULT_API_URL = "http://localhost:8080";
    private static final String DEFAULT_FINANCIAL_YEAR = "FY 2024-25";
    private static final String DEFAULT_QUARTER_SCHEME = "STANDARD_Q4";
    private static final String DEFAULT_THEME = "LIGHT";
    private static final String DEFAULT_API_KEY = "";
    
    private Properties properties;
    
    public SettingsManager() {
        properties = new Properties();
        loadSettings();
    }
    
    /**
     * Load settings from file; use defaults if file not found
     */
    private void loadSettings() {
        File settingsFile = new File(SETTINGS_FILE);
        if (settingsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(settingsFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Failed to load settings: " + e.getMessage());
                properties.setProperty("api_url", DEFAULT_API_URL);
                properties.setProperty("financial_year", DEFAULT_FINANCIAL_YEAR);
                properties.setProperty("quarter_scheme", DEFAULT_QUARTER_SCHEME);
                properties.setProperty("theme", DEFAULT_THEME);
                properties.setProperty("api_key", DEFAULT_API_KEY);
            }
        } else {
            // Initialize with defaults
            properties.setProperty("api_url", DEFAULT_API_URL);
            properties.setProperty("financial_year", DEFAULT_FINANCIAL_YEAR);
            properties.setProperty("quarter_scheme", DEFAULT_QUARTER_SCHEME);
            properties.setProperty("theme", DEFAULT_THEME);
            properties.setProperty("api_key", DEFAULT_API_KEY);
        }
    }
    
    /**
     * Save settings to file
     */
    public void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(fos, "InvestingHurdle Desktop UI Settings");
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }
    
    public String getApiUrl() {
        return properties.getProperty("api_url", DEFAULT_API_URL);
    }
    
    public void setApiUrl(String url) {
        properties.setProperty("api_url", url);
        saveSettings();
    }
    
    public String getFinancialYear() {
        return properties.getProperty("financial_year", DEFAULT_FINANCIAL_YEAR);
    }
    
    public void setFinancialYear(String fy) {
        properties.setProperty("financial_year", fy);
        saveSettings();
    }
    
    public String getQuarterScheme() {
        return properties.getProperty("quarter_scheme", DEFAULT_QUARTER_SCHEME);
    }
    
    public void setQuarterScheme(String scheme) {
        properties.setProperty("quarter_scheme", scheme);
        saveSettings();
    }
    
    public String getTheme() {
        return properties.getProperty("theme", DEFAULT_THEME);
    }
    
    public void setTheme(String theme) {
        properties.setProperty("theme", theme);
        saveSettings();
    }

    public String getApiKey() {
        return properties.getProperty("api_key", DEFAULT_API_KEY);
    }

    public void setApiKey(String apiKey) {
        properties.setProperty("api_key", apiKey == null ? "" : apiKey);
        saveSettings();
    }
}
