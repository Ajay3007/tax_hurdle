package com.investinghurdle.ui;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Upload Scene - File upload with drag-drop and broker detection
 */
public class UploadScene {
    
    private static final Logger logger = LogManager.getLogger(UploadScene.class);
    private static final String DROP_ZONE_DEFAULT_STYLE = "-fx-border-style: dashed; -fx-border-width: 2; -fx-border-color: #cccccc; " +
            "-fx-background-color: #f9f9f9; -fx-padding: 20;";
        private static final String DROP_ZONE_HOVER_STYLE = "-fx-border-style: dashed; -fx-border-width: 2; -fx-border-color: #4da3ff; " +
            "-fx-background-color: #f0f8ff; -fx-padding: 20;";
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    private InvestingHurdleUI mainApp;
    private ApiClient apiClient;
    private SettingsManager settingsManager;
    private Scene scene;
    private Label brokerLabel;
    private Label statusLabel;
    private Button calculateButton;
    private Button browseButton;
    private VBox dropZone;
    private File selectedFile;
    private boolean browseDisabledBeforeDrag;
    private boolean calculateDisabledBeforeDrag;
    
    public UploadScene(InvestingHurdleUI mainApp, ApiClient apiClient, SettingsManager settingsManager) {
        this.mainApp = mainApp;
        this.apiClient = apiClient;
        this.settingsManager = settingsManager;
        this.scene = buildScene();
    }
    
    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Title
        Label title = new Label("Upload & Detect Broker");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        // Drop zone
        VBox dropZone = createDropZone();
        
        // Broker info panel
        VBox brokerInfo = createBrokerInfoPanel();
        
        // Bottom buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        browseButton = new Button("Browse File");
        calculateButton = new Button("Calculate Taxes");
        calculateButton.setDisable(true);
        browseButton.setOnAction(e -> browseFile());
        calculateButton.setOnAction(e -> performCalculation());
        buttonBox.getChildren().addAll(browseButton, calculateButton);
        
        // Main layout
        VBox center = new VBox(15);
        center.setPadding(new Insets(0, 0, 0, 0));
        center.getChildren().addAll(title, dropZone, brokerInfo, buttonBox);
        
        root.setCenter(new ScrollPane(center));
        return new Scene(root, 800, 600);
    }
    
    private VBox createDropZone() {
        dropZone = new VBox();
        dropZone.setPrefHeight(150);
        dropZone.setStyle(DROP_ZONE_DEFAULT_STYLE);
        
        Label dropText = new Label("Drag & drop Excel file here\nor click Browse to select");
        dropText.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");
        dropZone.getChildren().add(dropText);
        
        // Drag and drop handlers
        dropZone.setOnDragEntered(this::handleDragEntered);
        dropZone.setOnDragOver(this::handleDragOver);
        dropZone.setOnDragDropped(this::handleDragDropped);
        dropZone.setOnDragExited(this::handleDragExited);
        
        return dropZone;
    }
    
    private VBox createBrokerInfoPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: #fafafa;");
        
        Label infoTitle = new Label("Broker Information");
        infoTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        brokerLabel = new Label("No file selected");
        brokerLabel.setStyle("-fx-text-fill: #666666;");
        
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999999;");
        
        panel.getChildren().addAll(infoTitle, brokerLabel, statusLabel);
        return panel;
    }
    
    private void handleDragEntered(DragEvent event) {
        browseDisabledBeforeDrag = browseButton.isDisable();
        calculateDisabledBeforeDrag = calculateButton.isDisable();
        browseButton.setDisable(true);
        calculateButton.setDisable(true);
        dropZone.setStyle(DROP_ZONE_HOVER_STYLE);
        event.consume();
    }

    private void handleDragOver(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragExited(DragEvent event) {
        dropZone.setStyle(DROP_ZONE_DEFAULT_STYLE);
        browseButton.setDisable(browseDisabledBeforeDrag);
        calculateButton.setDisable(calculateDisabledBeforeDrag || selectedFile == null);
        event.consume();
    }
    
    private void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            List<File> files = dragboard.getFiles();
            if (!files.isEmpty()) {
                File file = files.get(0);
                if (file.getName().endsWith(".xlsx")) {
                    selectedFile = file;
                    mainApp.setLastUploadedFile(file);
                    detectBroker(file);
                    success = true;
                } else {
                    mainApp.showError("Invalid File", "Please select an Excel (.xlsx) file");
                    statusLabel.setText("Unsupported file: " + file.getName());
                }
            }
        }
        dropZone.setStyle(DROP_ZONE_DEFAULT_STYLE);
        browseButton.setDisable(browseDisabledBeforeDrag);
        if (success) {
            calculateButton.setDisable(true);
        } else {
            calculateButton.setDisable(calculateDisabledBeforeDrag);
        }
        event.setDropCompleted(success);
        event.consume();
    }
    
    private void browseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Excel File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            selectedFile = file;
            mainApp.setLastUploadedFile(file);
            detectBroker(file);
        }
    }
    
    private void detectBroker(File file) {
        brokerLabel.setText("Detecting...");
        statusLabel.setText("Detecting broker for " + file.getName());
        calculateButton.setDisable(true);
        
        executor.execute(() -> {
            try {
                JsonObject result = apiClient.detectBroker(file);
                Platform.runLater(() -> {
                    String brokerName = result.get("broker_name").getAsString();
                    boolean autoDetected = result.get("auto_detected").getAsBoolean();
                    brokerLabel.setText("Broker: " + brokerName + (autoDetected ? " (auto-detected)" : " (template)"));
                    statusLabel.setText("File: " + file.getName());
                    calculateButton.setDisable(false);
                    logger.info("Broker detected: " + brokerName);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mainApp.showError("Detection Error", "Failed to detect broker: " + e.getMessage());
                    brokerLabel.setText("Detection failed");
                    statusLabel.setText("Detection failed. Please try another file.");
                });
            }
        });
    }
    
    private void performCalculation() {
        if (selectedFile == null) {
            mainApp.showError("No File", "Please select a file first");
            return;
        }
        
        brokerLabel.setText("Calculating...");
        statusLabel.setText("Calculating taxes for " + selectedFile.getName());
        calculateButton.setDisable(true);
        
        executor.execute(() -> {
            try {
                JsonObject result = apiClient.calculate(
                    selectedFile,
                    settingsManager.getFinancialYear(),
                    settingsManager.getQuarterScheme()
                );
                Platform.runLater(() -> {
                    statusLabel.setText("Opening results...");
                    mainApp.showResults(result);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    mainApp.showError("Calculation Error", "Failed to calculate: " + e.getMessage());
                    brokerLabel.setText("Calculation failed");
                    statusLabel.setText("Calculation failed. Please retry.");
                    calculateButton.setDisable(false);
                });
            }
        });
    }
    
    public Scene getScene() {
        return scene;
    }
}
