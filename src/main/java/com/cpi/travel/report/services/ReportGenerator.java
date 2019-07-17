package com.cpi.travel.report.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import com.cpi.travel.report.entities.FunctionParameters;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class ReportGenerator {

	private String reportBinariesDirectory;
	private InputStream report;

	public ReportGenerator(InputStream reportBinary) {
		this.report = reportBinary;
	}
	
	public byte[] generatePDFReportByteArray(Connection connection, FunctionParameters reportParams)
			throws MalformedURLException, JRException, IOException {
		JasperPrint jsPrint = JasperFillManager.fillReport(this.report, reportParams.toMap(), connection);
		byte[] jasperByte = JasperExportManager.exportReportToPdf(jsPrint);
		return jasperByte;
	}

	@SuppressWarnings("unused")
	private InputStream getReportBinaryFromURL() throws MalformedURLException, IOException {
		return new URL(reportBinariesDirectory).openStream();
	}
}
