/* (C)2025 */
package org.example.exception;

public class FileDeletionException extends RuntimeException {
    public FileDeletionException(final String message, final Throwable e) {
        super(message, e);
    }

    public FileDeletionException(final Throwable e) {
        super(e);
    }
}
