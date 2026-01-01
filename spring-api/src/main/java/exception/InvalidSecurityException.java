/**
 * 
 */
package exception;

/**
 * @author ajay
 *
 */
public class InvalidSecurityException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public enum ErrorCode {
		INVALID_DATA("Data format is invalid"),
		MISSING_FILE("Configuration file not found"),
		PARSE_ERROR("Failed to parse Excel data"),
		INVALID_DATE("Invalid date format"),
		MISSING_COLUMN("Required column not found"),
		INVALID_AMOUNT("Invalid amount value"),
		INVALID_HOLDING_DAYS("Invalid holding days value"),
		IO_ERROR("File I/O error occurred"),
		UNKNOWN_ERROR("Unknown error occurred");
		
		private final String message;
		
		ErrorCode(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
	}
	
	private ErrorCode code;
	private String context;
	
	public InvalidSecurityException(String message) {
		super(message);
		this.code = ErrorCode.UNKNOWN_ERROR;
		this.context = message;
	}
	
	public InvalidSecurityException(ErrorCode code, String context) {
		super(code.getMessage() + ": " + context);
		this.code = code;
		this.context = context;
	}
	
	public InvalidSecurityException(ErrorCode code, String context, Throwable cause) {
		super(code.getMessage() + ": " + context, cause);
		this.code = code;
		this.context = context;
	}
	
	public ErrorCode getErrorCode() {
		return code;
	}
	
	public String getContext() {
		return context;
	}
	
	public int getExitCode() {
		switch(code) {
			case MISSING_FILE:
				return 2;
			case PARSE_ERROR:
			case INVALID_DATA:
				return 3;
			case IO_ERROR:
				return 4;
			default:
				return 1;
		}
	}
}

