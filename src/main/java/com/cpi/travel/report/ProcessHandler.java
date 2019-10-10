package com.cpi.travel.report;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cpi.travel.report.db.DatabaseConnection;
import com.cpi.travel.report.entities.FunctionParameters;
import com.cpi.travel.report.entities.ReportStrings;
import com.cpi.travel.report.exception.ReportGenerationException;
import com.cpi.travel.report.services.AmazonS3Utility;
import com.cpi.travel.report.services.DatabaseOperations;
import com.cpi.travel.report.services.EmailSender;
import com.cpi.travel.report.services.ReportGenerator;
import com.cpi.travel.report.utilities.Env;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.jasperreports.engine.JRException;

public final class ProcessHandler {

	private AmazonS3Utility s3util;
	private FunctionParameters fnParams;
	
	public ProcessHandler(FunctionParameters fnParams) {
		this.s3util = new AmazonS3Utility();
		this.fnParams = fnParams;
	}

	public String process() {
		DatabaseOperations dbOps = new DatabaseOperations(fnParams.getPolicyId());
		String reportDir = fnParams.getStrPolicyId();

		List<ReportStrings> reportStrings = reportStrings(reportDir);
		String pwd = dbOps.getDocumentPassword();
		String reportGenerationStatus = generateReports(reportStrings, fnParams, pwd);
		dbOps.updateGenerateReportStatus(reportGenerationStatus);

		if (!"SUCCESS".equals(reportGenerationStatus)) {
			throw new ReportGenerationException(reportGenerationStatus);
		}

		EmailSender sender = new EmailSender(reportDir, pwd);
		ObjectNode emailResult = processEmailResponse(sender, dbOps);

		deleteReports(reportStrings);

		return generateResponse(emailResult);
	}
	
	private List<ReportStrings> reportStrings(String generatedReportUniqueDir) {
		return Arrays.stream(Env.REPORTS.value().split(",,"))
				.map(reportName -> {
					String reportBinaryDir = new StringBuilder(Env.JASPER_REPORT_DIR.value())
							.append(Env.SEPARATOR.value())
							.append(reportName)
							.append(".jasper")
							.toString();
					String generatedReportDir = new StringBuilder(Env.GENERATED_REPORT_DIR.value())
							.append(Env.SEPARATOR.value())
							.append(generatedReportUniqueDir)
							.append(Env.SEPARATOR.value())
							.append(reportName)
							.append(".pdf")
							.toString();
					return new ReportStrings(reportBinaryDir, generatedReportDir);
				}).collect(Collectors.toList());
	}

	private String generateReports(List<ReportStrings> reportStrings, FunctionParameters reportParams, String pwd) {
		try (Connection connection = DatabaseConnection.createMySQLConnection();) {
			for (ReportStrings reportString : reportStrings) {
				InputStream report = s3util.getObjectFromS3(reportString.getReportBinaryDir());
				ReportGenerator generator = new ReportGenerator(report);
				byte[] byteReport = generator.generatePasswordProtectedPDF(connection, reportParams, pwd);
				s3util.putObjectToS3Bucket(byteReport, reportString.getGeneratedReportDir());
			}
			return "SUCCESS";
		} catch (SQLException | JRException | IOException e) {
			return e.getMessage();
		}
	}

	private void deleteReports(List<ReportStrings> reportStrings) {
		String[] keys = reportStrings.stream()
				.map(reportString -> reportString.getGeneratedReportDir())
				.toArray(String[]::new);
		System.out.println("Deleting: " + Arrays.toString(keys));
		s3util.deleteObjectsFromS3(keys);
	}

	private ObjectNode processEmailResponse(EmailSender sender, DatabaseOperations ops) {
		ObjectNode emailResult = sender.send();
		String message = emailResult.get("message").asText();
		ops.updateEmailSendingStatus(message);

		return emailResult;
	}

	private String generateResponse(ObjectNode emailResult) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
		jsonNode.put("statusCode", 200);
		jsonNode.set("emailResponse", emailResult);

		try {
			return mapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			throw new ReportGenerationException(e);
		}
	}
}
