package com.cpi.ectpl.report;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.cpi.ectpl.report.db.DatabaseConnection;
import com.cpi.ectpl.report.exception.ReportGenerationException;
import com.cpi.ectpl.report.services.EmailSender;
import com.cpi.ectpl.report.services.ReportGenerator;
import com.cpi.ectpl.report.services.DatabaseOperations;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.jasperreports.engine.JRException;

public class LambdaFunctionHandler implements RequestHandler<FunctionParameters, String> {

	@Override
	public String handleRequest(FunctionParameters input, Context context) {
		System.out.println("Generating reports using input : " + input);
		Integer policyId = input.getPolicyId();
		Integer tranId = input.getTranId();
		DatabaseOperations updater = new DatabaseOperations(policyId, tranId);
		
		try (Connection connection = DatabaseConnection.createMySQLConnection();) {
			boolean isGenerated = true;
			
			String reports = System.getenv("REPORTS");
			
			for (String report : reports.split(",,")) {
				ReportGenerator utility = new ReportGenerator(report);
				isGenerated = utility.generatePDFReportByteStream(connection, input);
				
				if (!isGenerated) {
					String message = "Report " + report + " was not generated.";
					updater.updateGenerateReportStatus(message);
					throw new ReportGenerationException(message);
				} else {
					updater.updateGenerateReportStatus("SUCCESS");
				}
			}			
		} catch (SQLException | JRException | IOException e) {
			updater.updateGenerateReportStatus(e.getMessage());
			throw new ReportGenerationException(e);
		}

		String recipient = input.getRecipient();
		EmailSender sender = new EmailSender(recipient, policyId.toString());
		ObjectNode emailResult = sender.send();

		return generateResponse(emailResult);
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
