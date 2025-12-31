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
	
	public InvalidSecurityException(String message) {
		super(message);
	}
}
