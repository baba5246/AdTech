package io;

@SuppressWarnings("serial")
public class CsvBadFormatException extends RuntimeException {

	public CsvBadFormatException(String message) {
		super(message);
	}
}
