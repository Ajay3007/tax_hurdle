/**
 * 
 */
package util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Unit tests for FIFOCalculator
 * @author ajay
 *
 */
@DisplayName("FIFO Calculator Tests")
class FIFOCalculatorTest {
	
	private FIFOCalculator calculator;
	private static final String TEST_SYMBOL = "INFY";
	
	@BeforeEach
	void setUp() {
		calculator = new FIFOCalculator(TEST_SYMBOL);
	}
	
	@Test
	@DisplayName("Test simple FIFO allocation")
	void testSimpleFIFOAllocation() {
		// Buy 10 shares at 1500
		calculator.addBuyOrder(LocalDate.of(2021, 4, 1), 10, 15000);
		
		// Sell 5 shares at 1510
		FIFOAllocation allocation = calculator.calculateCostBasis(
			LocalDate.of(2021, 4, 5), 5, 7550);
		
		assertTrue(allocation != null);
		assertEquals(5, allocation.getSellQuantity());
		assertEquals(7550, allocation.getSellAmount());
		assertEquals(7500, allocation.getTotalCostOfAcquisition());
		assertEquals(50, allocation.getProfitOrLoss());
		assertEquals(1, allocation.getNumberOfBuysMatched());
	}
	
	@Test
	@DisplayName("Test partial fill with remaining inventory")
	void testPartialFillWithRemainingInventory() {
		// Buy 10 shares at 1500
		calculator.addBuyOrder(LocalDate.of(2021, 4, 1), 10, 15000);
		
		// Sell 3 shares
		FIFOAllocation allocation = calculator.calculateCostBasis(
			LocalDate.of(2021, 4, 5), 3, 4530);
		
		assertEquals(3, allocation.getSellQuantity());
		assertEquals(4500, allocation.getTotalCostOfAcquisition());
		assertEquals(30, allocation.getProfitOrLoss());
		
		// Verify remaining inventory
		assertEquals(7, calculator.getTotalPendingBuysQuantity());
		assertEquals(10500, calculator.getTotalPendingBuysCost());
	}
	
	@Test
	@DisplayName("Test multiple buy orders matched")
	void testMultipleBuyOrdersMatched() {
		// Buy 5 at 1500 on day 1
		calculator.addBuyOrder(LocalDate.of(2021, 4, 1), 5, 7500);
		
		// Buy 5 at 1600 on day 2
		calculator.addBuyOrder(LocalDate.of(2021, 4, 2), 5, 8000);
		
		// Sell 8 shares (will use both buy orders)
		FIFOAllocation allocation = calculator.calculateCostBasis(
			LocalDate.of(2021, 4, 5), 8, 12800);
		
		assertEquals(8, allocation.getSellQuantity());
		assertEquals(2, allocation.getNumberOfBuysMatched());
		assertEquals(15500, allocation.getTotalCostOfAcquisition()); // 7500 + 8000
		assertEquals(-2700, allocation.getProfitOrLoss()); // 12800 - 15500
		
		// Verify 2 remaining shares from second buy
		assertEquals(2, calculator.getTotalPendingBuysQuantity());
	}
	
	@Test
	@DisplayName("Test complete inventory depletion")
	void testCompleteInventoryDepletion() {
		calculator.addBuyOrder(LocalDate.of(2021, 4, 1), 10, 15000);
		
		// Sell all 10 shares
		FIFOAllocation allocation = calculator.calculateCostBasis(
			LocalDate.of(2021, 4, 5), 10, 15500);
		
		assertEquals(0, calculator.getTotalPendingBuysQuantity());
		assertEquals(500, allocation.getProfitOrLoss());
	}
	
	@Test
	@DisplayName("Test invalid quantities")
	void testInvalidQuantities() {
		// Zero quantity should be skipped
		calculator.addBuyOrder(LocalDate.of(2021, 4, 1), 0, 0);
		
		assertEquals(0, calculator.getPendingBuyOrderCount());
		
		// Negative quantity should be skipped
		calculator.addBuyOrder(LocalDate.of(2021, 4, 1), -5, -7500);
		
		assertEquals(0, calculator.getPendingBuyOrderCount());
	}
	
	@Test
	@DisplayName("Test holding days calculation")
	void testHoldingDaysCalculation() {
		LocalDate buyDate = LocalDate.of(2021, 4, 1);
		LocalDate sellDate = LocalDate.of(2021, 4, 8);
		
		calculator.addBuyOrder(buyDate, 5, 7500);
		FIFOAllocation allocation = calculator.calculateCostBasis(sellDate, 5, 7500);
		
		BuyOrderMatch match = allocation.getMatches().get(0);
		assertEquals(7, match.getHoldingDays()); // 7 days difference
	}
	
	@Test
	@DisplayName("Test unit cost calculation")
	void testUnitCostCalculation() {
		// 100 shares at 1000 = 100000
		calculator.addBuyOrder(LocalDate.of(2021, 4, 1), 100, 100000);
		
		FIFOAllocation allocation = calculator.calculateCostBasis(
			LocalDate.of(2021, 4, 5), 50, 51500);
		
		BuyOrderMatch match = allocation.getMatches().get(0);
		assertEquals(1000, match.getUnitCost());
		assertEquals(50000, match.getCostOfAcquisition());
	}
	
	@Test
	@DisplayName("Test reset functionality")
	void testReset() {
		calculator.addBuyOrder(LocalDate.of(2021, 4, 1), 10, 15000);
		calculator.calculateCostBasis(LocalDate.of(2021, 4, 5), 5, 7550);
		
		assertEquals(1, calculator.getPendingBuyOrderCount());
		assertEquals(1, calculator.getCompletedAllocations().size());
		
		calculator.reset();
		
		assertEquals(0, calculator.getPendingBuyOrderCount());
		assertEquals(0, calculator.getCompletedAllocations().size());
	}
}
