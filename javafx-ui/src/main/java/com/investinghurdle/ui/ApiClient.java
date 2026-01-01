package com.investinghurdle.ui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * HTTP client wrapper for calling the InvestingHurdle Spring API
 */
public class ApiClient {
    
    private static final Logger logger = LogManager.getLogger(ApiClient.class);
    
    private String baseUrl;
    private String apiKey;
    
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * Set the API base URL
     */
    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    private void applyApiKey(org.apache.hc.core5.http.ClassicHttpRequest request) {
        if (apiKey != null && !apiKey.isEmpty()) {
            request.setHeader("X-API-Key", apiKey);
        }
    }
    
    /**
     * Check if API is available
     */
    public boolean isHealthy() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(baseUrl + "/calculations/health");
            request.setHeader("Accept", "application/json");
            applyApiKey(request);
            var response = client.execute(request, resp -> {
                if (resp.getCode() == 200) {
                    return true;
                }
                return false;
            });
            return response != null && response.booleanValue();
        } catch (Exception e) {
            logger.warn("API health check failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get runtime configuration from API
     */
    public JsonObject getConfig() {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(baseUrl + "/calculations/config");
            request.setHeader("Accept", "application/json");
            applyApiKey(request);
            String response = client.execute(request, resp -> {
                if (resp.getCode() == 200) {
                    return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                }
                throw new RuntimeException("HTTP " + resp.getCode());
            });
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            logger.error("Failed to get config: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get API config", e);
        }
    }
    
    /**
     * Detect broker format from Excel file
     */
    public JsonObject detectBroker(File excelFile) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(baseUrl + "/calculations/detect-broker");
            applyApiKey(request);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", new FileBody(excelFile));
            request.setEntity(builder.build());
            
            String response = client.execute(request, resp -> {
                if (resp.getCode() == 200) {
                    return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                }
                throw new RuntimeException("HTTP " + resp.getCode() + ": " + EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8));
            });
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            logger.error("Broker detection failed: " + e.getMessage(), e);
            throw new RuntimeException("Broker detection failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calculate taxes from Excel file
     */
    public JsonObject calculate(File excelFile, String financialYear, String quarterScheme) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(baseUrl + "/calculations/upload");
            applyApiKey(request);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", new FileBody(excelFile));
            builder.addPart("financial_year", new StringBody(financialYear, ContentType.TEXT_PLAIN));
            builder.addPart("quarter_scheme", new StringBody(quarterScheme, ContentType.TEXT_PLAIN));
            request.setEntity(builder.build());
            
            String response = client.execute(request, resp -> {
                if (resp.getCode() == 200) {
                    return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                }
                throw new RuntimeException("HTTP " + resp.getCode() + ": " + EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8));
            });
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            logger.error("Calculation failed: " + e.getMessage(), e);
            throw new RuntimeException("Calculation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Export calculation results to Excel
     */
    public byte[] export(File excelFile, String financialYear, String quarterScheme) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(baseUrl + "/calculations/export");
            applyApiKey(request);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", new FileBody(excelFile));
            builder.addPart("financial_year", new StringBody(financialYear, ContentType.TEXT_PLAIN));
            builder.addPart("quarter_scheme", new StringBody(quarterScheme, ContentType.TEXT_PLAIN));
            request.setEntity(builder.build());
            
            return client.execute(request, resp -> {
                if (resp.getCode() == 200) {
                    return EntityUtils.toByteArray(resp.getEntity());
                }
                throw new RuntimeException("HTTP " + resp.getCode());
            });
        } catch (Exception e) {
            logger.error("Export failed: " + e.getMessage(), e);
            throw new RuntimeException("Export failed: " + e.getMessage(), e);
        }
    }
}
