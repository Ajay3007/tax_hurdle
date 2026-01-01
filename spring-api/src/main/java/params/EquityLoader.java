package params;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import exception.InvalidSecurityException;
import logging.HurdleLogger;
import util.HurdleConstant;
import util.QuarterConfig;

public class EquityLoader {
	public static final byte INDEX_EQUITES = 1;
	
	public static final byte INDEX_BUYAMT = 8;
	public static final byte INDEX_SELLAMT = 12;
	public static final byte INDEX_DAYS_HOLD = 13;
	public static final byte INDEX_SELL_DATE = 9;
	
	public int START_ROW = 25;
	public int END_ROW = 297;
	
	// Intraday
	public static final byte INDEX_SPECUL = 17;
	
	// STCG
	public static final byte INDEX_STCG = 15;
	
	
	private XSSFSheet equitesSheet;
	
	private XSSFWorkbook workbook;
	
	private QuarterConfig quarterConfig;
	
	private double totalStcgBuy;
	private double totalStcgSell;
	private double totalStcg;
	private double totalIntraBuy;
	private double totalIntraSell;
	private double totalIntraTurnover;
	private double totalTurnover;
	private double stcgQ1;
	private double stcgQ2;
	private double stcgQ3;
	private double stcgQ4;
	private double stcgQ5;
	
	
	

	public void initialize() throws IOException {
		FileInputStream fileInputStream = null;
		try {
			HurdleLogger.info("Initializing Equity Loader...");
			System.out.println("Initializing Equity Loader...");
			
			// Initialize Quarter Config with default FY 2021-22
			this.quarterConfig = QuarterConfig.createFY202122();
			HurdleLogger.info("Quarter configuration initialized: " + quarterConfig);
			
			File configFile = new File(HurdleConstant.TAX_CONFIG_FILE_PATH);
			if (!configFile.exists()) {
				throw new InvalidSecurityException(InvalidSecurityException.ErrorCode.MISSING_FILE,
					"Configuration file not found: " + HurdleConstant.TAX_CONFIG_FILE_PATH);
			}
			
			fileInputStream = new FileInputStream(configFile);
			this.workbook = new XSSFWorkbook(fileInputStream);
			
			this.equitesSheet = this.workbook.getSheetAt(INDEX_EQUITES);
			if (this.equitesSheet == null) {
				throw new InvalidSecurityException(InvalidSecurityException.ErrorCode.INVALID_DATA,
					"Equities sheet not found at index: " + INDEX_EQUITES);
			}
			
			loadEquities();
			HurdleLogger.info("Equity loader initialized successfully");
			System.out.println("\nEquity loader initialized SUCCESSFULLY :)\n");
			
		} catch (InvalidSecurityException e) {
			HurdleLogger.error("Security exception during initialization: " + e.getMessage(), e);
			throw e;
		} catch (IOException e) {
			HurdleLogger.error("IO exception during initialization: " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			HurdleLogger.error("Unexpected exception during initialization: " + e.getMessage(), e);
			throw new InvalidSecurityException(InvalidSecurityException.ErrorCode.PARSE_ERROR,
				"Failed to initialize equity loader: " + e.getMessage(), e);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					HurdleLogger.warn("Failed to close file input stream: " + e.getMessage());
				}
			}
		}
	}

	private void loadEquities() {
		// TODO Auto-generated method stub
		Iterator<Row> rowIterator = this.equitesSheet.iterator();
		int days;
		double buyValue;
		double sellValue;
		double totalBuySTCG = 0;
		double totalSellSTCG = 0;
		double totalBuyINTRA = 0;
		double totalSellINTRA = 0;
		double stcg;
		double stcg1 = 0;
		double stcg2 = 0;
		double stcg3 = 0;
		double stcg4 = 0;
		double stcg5 = 0;
		double intraTurnover;
		double totalSTCG = 0;
		double totalIntraTurnover = 0;
		XSSFCell cell;
		int rowNum;
		int i=0;
		while(rowIterator.hasNext()) {
			XSSFRow row = (XSSFRow) rowIterator.next();
			rowNum = row.getRowNum();
			if(rowNum < START_ROW - 1) {
				continue;
			}
			//System.out.println("row num : " + rowNum);
			if(rowNum == END_ROW-1) {
				break;
			}
			String str = null; 
			String DDMMYY[];
			
			cell = row.getCell(INDEX_BUYAMT);
			str = cell.getStringCellValue();
			buyValue = Double.parseDouble(str.trim());
			
			cell = row.getCell(INDEX_SELLAMT);
			str = cell.getStringCellValue();
			sellValue = Double.parseDouble(str.trim());
			
			cell = row.getCell(INDEX_DAYS_HOLD);
			str = cell.getStringCellValue();
			days = Integer.parseInt(str.trim());
			if(days!=0) {
				cell = row.getCell(INDEX_STCG);
				str = cell.getStringCellValue();
				stcg = Double.parseDouble(str.trim());
				cell = row.getCell(INDEX_SELL_DATE);
				str = cell.getStringCellValue();
				DDMMYY = str.split("/");
				int DD = Integer.parseInt(DDMMYY[0]);
				int MM = Integer.parseInt(DDMMYY[1]);
				int YY = Integer.parseInt(DDMMYY[2]);
				LocalDate date = LocalDate.of(YY, MM, DD);
				int quarter = returnQuarter(date, stcg);
				if(quarter == 1) {
					stcg1 += stcg;
				} else if(quarter == 2) {
					stcg2 += stcg;
				} else if(quarter == 3) {
					stcg3 += stcg;
				} else if(quarter == 4) {
					stcg4 += stcg;
				} else if(quarter == 5) {
					stcg5 += stcg;
				}
				totalBuySTCG += buyValue;
				totalSellSTCG += sellValue;
				totalSTCG += stcg;
			} else {
				cell = row.getCell(INDEX_SPECUL);
				str = cell.getStringCellValue();
				intraTurnover = Double.parseDouble(str.trim());
				if(intraTurnover < 0.0) {
					intraTurnover = intraTurnover * (-1);
				}
				totalBuyINTRA += buyValue;
				totalSellINTRA += sellValue;
				totalIntraTurnover += intraTurnover;
			}
			//System.out.println("buy : " + buyValue);
		}
		
		
		this.totalIntraBuy = totalBuyINTRA;
		this.totalIntraSell = totalSellINTRA;
		this.totalIntraTurnover = totalIntraTurnover;
		this.totalStcgBuy = totalBuySTCG;
		this.totalStcgSell = totalSellSTCG;
		this.totalStcg = totalSTCG;
		this.totalTurnover = totalIntraTurnover + totalSellSTCG;
		this.stcgQ1 = stcg1;
		this.stcgQ2 = stcg2;
		this.stcgQ3 = stcg3;
		this.stcgQ4 = stcg4;
		this.stcgQ5 = stcg5;
		
	}
	
	private int returnQuarter(LocalDate date, double stcg) {
		if (date == null || quarterConfig == null) {
			return 0;
		}
		return quarterConfig.getQuarterNumber(date);
	}
	
	public double getStcgQ1() {
		return stcgQ1;
	}

	public void setStcgQ1(double stcgQ1) {
		this.stcgQ1 = stcgQ1;
	}

	public double getStcgQ2() {
		return stcgQ2;
	}

	public void setStcgQ2(double stcgQ2) {
		this.stcgQ2 = stcgQ2;
	}

	public double getStcgQ3() {
		return stcgQ3;
	}

	public void setStcgQ3(double stcgQ3) {
		this.stcgQ3 = stcgQ3;
	}

	public double getStcgQ4() {
		return stcgQ4;
	}

	public void setStcgQ4(double stcgQ4) {
		this.stcgQ4 = stcgQ4;
	}

	public double getStcgQ5() {
		return stcgQ5;
	}

	public void setStcgQ5(double stcgQ5) {
		this.stcgQ5 = stcgQ5;
	}

	public double getTotalStcgBuy() {
		return totalStcgBuy;
	}

	public void setTotalStcgBuy(double totalStcgBuy) {
		this.totalStcgBuy = totalStcgBuy;
	}

	public double getTotalStcgSell() {
		return totalStcgSell;
	}

	public void setTotalStcgSell(double totalStcgSell) {
		this.totalStcgSell = totalStcgSell;
	}

	public double getTotalStcg() {
		return totalStcg;
	}

	public void setTotalStcg(double totalStcg) {
		this.totalStcg = totalStcg;
	}

	public double getTotalIntraBuy() {
		return totalIntraBuy;
	}

	public void setTotalIntraBuy(double totalIntraBuy) {
		this.totalIntraBuy = totalIntraBuy;
	}

	public double getTotalIntraSell() {
		return totalIntraSell;
	}

	public void setTotalIntraSell(double totalIntraSell) {
		this.totalIntraSell = totalIntraSell;
	}

	public double getTotalIntraTurnover() {
		return totalIntraTurnover;
	}

	public void setTotalIntraTurnover(double totalIntraTurnover) {
		this.totalIntraTurnover = totalIntraTurnover;
	}

	public double getTotalTurnover() {
		return totalTurnover;
	}

	public void setTotalTurnover(double totalTurnover) {
		this.totalTurnover = totalTurnover;
	}
	
	public QuarterConfig getQuarterConfig() {
		return quarterConfig;
	}
	
	public void setQuarterConfig(QuarterConfig quarterConfig) {
		this.quarterConfig = quarterConfig;
	}

}
