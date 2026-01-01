package util;

import com.investinghurdle.api.dto.LtcgResponse;
import com.investinghurdle.api.dto.QuarterDetailResponse;
import com.investinghurdle.api.dto.SpeculationResponse;
import com.investinghurdle.api.dto.StcgResponse;
import com.investinghurdle.api.dto.TaxCalculationResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import logging.HurdleLogger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Exports TaxCalculationResponse to an Excel workbook with summary and quarterly breakdowns.
 */
public class ExcelSummaryExporter {

    public void export(File outputFile, TaxCalculationResponse response) throws IOException {
        HurdleLogger.info("Exporting Excel summary to: " + outputFile.getAbsolutePath());
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = buildHeaderStyle(workbook);
            CellStyle moneyStyle = buildMoneyStyle(workbook);
            CellStyle dateStyle = buildDateStyle(workbook);
            CellStyle greenStyle = buildColorStyle(workbook, IndexedColors.LIGHT_GREEN);
            CellStyle redStyle = buildColorStyle(workbook, IndexedColors.ROSE);

            buildSummarySheet(workbook, response, headerStyle, moneyStyle, greenStyle, redStyle);
            buildQuarterSheet(workbook, "STCG Quarterly", response.getStcgQuarterlyBreakdown(), true, false, false,
                    headerStyle, moneyStyle, dateStyle, greenStyle, redStyle);
            buildQuarterSheet(workbook, "LTCG Quarterly", response.getLtcgQuarterlyBreakdown(), false, true, false,
                    headerStyle, moneyStyle, dateStyle, greenStyle, redStyle);
            buildQuarterSheet(workbook, "Speculation Quarterly", response.getSpeculationQuarterlyBreakdown(), false, false, true,
                    headerStyle, moneyStyle, dateStyle, greenStyle, redStyle);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }
        }
    }

    private void buildSummarySheet(Workbook wb, TaxCalculationResponse resp,
                                   CellStyle header, CellStyle money,
                                   CellStyle green, CellStyle red) {
        Sheet sheet = wb.createSheet("Summary");
        int rowIdx = 0;

        // Title row
        Row title = sheet.createRow(rowIdx++);
        Cell titleCell = title.createCell(0);
        titleCell.setCellValue("InvestingHurdle - Tax Summary");

        // Financial year
        Row fy = sheet.createRow(rowIdx++);
        fy.createCell(0).setCellValue("Financial Year");
        fy.createCell(1).setCellValue(resp.getFinancialYear());

        // Broker info
        Row br = sheet.createRow(rowIdx++);
        br.createCell(0).setCellValue("Broker");
        br.createCell(1).setCellValue(resp.getBrokerName() != null ? resp.getBrokerName() : "");
        br.createCell(2).setCellValue(resp.getBrokerType() != null ? resp.getBrokerType() : "");

        rowIdx++; // spacer

        // STCG
        rowIdx = writeSectionHeader(sheet, rowIdx, "Short-Term Capital Gains (STCG)", header);
        rowIdx = writeKeyValue(sheet, rowIdx, "Full Value of Consideration", resp.getStcg().getFullValueOfConsideration(), money);
        rowIdx = writeKeyValue(sheet, rowIdx, "Cost of Acquisition", resp.getStcg().getCostOfAcquisition(), money);
        rowIdx = writeKeyValueWithColor(sheet, rowIdx, "Total STCG", resp.getStcg().getTotalStcg(), money, green, red, resp.getStcg().isPositive());

        rowIdx++; // spacer

        // LTCG
        LtcgResponse ltcg = resp.getLtcg();
        rowIdx = writeSectionHeader(sheet, rowIdx, "Long-Term Capital Gains (LTCG)", header);
        rowIdx = writeKeyValue(sheet, rowIdx, "Full Value of Consideration", ltcg.getFullValueOfConsideration(), money);
        rowIdx = writeKeyValue(sheet, rowIdx, "Cost of Acquisition", ltcg.getCostOfAcquisition(), money);
        rowIdx = writeKeyValueWithColor(sheet, rowIdx, "Total LTCG", ltcg.getTotalLtcg(), money, green, red, ltcg.isPositive());
        rowIdx = writeKeyValue(sheet, rowIdx, "Exemption Limit", ltcg.getExemptionLimit(), money);
        rowIdx = writeKeyValue(sheet, rowIdx, "Taxable LTCG", ltcg.getTaxableLtcg(), money);

        rowIdx++; // spacer

        // Speculation
        SpeculationResponse spec = resp.getSpeculation();
        rowIdx = writeSectionHeader(sheet, rowIdx, "Speculation / Intraday", header);
        rowIdx = writeKeyValue(sheet, rowIdx, "Full Value of Consideration", spec.getFullValueOfConsideration(), money);
        rowIdx = writeKeyValue(sheet, rowIdx, "Cost of Acquisition", spec.getCostOfAcquisition(), money);
        rowIdx = writeKeyValueWithColor(sheet, rowIdx, "Profit / Loss", spec.getProfitLoss(), money, green, red, spec.isPositive());
        rowIdx = writeKeyValue(sheet, rowIdx, "Total Turnover", spec.getTotalTurnover(), money);

        // Autosize relevant columns
        for (int c = 0; c < 2; c++) {
            sheet.autoSizeColumn(c);
        }
    }

    private void buildQuarterSheet(Workbook wb, String sheetName, List<QuarterDetailResponse> quarters,
                                   boolean includeStcg, boolean includeLtcg, boolean includeSpec,
                                   CellStyle header, CellStyle money, CellStyle date,
                                   CellStyle green, CellStyle red) {
        Sheet sheet = wb.createSheet(sheetName);
        int rowIdx = 0;

        Row h = sheet.createRow(rowIdx++);
        int col = 0;
        setHeaderCell(h, col++, "Quarter", header);
        setHeaderCell(h, col++, "Start Date", header);
        setHeaderCell(h, col++, "End Date", header);
        if (includeStcg) setHeaderCell(h, col++, "STCG Amount", header);
        if (includeLtcg) setHeaderCell(h, col++, "LTCG Amount", header);
        if (includeSpec) {
            setHeaderCell(h, col++, "Speculation Amount", header);
            setHeaderCell(h, col++, "Speculation Turnover", header);
        }
        setHeaderCell(h, col++, "Full Value of Consideration", header);
        setHeaderCell(h, col++, "Cost of Acquisition", header);
        setHeaderCell(h, col++, "Display Color", header);

        if (quarters != null) {
            for (QuarterDetailResponse q : quarters) {
                Row r = sheet.createRow(rowIdx++);
                int c = 0;
                r.createCell(c++).setCellValue(q.getQuarterCode());

                Cell start = r.createCell(c++);
                start.setCellValue(q.getStartDate());
                start.setCellStyle(date);

                Cell end = r.createCell(c++);
                end.setCellValue(q.getEndDate());
                end.setCellStyle(date);

                if (includeStcg) {
                    Cell st = r.createCell(c++);
                    st.setCellValue(nullSafe(q.getStcgAmount()));
                    st.setCellStyle(money);
                    st.setCellStyle(q.getPositive() != null && q.getPositive() ? green : red);
                }
                if (includeLtcg) {
                    Cell lt = r.createCell(c++);
                    lt.setCellValue(nullSafe(q.getLtcgAmount()));
                    lt.setCellStyle(money);
                    lt.setCellStyle(q.getPositive() != null && q.getPositive() ? green : red);
                }
                if (includeSpec) {
                    Cell sp = r.createCell(c++);
                    sp.setCellValue(nullSafe(q.getSpeculationAmount()));
                    sp.setCellStyle(money);
                    sp.setCellStyle(q.getPositive() != null && q.getPositive() ? green : red);

                    Cell to = r.createCell(c++);
                    to.setCellValue(nullSafe(q.getSpeculationTurnover()));
                    to.setCellStyle(money);
                }

                Cell sell = r.createCell(c++);
                sell.setCellValue(nullSafe(q.getFullValueOfConsideration()));
                sell.setCellStyle(money);

                Cell buy = r.createCell(c++);
                buy.setCellValue(nullSafe(q.getCostOfAcquisition()));
                buy.setCellStyle(money);

                r.createCell(c).setCellValue(q.getDisplayColor() != null ? q.getDisplayColor() : "");
            }
        }

        for (int c = 0; c < col + 2; c++) {
            sheet.autoSizeColumn(c);
        }
    }

    private int writeSectionHeader(Sheet sheet, int rowIdx, String text, CellStyle header) {
        Row r = sheet.createRow(rowIdx++);
        Cell c = r.createCell(0);
        c.setCellValue(text);
        c.setCellStyle(header);
        return rowIdx;
    }

    private int writeKeyValue(Sheet sheet, int rowIdx, String key, double value, CellStyle money) {
        Row r = sheet.createRow(rowIdx++);
        r.createCell(0).setCellValue(key);
        Cell v = r.createCell(1);
        v.setCellValue(value);
        v.setCellStyle(money);
        return rowIdx;
    }

    private int writeKeyValueWithColor(Sheet sheet, int rowIdx, String key, double value, CellStyle money,
                                       CellStyle green, CellStyle red, boolean positive) {
        Row r = sheet.createRow(rowIdx++);
        r.createCell(0).setCellValue(key);
        Cell v = r.createCell(1);
        v.setCellValue(value);
        v.setCellStyle(money);
        v.setCellStyle(positive ? green : red);
        return rowIdx;
    }

    private void setHeaderCell(Row row, int idx, String text, CellStyle style) {
        Cell cell = row.createCell(idx);
        cell.setCellValue(text);
        cell.setCellStyle(style);
    }

    private CellStyle buildHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private CellStyle buildMoneyStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private CellStyle buildDateStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(wb.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }

    private CellStyle buildColorStyle(Workbook wb, IndexedColors color) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private double nullSafe(Double v) {
        return v == null ? 0.0 : v;
    }
}
