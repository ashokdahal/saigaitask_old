/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

/**
 * 無効なアクセス/リクエストを検出時に発生する例外
 * CSRF対策
 */
public class InvalidAccessException extends RuntimeException{
	static final long serialVersionUID = -1848914673093119400L;

    /**
     * 
     */
    public InvalidAccessException() {
        super();
    }

    /**
     * @param s
     */
    public InvalidAccessException(String s) {
        super(s);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public InvalidAccessException(Throwable cause) {
        super(cause);
    }
}
