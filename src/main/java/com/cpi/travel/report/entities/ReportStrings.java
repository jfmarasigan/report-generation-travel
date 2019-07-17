package com.cpi.travel.report.entities;

public class ReportStrings {

	private String reportBinaryDir;
	private String generatedReportDir;

	public ReportStrings(String reportBinaryDir, String generatedReportDir) {
		this.reportBinaryDir = reportBinaryDir;
		this.generatedReportDir = generatedReportDir;
	}

	public String getReportBinaryDir() {
		return reportBinaryDir;
	}

	public String getGeneratedReportDir() {
		return generatedReportDir;
	}

}
