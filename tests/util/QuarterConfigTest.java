/**
 * 
 */
package util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for QuarterConfig
 * @author ajay
 *
 */
@DisplayName("Quarter Configuration Tests")
class QuarterConfigTest {
	
	private QuarterConfig fy202122;
	private QuarterConfig fy202223;
	
	@BeforeEach
	void setUp() {
		fy202122 = QuarterConfig.createFY202122();
		fy202223 = QuarterConfig.createFY202223();
	}
	
	@Test
	@DisplayName("FY 2021-22 creates 5 quarters")
	void testFY202122QuarterCount() {
		List<Quarter> quarters = fy202122.getQuarters();
		assertEquals(5, quarters.size());
	}
	
	@Test
	@DisplayName("FY 2022-23 creates 5 quarters")
	void testFY202223QuarterCount() {
		List<Quarter> quarters = fy202223.getQuarters();
		assertEquals(5, quarters.size());
	}
	
	@Test
	@DisplayName("Q1 detection for FY 2021-22")
	void testQ1DetectionFY202122() {
		LocalDate q1Date = LocalDate.of(2021, 4, 15);
		int quarter = fy202122.getQuarterNumber(q1Date);
		assertEquals(1, quarter);
	}
	
	@Test
	@DisplayName("Q2 detection for FY 2021-22")
	void testQ2DetectionFY202122() {
		LocalDate q2Date = LocalDate.of(2021, 7, 15);
		int quarter = fy202122.getQuarterNumber(q2Date);
		assertEquals(2, quarter);
	}
	
	@Test
	@DisplayName("Q3 detection for FY 2021-22")
	void testQ3DetectionFY202122() {
		LocalDate q3Date = LocalDate.of(2021, 10, 15);
		int quarter = fy202122.getQuarterNumber(q3Date);
		assertEquals(3, quarter);
	}
	
	@Test
	@DisplayName("Q4 detection for FY 2021-22")
	void testQ4DetectionFY202122() {
		LocalDate q4Date = LocalDate.of(2021, 12, 20);
		int quarter = fy202122.getQuarterNumber(q4Date);
		assertEquals(4, quarter);
	}
	
	@Test
	@DisplayName("Q5 detection for FY 2021-22")
	void testQ5DetectionFY202122() {
		LocalDate q5Date = LocalDate.of(2022, 3, 25);
		int quarter = fy202122.getQuarterNumber(q5Date);
		assertEquals(5, quarter);
	}
	
	@Test
	@DisplayName("Date at Q1 start boundary")
	void testQ1StartBoundary() {
		LocalDate q1Start = LocalDate.of(2021, 4, 1);
		int quarter = fy202122.getQuarterNumber(q1Start);
		assertEquals(1, quarter);
	}
	
	@Test
	@DisplayName("Date at Q1 end boundary")
	void testQ1EndBoundary() {
		LocalDate q1End = LocalDate.of(2021, 6, 15);
		int quarter = fy202122.getQuarterNumber(q1End);
		assertEquals(1, quarter);
	}
	
	@Test
	@DisplayName("Q5 fiscal year end")
	void testQ5FiscalYearEnd() {
		LocalDate fiscalEnd = LocalDate.of(2022, 3, 31);
		int quarter = fy202122.getQuarterNumber(fiscalEnd);
		assertEquals(5, quarter);
	}
	
	@Test
	@DisplayName("Quarter name retrieval")
	void testQuarterName() {
		Quarter q1 = fy202122.getQuarters().get(0);
		assertEquals("Q1", q1.getName());
	}
	
	@Test
	@DisplayName("Date outside fiscal year range")
	void testOutsideFiscalYear() {
		LocalDate outsideDate = LocalDate.of(2020, 3, 31);
		int quarter = fy202122.getQuarterNumber(outsideDate);
		assertEquals(-1, quarter); // Or throw exception based on implementation
	}
	
	@Test
	@DisplayName("Create custom quarter config")
	void testCreateCustomQuarters() {
		QuarterConfig customConfig = new QuarterConfig("Custom");
		Quarter customQ = new Quarter("CQ1", "CustomQ", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 3, 31));
		customConfig.addQuarter(customQ);
		assertEquals(1, customConfig.getQuarters().size());
	}
	
	@Test
	@DisplayName("FY 2022-23 Q1 date detection")
	void testFY202223Q1() {
		LocalDate q1Date = LocalDate.of(2022, 4, 15);
		int quarter = fy202223.getQuarterNumber(q1Date);
		assertEquals(1, quarter);
	}
	
	@Test
	@DisplayName("Consistent quarter detection across years")
	void testConsistentDetectionPattern() {
		LocalDate date = LocalDate.of(2021, 6, 10);
		int q1 = fy202122.getQuarterNumber(date);
		
		LocalDate date2 = LocalDate.of(2022, 6, 10);
		int q2 = fy202223.getQuarterNumber(date2);
		
		assertEquals(q1, q2);
	}
}
