/**
 * 
 */
package bootstrap;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import params.EquityLoader;
import params.WorkbookLoader;
import security.Security;

/**
 * @author ajay
 *
 */
public class InvestingHurdleBootstrapper {
	
	private static InvestingHurdleBootstrapper INSTANCE = new InvestingHurdleBootstrapper();
	private WorkbookLoader workbookLoader;
	private EquityLoader equityLoader;
	private static Logger logger;
	
	public ConcurrentMap<String, Queue<Security>> securityMap = new ConcurrentHashMap<>();

	public ConcurrentMap<String, Queue<Security>> getSecurityMap() {
		return securityMap;
	}


	public void setSecurityMap(ConcurrentMap<String, Queue<Security>> securityMap) {
		INSTANCE.securityMap = securityMap;
	}

	private InvestingHurdleBootstrapper() {
		
	}
	
	public static InvestingHurdleBootstrapper getInstance() {
		return INSTANCE;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("***************** WELCOME TO THE INVESTING WORLD... ********************");
		
		//INSTANCE.initWorkbookLoader();
		
		//printSecurities();
		
		INSTANCE.initEquityLoader();
		
		INSTANCE.printEquityDetails();
		
		System.out.println("\n\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-* END *-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
		
	}


	public void printEquityDetails() {
		// TODO Auto-generated method stub
		//this.equityLoader = new EquityLoader();
		System.out.println("$$$$$$$$$********  STCG  ********$$$$$$$$$\n");
		System.out.println("Full Value of consideration : " + this.equityLoader.getTotalStcgSell());
		System.out.println("Cost of acquisition : " + this.equityLoader.getTotalStcgBuy());
		System.out.println("STCG = " + (this.equityLoader.getTotalStcgSell()-this.equityLoader.getTotalStcgBuy()));
		System.out.println("STCG total : " + this.equityLoader.getTotalStcg());
		System.out.println("STCG Q1 = " + this.equityLoader.getStcgQ1());
		System.out.println("STCG Q2 = " + this.equityLoader.getStcgQ2());
		System.out.println("STCG Q3 = " + this.equityLoader.getStcgQ3());
		System.out.println("STCG Q4 = " + this.equityLoader.getStcgQ4());
		System.out.println("STCG Q5 = " + this.equityLoader.getStcgQ5());
		
		System.out.println("\n$$$$$$$$$********  SPECULATION  ********$$$$$$$$$\n");
		System.out.println("Full Value of consideration : " + this.equityLoader.getTotalIntraSell());
		System.out.println("Cost of acquisition : " + this.equityLoader.getTotalIntraBuy());
		System.out.println("PL = " + (this.equityLoader.getTotalIntraSell()-this.equityLoader.getTotalIntraBuy()));
		System.out.println("Turnover total intraday : " + this.equityLoader.getTotalIntraTurnover());
	}


	private void initEquityLoader() {
		// TODO Auto-generated method stub
		this.equityLoader = new EquityLoader();
		try {
			this.equityLoader.initialize();
		} catch (Exception e) {
			System.out.println("Exception in initialization of Equity loader...\n");
			e.printStackTrace();
		}
	}


	private static void printSecurities() {
		//INSTANCE.getLogger().info("Into printSecurity method...");
		LogManager.getRootLogger().info("Into printSecurity method...");
		Iterator<Entry<String, Queue<Security>>> itr = INSTANCE.securityMap.entrySet().iterator();
		Queue<Security> q;
		while(itr.hasNext()) {
			Entry<String, Queue<Security>> entry = itr.next();
			q = entry.getValue();
			
			setAveragePrices();
			
			Iterator<Security> iterator = q.iterator();
			Security stock = null;
			System.out.println("Key = " + entry.getKey() + ", Value = ");
			
			while(iterator.hasNext()) {
				stock = iterator.next();
				System.out.println(stock.toString() + "\n");
			}
			System.out.println("\n");
		}
	}


	private static void setAveragePrices() {
		
	}


	private void initWorkbookLoader() {
		this.workbookLoader = new WorkbookLoader();
		try {
			this.workbookLoader.initialize();
		} catch (Exception e) {
			System.out.println("Exception in initialization of workbook loader...\n");
			e.printStackTrace();
		}
	}
	
	public Logger getLogger() {
		return logger;
	}

}
