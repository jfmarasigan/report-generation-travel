package com.cpi.travel.report.utilities;

public enum Env {
	
	SEPARATOR("/"),
	JASPER_REPORT_DIR(System.getenv("JASPER_REPORT_DIR")),
	GENERATED_REPORT_DIR(System.getenv("GENERATED_REPORT_DIR")),
	BUCKET_NAME(System.getenv("BUCKET")),
	HOST(System.getenv("HOST")),
	PORT(System.getenv("PORT")),
	DATABASE_NAME(System.getenv("DATABASE_NAME")),
	DATABASE_USER(System.getenv("DATABASE_USER")),
	DATABASE_PASSWORD(System.getenv("DATABASE_PASSWORD")),
	REPORTS(System.getenv("REPORTS")),
	EMAIL_SENDER(System.getenv("EMAIL_SENDER_FUNCTION")),
	COMPANY_NAME(System.getenv("COMPANY_NAME")),
	APPLICATION(System.getenv("APPLICATION"))
	;
	
	private String value;
	
	Env(String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
}
