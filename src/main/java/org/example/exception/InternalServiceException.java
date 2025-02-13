/* (C)2025 */
package org.example.exception;

public class InternalServiceException extends RuntimeException {
    public InternalServiceException(final String message, final Throwable e) {
        super(message, e);
    }

    public InternalServiceException(final Throwable e) {
        super(e);
    }
}
