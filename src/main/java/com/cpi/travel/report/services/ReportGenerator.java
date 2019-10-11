package com.cpi.travel.report.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import com.cpi.travel.report.entities.FunctionParameters;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

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
	
	public byte[] generatePasswordProtectedPDF(Connection connection, FunctionParameters reportParams, String pwd) 
			throws MalformedURLException, JRException, IOException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(this.report, reportParams.toMap(), connection);
		
		ByteArrayOutputStream reportOutputStream = new ByteArrayOutputStream();
		
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportOutputStream));
		
		SimplePdfExporterConfiguration config = new SimplePdfExporterConfiguration();
		
		if (pwd != null && !"".equals(pwd)) {
			config.setEncrypted(true);
			config.set128BitKey(true);
			config.setUserPassword(pwd);
			//config.setOwnerPassword(pwd);
			config.setPermissions(PdfWriter.ALLOW_PRINTING);
		}
		exporter.setConfiguration(config);		
		exporter.exportReport();
		
		return reportOutputStream.toByteArray();
	}

	@SuppressWarnings("unused")
	private InputStream getReportBinaryFromURL() throws MalformedURLException, IOException {
		return new URL(reportBinariesDirectory).openStream();
	}
}
