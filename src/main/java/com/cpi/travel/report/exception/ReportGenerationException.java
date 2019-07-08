package com.cpi.travel.report.exception;

public class ReportGenerationException extends RuntimeException {

	private static final long serialVersionUID = -6331026917413718098L;

	public ReportGenerationException(String message) {
		super(message);
	}
	
	public ReportGenerationException(Throwable throwable) {
		super(throwable);
	}
	
	public ReportGenerationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
