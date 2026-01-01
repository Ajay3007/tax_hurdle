/**
 * 
 */
package params;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import bootstrap.InvestingHurdleBootstrapper;
import exception.InvalidSecurityException;
import security.Security;
import util.HurdleConstant;

/**
 * @author ajay
 *
 */
public class WorkbookLoader {
	
	public static final byte INDEX_SECURITY = 1;
	public static final byte INDEX_DATE = 2;
	public static final byte INDEX_BUY_PRICE = 3;
	public static final byte INDEX_SELL_PRICE = 3;
	public static final byte INDEX_QUANTITY = 4;
	public static final byte INDEX_LTP = 5;
	
	public static final byte INDEX_BUY_SHEET = 0;
	public static final byte INDEX_SELL_SHEET = 1;
	
	private XSSFSheet buySheet;
	private XSSFSheet sellSheet;
	
	private XSSFWorkbook workbook;
	
	Security stock;

	public void initialize() throws Exception {
		try {
			FileInputStream fileInputStream = new FileInputStream(new File(HurdleConstant.CONFIGURATION_FILE_PATH));
			this.workbook = new XSSFWorkbook(fileInputStream);
			this.buySheet = this.workbook.getSheetAt(INDEX_BUY_SHEET);
			this.buySheet.removeRow(this.buySheet.getRow(0));
			this.sellSheet = this.workbook.getSheetAt(INDEX_SELL_SHEET);
			this.sellSheet.removeRow(this.sellSheet.getRow(0));
			fileInputStream.close();
			loadBuySheet();
			loadSellSheet();
			System.out.println("\nExcel workbook loader initialized SUCCESSFULLY :)\n");
			
		} catch (Exception e) {
			
		}
	}

	private void loadSellSheet() {
		Iterator<Row> rowIterator = this.sellSheet.iterator();
		String security;
		String date;
		double sellPrice;
		int quantity;
		double ltp;
		XSSFCell cell;
		int rowNum;
		
		while(rowIterator.hasNext()) {
			XSSFRow row = (XSSFRow) rowIterator.next();
			rowNum = row.getRowNum();
			
			// Extracting security from each row
			cell = row.getCell(INDEX_SECURITY);
			if(cell == null)
				throw new InvalidSecurityException("Security field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("Security field has been left empty in row [" + rowNum + "]");
			String str = cell.getStringCellValue();
			security = str.trim().toUpperCase();
			
			// Extracting date from each row
			cell = row.getCell(INDEX_DATE);
			if(cell == null)
				throw new InvalidSecurityException("Date field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("Date field has been left empty in row [" + rowNum + "]");
			str = cell.getStringCellValue();
			date = str.trim();
			
			// Extracting Buy Price from each row
			cell = row.getCell(INDEX_SELL_PRICE);
			if(cell == null)
				throw new InvalidSecurityException("Sell Price field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("Sell Price field has been left empty in row [" + rowNum + "]");
			str = cell.getStringCellValue();
			sellPrice = Double.parseDouble(str.trim());
			
			// Extracting Quantity from each row
			cell = row.getCell(INDEX_QUANTITY);
			if(cell == null)
				throw new InvalidSecurityException("Quantity field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("Quantity field has been left empty in row [" + rowNum + "]");
			str = cell.getStringCellValue();
			quantity = Integer.parseInt(str.trim());
			
			// Extracting LTP from each row
			cell = row.getCell(INDEX_LTP);
			if(cell == null)
				throw new InvalidSecurityException("LTP field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("LTP field has been left empty in row [" + rowNum + "]");
			str = cell.getStringCellValue();
			ltp = Double.parseDouble(str.trim());
			
			stock = new Security(security, false, sellPrice, quantity, date, ltp);
			
			Queue<Security> q = new LinkedList<>();
			
			if(InvestingHurdleBootstrapper.getInstance().getSecurityMap().containsKey(security)) {
				q = InvestingHurdleBootstrapper.getInstance().getSecurityMap().get(security);
				q.add(stock);
			} else {
				q.add(stock);
			}
			InvestingHurdleBootstrapper.getInstance().securityMap.put(security, q);
			
		}
	}

	private void loadBuySheet() {
		Iterator<Row> rowIterator = this.buySheet.iterator();
		String security;
		String date;
		double buyPrice;
		int quantity;
		double ltp;
		XSSFCell cell;
		int rowNum;
		
		while(rowIterator.hasNext()) {
			XSSFRow row = (XSSFRow) rowIterator.next();
			rowNum = row.getRowNum();
			
			// Extracting security from each row
			cell = row.getCell(INDEX_SECURITY);
			if(cell == null)
				throw new InvalidSecurityException("Security field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("Security field has been left empty in row [" + rowNum + "]");
			String str = cell.getStringCellValue();
			security = str.trim().toUpperCase();
			
			// Extracting date from each row
			cell = row.getCell(INDEX_DATE);
			if(cell == null)
				throw new InvalidSecurityException("Date field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("Date field has been left empty in row [" + rowNum + "]");
			str = cell.getStringCellValue();
			date = str.trim();
			
			// Extracting Buy Price from each row
			cell = row.getCell(INDEX_BUY_PRICE);
			if(cell == null)
				throw new InvalidSecurityException("Buy Price field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("Buy Price field has been left empty in row [" + rowNum + "]");
			str = cell.getStringCellValue();
			buyPrice = Double.parseDouble(str.trim());
			
			// Extracting Quantity from each row
			cell = row.getCell(INDEX_QUANTITY);
			if(cell == null)
				throw new InvalidSecurityException("Quantity field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("Quantity field has been left empty in row [" + rowNum + "]");
			str = cell.getStringCellValue();
			quantity = Integer.parseInt(str.trim());
			
			// Extracting LTP from each row
			cell = row.getCell(INDEX_LTP);
			if(cell == null)
				throw new InvalidSecurityException("LTP field has been left empty in row [" + rowNum + "]");
			cell.setCellType(CellType.STRING);
			if(cell.getStringCellValue().trim().isEmpty())
				throw new InvalidSecurityException("LTP field has been left empty in row [" + rowNum + "]");
			str = cell.getStringCellValue();
			ltp = Double.parseDouble(str.trim());
			
			stock = new Security(security, true, buyPrice, quantity, date, ltp);
			
			Queue<Security> q = new LinkedList<>();
			
			if(InvestingHurdleBootstrapper.getInstance().getSecurityMap().containsKey(security)) {
				q = InvestingHurdleBootstrapper.getInstance().getSecurityMap().get(security);
				q.add(stock);
			} else {
				q.add(stock);
			}
			InvestingHurdleBootstrapper.getInstance().securityMap.put(security, q);
			
		}
	}

}
