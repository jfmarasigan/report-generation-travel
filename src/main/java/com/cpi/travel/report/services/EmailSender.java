package com.cpi.travel.report.services;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.cpi.travel.report.exception.ReportGenerationException;
import com.cpi.travel.report.utilities.Env;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class EmailSender {

	private String recipient;
	private String uniqueDirectory;
	private String docPwd;

	public EmailSender(String recipient, String uniqueDirectory, String docPwd) {
		this.recipient = recipient;
		this.uniqueDirectory = uniqueDirectory;
		this.docPwd = docPwd;
	}
	
	public EmailSender(String uniqueDirectory, String docPwd) {
		this.recipient = null;
		this.uniqueDirectory = uniqueDirectory;
		this.docPwd = docPwd;
	}

	public ObjectNode send() {
		AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard();
		AWSLambda client = builder.build();

		InvokeRequest request = new InvokeRequest().withFunctionName(Env.EMAIL_SENDER.value());
		String emailPayload = constructEmailPayload();
		request.withPayload(emailPayload);

		InvokeResult result = client.invoke(request);
		ByteBuffer buffer = result.getPayload();
		String functionError = result.getFunctionError();
		String resultPayload = StandardCharsets.UTF_8.decode(buffer).toString();

		if (buffer != null && functionError == null) {
			System.out.println("Email sending done. Payload: " + resultPayload);
			return constructResponse("Email sending done.", resultPayload);
		} else {
			System.out.println("Error after invoking email sender : " + resultPayload);
			return constructResponse("Error after invoking email sender", resultPayload);
		}
	}

	/**
	 * returns a JSON string representing the payload for sending email
	 */
	private final String constructEmailPayload() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
			jsonNode.put("recipient", recipient);
			jsonNode.put("prefix", uniqueDirectory);
			jsonNode.put("docPwd", docPwd);

			return mapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			throw new ReportGenerationException("Error on generating payload for email sending.", e);
		}
	}

	private final ObjectNode constructResponse(String message, String payload) {
		ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();
		jsonNode.put("message", message);
		jsonNode.put("payload", payload);

		return jsonNode;
	}
}
