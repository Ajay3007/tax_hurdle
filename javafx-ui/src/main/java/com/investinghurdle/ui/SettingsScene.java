package com.investinghurdle.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Settings Scene - Configure API URL, financial year, quarter scheme
 */
public class SettingsScene {
    
    private static final Logger logger = LogManager.getLogger(SettingsScene.class);
    
    private InvestingHurdleUI mainApp;
    private SettingsManager settingsManager;
    private ApiClient apiClient;
    private Scene scene;
    
    public SettingsScene(InvestingHurdleUI mainApp, SettingsManager settingsManager, ApiClient apiClient) {
        this.mainApp = mainApp;
        this.settingsManager = settingsManager;
        this.apiClient = apiClient;
        this.scene = buildScene();
    }
    
    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Title
        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        // Settings form
        VBox form = createSettingsForm();
        
        // Back button
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        Button backBtn = new Button("Back to Upload");
        backBtn.setOnAction(e -> mainApp.switchToScene(mainApp.getUploadScene()));
        buttonBox.getChildren().add(backBtn);
        
        // Main layout
        VBox center = new VBox(15);
        center.getChildren().addAll(title, form, buttonBox);
        
        root.setCenter(new ScrollPane(center));
        return new Scene(root, 800, 600);
    }
    
    private VBox createSettingsForm() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: #fafafa;");
        
        // API URL
        HBox apiUrlBox = new HBox(10);
        Label apiUrlLabel = new Label("API Base URL:");
        apiUrlLabel.setPrefWidth(150);
        TextField apiUrlField = new TextField(settingsManager.getApiUrl());
        apiUrlField.setPrefWidth(400);
        Button testApiBtn = new Button("Test Connection");
        testApiBtn.setOnAction(e -> testConnection(apiUrlField.getText()));
        apiUrlBox.getChildren().addAll(apiUrlLabel, apiUrlField, testApiBtn);

        // API Key (optional)
        HBox apiKeyBox = new HBox(10);
        Label apiKeyLabel = new Label("API Key (optional):");
        apiKeyLabel.setPrefWidth(150);
        TextField apiKeyField = new TextField(settingsManager.getApiKey());
        apiKeyField.setPrefWidth(300);
        apiKeyBox.getChildren().addAll(apiKeyLabel, apiKeyField);
        
        // Financial Year
        HBox fyBox = new HBox(10);
        Label fyLabel = new Label("Financial Year:");
        fyLabel.setPrefWidth(150);
        ComboBox<String> fyCombo = new ComboBox<>();
        fyCombo.getItems().addAll("FY 2021-22", "FY 2022-23", "FY 2023-24", "FY 2024-25");
        fyCombo.setValue(settingsManager.getFinancialYear());
        fyCombo.setPrefWidth(200);
        fyBox.getChildren().addAll(fyLabel, fyCombo);
        
        // Quarter Scheme
        HBox qsBox = new HBox(10);
        Label qsLabel = new Label("Quarter Scheme:");
        qsLabel.setPrefWidth(150);
        ComboBox<String> qsCombo = new ComboBox<>();
        qsCombo.getItems().addAll("STANDARD_Q4", "Q5_IT_PORTAL");
        qsCombo.setValue(settingsManager.getQuarterScheme());
        qsCombo.setPrefWidth(200);
        qsBox.getChildren().addAll(qsLabel, qsCombo);
        
        // Save button
        HBox saveBox = new HBox(10);
        saveBox.setPadding(new Insets(15, 0, 0, 0));
        Button saveBtn = new Button("Save Settings");
        Label statusLabel = new Label("");
        saveBtn.setOnAction(e -> {
            settingsManager.setApiUrl(apiUrlField.getText());
            apiClient.setBaseUrl(apiUrlField.getText());
            settingsManager.setApiKey(apiKeyField.getText());
            apiClient.setApiKey(apiKeyField.getText());
            settingsManager.setFinancialYear(fyCombo.getValue());
            settingsManager.setQuarterScheme(qsCombo.getValue());
            statusLabel.setText("Settings saved!");
            logger.info("Settings saved");
        });
        saveBox.getChildren().addAll(saveBtn, statusLabel);
        
        form.getChildren().addAll(
            new Label("API Configuration"),
            apiUrlBox,
            apiKeyBox,
            new Separator(),
            new Label("Calculation Defaults"),
            fyBox,
            qsBox,
            saveBox
        );
        
        return form;
    }
    
    private void testConnection(String apiUrl) {
        try {
            ApiClient testClient = new ApiClient(apiUrl);
            if (testClient.isHealthy()) {
                mainApp.showInfo("Connection OK", "Successfully connected to API at " + apiUrl);
                logger.info("API connection successful");
            } else {
                mainApp.showError("Connection Failed", "Could not connect to API at " + apiUrl);
            }
        } catch (Exception e) {
            mainApp.showError("Connection Error", "Failed to connect: " + e.getMessage());
            logger.error("Connection test failed: " + e.getMessage());
        }
    }
    
    public Scene getScene() {
        return scene;
    }
}
