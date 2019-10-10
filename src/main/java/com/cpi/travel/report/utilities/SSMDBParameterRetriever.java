package com.cpi.travel.report.utilities;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;

public final class SSMDBParameterRetriever {
	private static final String PREFIX = "/bliss/" + Env.COMPANY_NAME.value() + "/db/";
	public static final String DB_HOST = PREFIX + "host";
	public static final String DB_PORT = PREFIX + "port";
	public static final String DB_NAME = PREFIX + Env.APPLICATION.value() + "/database";
	public static final String DB_USER = PREFIX + "user";
	public static final String DB_PASS = PREFIX + "password";

	public static Map<String, String> getDatabaseParameters(List<String> parameterNames) {
		GetParametersRequest request = new GetParametersRequest();
		request.withNames(parameterNames).setWithDecryption(true);

		AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();
		GetParametersResult result = client.getParameters(request);
		List<Parameter> paramsList = result.getParameters();
		
		// convert list to map with keys = names, value = value
		Map<String, String> parameters = paramsList.stream().collect(Collectors.toMap(Parameter::getName, Parameter::getValue));
		
		return parameters;
	}
}
