package com.cpi.ectpl.report.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import com.cpi.ectpl.report.FunctionParameters;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class ReportGenerator {

	private String reportName;
	private AmazonS3Utility s3util;

	private String reportBinariesDirectory;

	private static final String SEPARATOR = "/";
	private static final String JASPER_REPORT_DIR = System.getenv("JASPER_REPORT_DIR");
	private static final String GENERATED_REPORTS_DIR = System.getenv("GENERATED_REPORT_DIR");

	public ReportGenerator(String reportName) {
		this.reportName = reportName;
		this.s3util = new AmazonS3Utility();

		this.reportBinariesDirectory = new StringBuilder(JASPER_REPORT_DIR).append(SEPARATOR)
				.append(reportName).append(".jasper").toString();
		System.out.println("Report binaries directory: " + reportBinariesDirectory);
	}

	public boolean generatePDFReportByteStream(Connection connection, FunctionParameters reportParams)
			throws MalformedURLException, JRException, IOException {
		InputStream reportStream = s3util.getObjectFromS3(reportBinariesDirectory);
		JasperPrint jsPrint = JasperFillManager.fillReport(reportStream, reportParams.toMap(), connection);
		byte[] jasperByte = JasperExportManager.exportReportToPdf(jsPrint);
		String policyId = reportParams.getPolicyId().toString();
		return s3util.putObjectToS3Bucket(jasperByte, generateReportDirectory(policyId));
	}

	private String generateReportDirectory(String uniqueDirectory) {
		return new StringBuilder(GENERATED_REPORTS_DIR)
				.append(SEPARATOR)
				.append(uniqueDirectory)
				.append(SEPARATOR)
				.append(reportName)
				.append(".pdf").toString();
	}

	@SuppressWarnings("unused")
	private InputStream getReportBinaryFromURL() throws MalformedURLException, IOException {
		return new URL(reportBinariesDirectory).openStream();
	}
}
