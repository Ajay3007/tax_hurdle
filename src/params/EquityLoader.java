package params;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import util.HurdleConstant;

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
	
	
	

	public void initialize() throws Exception {
		try {
			System.out.println("Initializing Equity Loader...");
			FileInputStream fileInputStream = new FileInputStream(new File(HurdleConstant.TAX_CONFIG_FILE_PATH));
			this.workbook = new XSSFWorkbook(fileInputStream);
			this.equitesSheet = this.workbook.getSheetAt(INDEX_EQUITES);
//			for(int i=0; i<START_ROW-1; i++) {
//				System.out.println("removing row  " + i+1);
//				this.equitesSheet.removeRow(this.equitesSheet.getRow(0));
//			}
			fileInputStream.close();
			loadEquities();
			System.out.println("\nEquity loader initialized SUCCESSFULLY :)\n");
			
		} catch (Exception e) {
			// TODO: handle exception
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
				Date date = new Date(YY, MM, DD);
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
	
	private int returnQuarter(Date date, double stcg) {
		Date d1 = new Date(2021, 4, 1);
		Date d2 = new Date(2021, 6, 15);
		Date d3 = new Date(2021, 6, 16);
		Date d4 = new Date(2021, 9, 15);
		Date d5 = new Date(2021, 9, 16);
		Date d6 = new Date(2021, 12, 15);
		Date d7 = new Date(2021, 12, 16);
		Date d8 = new Date(2022, 3, 15);
		Date d9 = new Date(2022, 3, 16);
		Date d10 = new Date(2022, 3, 31);
		if(date.equals(d1) || date.equals(d2) || date.after(d1) && date.before(d2)) {
			return 1;
		} else if(date.equals(d3) || date.equals(d4) || date.after(d3) && date.before(d4)) {
			return 2;
		} else if(date.equals(d5) || date.equals(d6) || date.after(d5) && date.before(d6)) {
			return 3;
		} else if(date.equals(d7) || date.equals(d8) || date.after(d7) && date.before(d8)) {
			return 4;
		} else if(date.equals(d9) || date.equals(d10) || date.after(d9) && date.before(d10)) {
			return 5;
		}
		
		return 0;
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

}
