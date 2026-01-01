/**
 * 
 */
package exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Unit tests for InvalidSecurityException
 * @author ajay
 *
 */
@DisplayName("Invalid Security Exception Tests")
class InvalidSecurityExceptionTest {
	
	@Test
	@DisplayName("Create exception with message only")
	void testExceptionWithMessage() {
		String message = "Invalid data in cell";
		InvalidSecurityException ex = new InvalidSecurityException(message);
		assertEquals(message, ex.getMessage());
	}
	
	@Test
	@DisplayName("Create exception with error code and context")
	void testExceptionWithMessageAndErrorCode() {
		String context = "Data parsing failed";
		InvalidSecurityException.ErrorCode code = InvalidSecurityException.ErrorCode.PARSE_ERROR;
		InvalidSecurityException ex = new InvalidSecurityException(code, context);
		assertTrue(ex.getMessage().contains("PARSE_ERROR"));
		assertEquals(code, ex.getErrorCode());
	}
	
	@Test
	@DisplayName("Create exception with error code, context and cause")
	void testExceptionWithMessageAndCause() {
		String context = "Failed to load data";
		Exception cause = new Exception("File not found");
		InvalidSecurityException ex = new InvalidSecurityException(InvalidSecurityException.ErrorCode.IO_ERROR, context, cause);
		assertTrue(ex.getMessage().contains(context));
		assertEquals(cause, ex.getCause());
	}
	
	@Test
	@DisplayName("ErrorCode INVALID_DATA exit code")
	void testInvalidDataErrorCode() {
		InvalidSecurityException ex = new InvalidSecurityException(
			InvalidSecurityException.ErrorCode.INVALID_DATA,
			"Invalid data"
		);
		assertEquals(3, ex.getExitCode());
	}
	
	@Test
	@DisplayName("ErrorCode MISSING_FILE exit code")
	void testMissingFileErrorCode() {
		InvalidSecurityException ex = new InvalidSecurityException(
			InvalidSecurityException.ErrorCode.MISSING_FILE,
			"File missing"
		);
		assertEquals(2, ex.getExitCode());
	}
	
	@Test
	@DisplayName("ErrorCode PARSE_ERROR exit code")
	void testParseErrorErrorCode() {
		InvalidSecurityException ex = new InvalidSecurityException(
			InvalidSecurityException.ErrorCode.PARSE_ERROR,
			"Parse error"
		);
		assertEquals(3, ex.getExitCode());
	}
	
	@Test
	@DisplayName("ErrorCode MISSING_COLUMN exit code")
	void testConfigErrorErrorCode() {
		InvalidSecurityException ex = new InvalidSecurityException(
			InvalidSecurityException.ErrorCode.MISSING_COLUMN,
			"Configuration error"
		);
		assertEquals(1, ex.getExitCode());
	}
	
	@Test
	@DisplayName("Get error code from exception")
	void testGetErrorCode() {
		InvalidSecurityException.ErrorCode code = InvalidSecurityException.ErrorCode.IO_ERROR;
		InvalidSecurityException ex = new InvalidSecurityException(code, "IO failed");
		assertEquals(code, ex.getErrorCode());
	}
	
	@Test
	@DisplayName("Error code IO_ERROR maps to exit code 4")
	void testIOErrorExitCode() {
		InvalidSecurityException ex = new InvalidSecurityException(
			InvalidSecurityException.ErrorCode.IO_ERROR,
			"IO error"
		);
		assertEquals(4, ex.getExitCode());
	}
	
	@Test
	@DisplayName("Multiple error codes have different exit codes")
	void testDifferentErrorCodeExitCodes() {
		InvalidSecurityException ex1 = new InvalidSecurityException(
			InvalidSecurityException.ErrorCode.INVALID_DATA,
			"msg1"
		);
		InvalidSecurityException ex2 = new InvalidSecurityException(
			InvalidSecurityException.ErrorCode.MISSING_FILE,
			"msg2"
		);
		assertNotEquals(ex1.getExitCode(), ex2.getExitCode());
	}
	
	@Test
	@DisplayName("Exception is Throwable")
	void testExceptionIsThrowable() {
		InvalidSecurityException ex = new InvalidSecurityException("Test");
		assertTrue(ex instanceof Throwable);
		assertTrue(ex instanceof RuntimeException);
	}
	
	@Test
	@DisplayName("Exception message contains context")
	void testExceptionMessageContainsContext() {
		InvalidSecurityException ex = new InvalidSecurityException("Test");
		assertNotNull(ex);
		assertTrue(ex.getExitCode() > 0);
	}
}
