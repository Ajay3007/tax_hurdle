package com.investinghurdle.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main InvestingHurdle Desktop Application (JavaFX)
 * Provides a GUI for uploading Excel files, detecting broker format,
 * calculating taxes, and exporting reports.
 */
public class InvestingHurdleUI extends Application {
    
    private static final Logger logger = LogManager.getLogger(InvestingHurdleUI.class);
    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 800;
    
    private Stage primaryStage;
    private SettingsManager settingsManager;
    private ApiClient apiClient;
    private UploadScene uploadSceneController;
    private ResultsScene resultsSceneController;
    private SettingsScene settingsSceneController;
    private BorderPane mainLayout;
    private boolean darkTheme = false;
    private java.io.File lastUploadedFile;
    
    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            settingsManager = new SettingsManager();
            apiClient = new ApiClient(settingsManager.getApiUrl());
            
            // Build main layout with top navigation and swappable center
            mainLayout = new BorderPane();
            mainLayout.setStyle("-fx-font-family: 'Segoe UI', Arial;");
            
            // Create top navigation bar
            mainLayout.setTop(createTopBar());
            
            // Create scenes (which return their root nodes)
            uploadSceneController = new UploadScene(this, apiClient, settingsManager);
            resultsSceneController = new ResultsScene(this, apiClient, settingsManager);
            settingsSceneController = new SettingsScene(this, settingsManager, apiClient);
            
            // Set initial content to upload scene
            mainLayout.setCenter(uploadSceneController.getScene().getRoot());
            
            // Create and show main scene
            Scene mainScene = new Scene(mainLayout, WINDOW_WIDTH, WINDOW_HEIGHT);
            applyTheme(mainScene, darkTheme);
            
            primaryStage.setTitle("InvestingHurdle - Tax Calculator");
            primaryStage.setScene(mainScene);
            primaryStage.show();
            
            logger.info("Application started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            showError("Startup Error", "Failed to start application: " + e.getMessage());
        }
    }
    
    /**
     * Switch active scene by updating center content
     */
    public void switchToScene(Scene scene) {
        if (scene != null && scene.getRoot() != null) {
            mainLayout.setCenter(scene.getRoot());
        }
    }

    public Scene getUploadScene() {
        return uploadSceneController != null ? uploadSceneController.getScene() : null;
    }

    public Scene getResultsScene() {
        return resultsSceneController != null ? resultsSceneController.getScene() : null;
    }

    public Scene getSettingsScene() {
        return settingsSceneController != null ? settingsSceneController.getScene() : null;
    }
    
    /**
     * Switch to results scene after calculation
     */
    public void showResults(Object calculationResult) {
        if (resultsSceneController != null) {
            resultsSceneController.displayResults(calculationResult);
        }
        if (resultsSceneController != null) {
            switchToScene(resultsSceneController.getScene());
        }
    }

    public java.io.File getLastUploadedFile() {
        return lastUploadedFile;
    }

    public void setLastUploadedFile(java.io.File file) {
        this.lastUploadedFile = file;
    }
    
    /**
     * Create top navigation bar
     */
    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
        
        Button uploadBtn = new Button("Upload");
        Button resultsBtn = new Button("Results");
        Button settingsBtn = new Button("Settings");
        ToggleButton themeToggle = new ToggleButton("🌙 Dark");
        
        uploadBtn.setOnAction(e -> switchToScene(uploadSceneController != null ? uploadSceneController.getScene() : null));
        resultsBtn.setOnAction(e -> switchToScene(resultsSceneController != null ? resultsSceneController.getScene() : null));
        settingsBtn.setOnAction(e -> switchToScene(settingsSceneController != null ? settingsSceneController.getScene() : null));
        themeToggle.setOnAction(e -> toggleTheme());
        
        topBar.getChildren().addAll(uploadBtn, resultsBtn, settingsBtn, new Separator(), themeToggle);
        return topBar;
    }
    
    /**
     * Toggle light/dark theme
     */
    private void toggleTheme() {
        darkTheme = !darkTheme;
        Scene currentScene = primaryStage.getScene();
        applyTheme(currentScene, darkTheme);
        logger.info("Theme toggled: " + (darkTheme ? "DARK" : "LIGHT"));
    }
    
    /**
     * Apply light/dark theme stylesheet
     */
    private void applyTheme(Scene scene, boolean isDark) {
        String css = isDark ? getStylesheetDark() : getStylesheetLight();
        scene.getStylesheets().clear();
        scene.getStylesheets().add("data:text/css," + css.replaceAll("\\n", "").replaceAll("\\s+", " "));
    }
    
    private String getStylesheetLight() {
        return """
            * { -fx-font-family: 'Segoe UI', Arial; }
            .root { -fx-base: #ffffff; -fx-text-fill: #000000; }
            .button { -fx-padding: 5; -fx-font-size: 12; }
            .table-view { -fx-font-size: 11; }
            """;
    }
    
    private String getStylesheetDark() {
        return """
            * { -fx-font-family: 'Segoe UI', Arial; }
            .root { -fx-base: #2b2b2b; -fx-text-fill: #ffffff; }
            .button { -fx-padding: 5; -fx-font-size: 12; -fx-text-fill: #ffffff; }
            .table-view { -fx-font-size: 11; -fx-text-fill: #ffffff; }
            .text-input { -fx-control-inner-background: #3c3c3c; -fx-text-fill: #ffffff; }
            """;
    }
    
    /**
     * Show error dialog
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show info dialog
     */
    public void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
