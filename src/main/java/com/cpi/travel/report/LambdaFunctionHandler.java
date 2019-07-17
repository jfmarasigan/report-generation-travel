package com.cpi.travel.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.cpi.travel.report.entities.FunctionParameters;

public class LambdaFunctionHandler implements RequestHandler<FunctionParameters, String> {

	@Override
	public String handleRequest(FunctionParameters input, Context context) {
		System.out.println("Generating reports using input : " + input);
		ProcessHandler handler = new ProcessHandler(input);
		return handler.process();
	}
}
