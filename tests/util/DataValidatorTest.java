/**
 * 
 */
package util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Unit tests for DataValidator
 * @author ajay
 *
 */
@DisplayName("Data Validator Tests")
class DataValidatorTest {
	
	@Test
	@DisplayName("Valid date in dd/MM/yyyy format")
	void testValidDate() {
		DataValidator.ValidationResult result = DataValidator.validateDate("15/06/2021");
		assertTrue(result.isValid());
	}
	
	@Test
	@DisplayName("Invalid date format")
	void testInvalidDateFormat() {
		DataValidator.ValidationResult result = DataValidator.validateDate("2021-06-15");
		assertFalse(result.isValid());
		assertEquals(DataValidator.ValidationError.INVALID_DATE, result.getError());
	}
	
	@Test
	@DisplayName("Day out of range")
	void testDayOutOfRange() {
		DataValidator.ValidationResult result = DataValidator.validateDate("32/06/2021");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Month out of range")
	void testMonthOutOfRange() {
		DataValidator.ValidationResult result = DataValidator.validateDate("15/13/2021");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Null date string")
	void testNullDate() {
		DataValidator.ValidationResult result = DataValidator.validateDate(null);
		assertFalse(result.isValid());
		assertEquals(DataValidator.ValidationError.MISSING_VALUE, result.getError());
	}
	
	@Test
	@DisplayName("Valid positive amount")
	void testValidAmount() {
		DataValidator.ValidationResult result = DataValidator.validateAmount("1500.50");
		assertTrue(result.isValid());
	}
	
	@Test
	@DisplayName("Negative amount")
	void testNegativeAmount() {
		DataValidator.ValidationResult result = DataValidator.validateAmount("-500");
		assertFalse(result.isValid());
		assertEquals(DataValidator.ValidationError.INVALID_AMOUNT, result.getError());
	}
	
	@Test
	@DisplayName("Non-numeric amount")
	void testNonNumericAmount() {
		DataValidator.ValidationResult result = DataValidator.validateAmount("abc");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Valid positive quantity")
	void testValidQuantity() {
		DataValidator.ValidationResult result = DataValidator.validateQuantity("10");
		assertTrue(result.isValid());
	}
	
	@Test
	@DisplayName("Zero quantity")
	void testZeroQuantity() {
		DataValidator.ValidationResult result = DataValidator.validateQuantity("0");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Valid holding days")
	void testValidHoldingDays() {
		DataValidator.ValidationResult result = DataValidator.validateHoldingDays("0");
		assertTrue(result.isValid());
		
		result = DataValidator.validateHoldingDays("365");
		assertTrue(result.isValid());
	}
	
	@Test
	@DisplayName("Negative holding days")
	void testNegativeHoldingDays() {
		DataValidator.ValidationResult result = DataValidator.validateHoldingDays("-1");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Invalid holding days format")
	void testInvalidHoldingDaysFormat() {
		DataValidator.ValidationResult result = DataValidator.validateHoldingDays("abc");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Valid stock symbol")
	void testValidSymbol() {
		DataValidator.ValidationResult result = DataValidator.validateSymbol("INFY");
		assertTrue(result.isValid());
		
		result = DataValidator.validateSymbol("TCS-BE");
		assertTrue(result.isValid());
	}
	
	@Test
	@DisplayName("Empty symbol")
	void testEmptySymbol() {
		DataValidator.ValidationResult result = DataValidator.validateSymbol("");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Valid price")
	void testValidPrice() {
		DataValidator.ValidationResult result = DataValidator.validatePrice("1500.75");
		assertTrue(result.isValid());
	}
	
	@Test
	@DisplayName("Zero price")
	void testZeroPrice() {
		DataValidator.ValidationResult result = DataValidator.validatePrice("0");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Negative price")
	void testNegativePrice() {
		DataValidator.ValidationResult result = DataValidator.validatePrice("-100");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Value in range")
	void testValueInRange() {
		DataValidator.ValidationResult result = DataValidator.validateInRange(50, 0, 100, "test");
		assertTrue(result.isValid());
	}
	
	@Test
	@DisplayName("Value out of range")
	void testValueOutOfRange() {
		DataValidator.ValidationResult result = DataValidator.validateInRange(150, 0, 100, "test");
		assertFalse(result.isValid());
	}
	
	@Test
	@DisplayName("Not null validation passes")
	void testNotNullPasses() {
		DataValidator.ValidationResult result = DataValidator.validateNotNull("value", "field");
		assertTrue(result.isValid());
	}
	
	@Test
	@DisplayName("Not null validation fails")
	void testNotNullFails() {
		DataValidator.ValidationResult result = DataValidator.validateNotNull(null, "field");
		assertFalse(result.isValid());
	}
}
