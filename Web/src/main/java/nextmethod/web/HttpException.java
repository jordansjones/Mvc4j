package nextmethod.web;

import javax.servlet.ServletException;

/**
 * 
 */
public class HttpException extends ServletException {
	
    private int httpErrorCode;

	public HttpException(final String message) {
		super(message);
	}

    public HttpException(final int httpErrorCode, final String message) {
        super(message);
        this.httpErrorCode = httpErrorCode;
	}

    public int getHttpErrorCode()
    {
        return httpErrorCode;
    }
}
