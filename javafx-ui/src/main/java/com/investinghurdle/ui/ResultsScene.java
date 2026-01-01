package com.investinghurdle.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Results Scene - Display calculation results with tables and charts
 */
public class ResultsScene {
    
    private static final Logger logger = LogManager.getLogger(ResultsScene.class);
    
    private InvestingHurdleUI mainApp;
    private ApiClient apiClient;
    private SettingsManager settingsManager;
    private Scene scene;
    private JsonObject currentResult;
    private VBox resultsArea;
    private Button exportBtn;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    public ResultsScene(InvestingHurdleUI mainApp, ApiClient apiClient, SettingsManager settingsManager) {
        this.mainApp = mainApp;
        this.apiClient = apiClient;
        this.settingsManager = settingsManager;
        this.scene = buildScene();
    }
    
    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        
        // Title
        Label title = new Label("Calculation Results");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        // Results area (placeholder)
        resultsArea = new VBox(10);
        resultsArea.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15;");
        Label placeholderLabel = new Label("Results will appear here after calculation");
        placeholderLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #999999;");
        resultsArea.getChildren().add(placeholderLabel);
        
        // Bottom buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        Button backBtn = new Button("Back to Upload");
        exportBtn = new Button("Export as Excel");
        exportBtn.setDisable(true);
        backBtn.setOnAction(e -> mainApp.switchToScene(mainApp.getUploadScene()));
        exportBtn.setOnAction(e -> exportResults());
        buttonBox.getChildren().addAll(backBtn, exportBtn);
        
        // Main layout
        VBox center = new VBox(15);
        center.getChildren().addAll(title, resultsArea, buttonBox);
        
        root.setCenter(new ScrollPane(center));
        return new Scene(root, 800, 600);
    }
    
    public void displayResults(Object result) {
        if (result instanceof JsonObject) {
            currentResult = (JsonObject) result;
            updateResultsDisplay();
        }
    }
    
    private void updateResultsDisplay() {
        try {
            // Extract summary data
            JsonObject stcg = currentResult.getAsJsonObject("stcg");
            JsonObject ltcg = currentResult.getAsJsonObject("ltcg");
            JsonObject speculation = currentResult.getAsJsonObject("speculation");
            
            double stcgTotal = stcg.get("total_stcg").getAsDouble();
            double ltcgTotal = ltcg.get("totalLtcg").getAsDouble();
            double speculationPL = speculation.get("profit_loss").getAsDouble();
            // Clear and rebuild results area
            resultsArea.getChildren().clear();

            Label summaryTitle = new Label("Summary");
            summaryTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

            TableView<String[]> summaryTable = new TableView<>();
            TableColumn<String[], String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
            TableColumn<String[], String> valueCol = new TableColumn<>("Amount");
            valueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[1]));
            summaryTable.getColumns().addAll(typeCol, valueCol);
            summaryTable.getItems().addAll(
                new String[]{"STCG", String.format("₹ %.2f", stcgTotal)},
                new String[]{"LTCG", String.format("₹ %.2f", ltcgTotal)},
                new String[]{"Speculation P&L", String.format("₹ %.2f", speculationPL)}
            );
            summaryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Pie chart
            PieChart pieChart = new PieChart();
            pieChart.setTitle("P&L Breakdown");
            pieChart.getData().add(new PieChart.Data("STCG", Math.max(stcgTotal, 0.01)));
            pieChart.getData().add(new PieChart.Data("LTCG", Math.max(ltcgTotal, 0.01)));
            pieChart.getData().add(new PieChart.Data("Speculation", Math.max(speculationPL, 0.01)));
            pieChart.setLabelsVisible(true);

            resultsArea.getChildren().addAll(summaryTitle, summaryTable, new Separator(), pieChart);

            // Quarterly breakdowns
            JsonArray stcgQuarters = currentResult.getAsJsonArray("stcg_quarterly_breakdown");
            JsonArray ltcgQuarters = currentResult.getAsJsonArray("ltcg_quarterly_breakdown");
            JsonArray specQuarters = currentResult.getAsJsonArray("speculation_quarterly_breakdown");

            if (stcgQuarters != null) {
                resultsArea.getChildren().addAll(
                    new Separator(),
                    buildQuarterSection("STCG by Quarter", stcgQuarters, "STCG", "stcg_amount", false)
                );
            }

            if (ltcgQuarters != null) {
                resultsArea.getChildren().addAll(
                    new Separator(),
                    buildQuarterSection("LTCG by Quarter", ltcgQuarters, "LTCG", "ltcg_amount", false)
                );
            }

            if (specQuarters != null) {
                resultsArea.getChildren().addAll(
                    new Separator(),
                    buildQuarterSection("Speculation / Intraday by Quarter", specQuarters, "Speculation P&L", "speculation_amount", true)
                );
            }

            exportBtn.setDisable(false);
            logger.info("Results displayed: STCG=" + stcgTotal + ", LTCG=" + ltcgTotal + ", Speculation=" + speculationPL);
            
        } catch (Exception e) {
            logger.error("Failed to display results: " + e.getMessage(), e);
        }
    }
    
    private void exportResults() {
        if (currentResult == null) {
            mainApp.showError("No Results", "No calculation results to export");
            return;
        }
        
        File input = mainApp.getLastUploadedFile();
        if (input == null) {
            mainApp.showError("No File", "Upload and calculate before exporting");
            return;
        }

        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Excel Report");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            chooser.setInitialFileName("tax-summary-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss")) + ".xlsx");

            File file = chooser.showSaveDialog(null);
            if (file != null) {
                exportBtn.setDisable(true);
                executor.execute(() -> {
                    try {
                        byte[] bytes = apiClient.export(input, settingsManager.getFinancialYear(), settingsManager.getQuarterScheme());
                        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                            fos.write(bytes);
                        }
                        Platform.runLater(() -> {
                            mainApp.showInfo("Export", "Report saved to: " + file.getAbsolutePath());
                            exportBtn.setDisable(false);
                        });
                        logger.info("Exported report to " + file.getAbsolutePath());
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            exportBtn.setDisable(false);
                            mainApp.showError("Export Error", "Failed to export: " + ex.getMessage());
                        });
                        logger.error("Export failed", ex);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("Export failed: " + e.getMessage(), e);
            mainApp.showError("Export Error", "Failed to export: " + e.getMessage());
        }
    }
    
    public Scene getScene() {
        return scene;
    }

    private TableView<String[]> buildQuarterTable(JsonArray quarters, String amountLabel, String amountKey, boolean includeTurnover) {
        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> qCol = new TableColumn<>("Quarter");
        qCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[0]));

        TableColumn<String[], String> periodCol = new TableColumn<>("Period");
        periodCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[1]));

        TableColumn<String[], String> amtCol = new TableColumn<>(amountLabel);
        amtCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[2]));

        TableColumn<String[], String> turnoverCol = null;
        if (includeTurnover) {
            turnoverCol = new TableColumn<>("Turnover");
            turnoverCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[3]));
        }

        TableColumn<String[], String> sellCol = new TableColumn<>("Sell");
        sellCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[includeTurnover ? 4 : 3]));

        TableColumn<String[], String> buyCol = new TableColumn<>("Buy");
        buyCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue()[includeTurnover ? 5 : 4]));

        if (includeTurnover) {
            table.getColumns().addAll(qCol, periodCol, amtCol, turnoverCol, sellCol, buyCol);
        } else {
            table.getColumns().addAll(qCol, periodCol, amtCol, sellCol, buyCol);
        }

        for (int i = 0; i < quarters.size(); i++) {
            JsonObject q = quarters.get(i).getAsJsonObject();
            String code = getStringSafe(q, "quarter_code");
            String period = buildPeriod(q);
            String amount = formatCurrency(q, amountKey);
            String sell = formatCurrency(q, "full_value_of_consideration");
            String buy = formatCurrency(q, "cost_of_acquisition");
            String turnover = includeTurnover ? formatCurrency(q, "speculation_turnover") : null;

            if (includeTurnover) {
                table.getItems().add(new String[]{code, period, amount, turnover, sell, buy});
            } else {
                table.getItems().add(new String[]{code, period, amount, sell, buy});
            }
        }

        return table;
    }

    private TitledPane buildQuarterSection(String title, JsonArray quarters, String amountLabel, String amountKey, boolean includeTurnover) {
        VBox box = new VBox(8);
        BarChart<String, Number> chart = buildQuarterChart(quarters, amountKey, amountLabel);
        TableView<String[]> table = buildQuarterTable(quarters, amountLabel, amountKey, includeTurnover);
        if (chart != null) {
            box.getChildren().add(chart);
        }
        box.getChildren().add(table);
        TitledPane pane = new TitledPane(title, box);
        pane.setExpanded(true);
        return pane;
    }

    private BarChart<String, Number> buildQuarterChart(JsonArray quarters, String amountKey, String seriesName) {
        if (quarters == null || quarters.size() == 0) {
            return null;
        }
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(seriesName + " (₹)");
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(seriesName + " by Quarter");
        chart.setLegendVisible(false);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < quarters.size(); i++) {
            JsonObject q = quarters.get(i).getAsJsonObject();
            if (!q.has(amountKey) || q.get(amountKey).isJsonNull()) {
                continue;
            }
            double val = q.get(amountKey).getAsDouble();
            String code = getStringSafe(q, "quarter_code");
            if (code.isEmpty()) {
                code = "Q" + (i + 1);
            }
            series.getData().add(new XYChart.Data<>(code, val));
        }
        chart.getData().add(series);
        chart.setCategoryGap(12);
        chart.setBarGap(6);
        chart.setPrefHeight(240);
        return chart;
    }

    private String buildPeriod(JsonObject q) {
        String start = getStringSafe(q, "start_date");
        String end = getStringSafe(q, "end_date");
        String name = getStringSafe(q, "quarter_name");
        if (!name.isEmpty()) {
            return name;
        }
        if (!start.isEmpty() || !end.isEmpty()) {
            return start + " to " + end;
        }
        return "";
    }

    private String formatCurrency(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return "-";
        }
        try {
            double val = obj.get(key).getAsDouble();
            return String.format("₹ %.2f", val);
        } catch (Exception e) {
            return "-";
        }
    }

    private String getStringSafe(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return "";
        }
        try {
            return obj.get(key).getAsString();
        } catch (Exception e) {
            return "";
        }
    }
}
