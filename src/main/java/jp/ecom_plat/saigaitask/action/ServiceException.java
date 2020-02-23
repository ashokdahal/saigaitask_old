/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

/**
 * システムエラー時にスローされる例外クラスです.
 */
public class ServiceException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1137949488882368859L;

	/**
	 * Constructs the exception from a message.
	 *
	 * @param message The message describing the exception.
	 */
	public ServiceException(String message) {
		super(message);
	}

	/**
	 * Constructs the exception from a message and causing exception.
	 *
	 * @param message The message describing the exception.
	 * @param cause The case of the exception.
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs the exception from a causing exception.
	 *
	 * @param cause The case of the exception.
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}

}
