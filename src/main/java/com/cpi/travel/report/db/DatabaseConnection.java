package com.cpi.travel.report.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cpi.travel.report.exception.ReportGenerationException;
import com.cpi.travel.report.utilities.Env;
import com.cpi.travel.report.utilities.KMSDecryptor;
import com.cpi.travel.report.utilities.SSMDBParameterRetriever;
import com.mysql.cj.jdbc.Driver;

public class DatabaseConnection {

	private String url;
	private String username;
	private String password;
	private String databaseName;
	private String portNumber = "3306";

	public DatabaseConnection(String url) {
		this.url = url;
	}

	public Connection getConnection() throws SQLException {
		DriverManager.registerDriver(new Driver());
		String jdbcURL = "jdbc:mysql://" + url + ":" + portNumber + "/" + databaseName;
		return DriverManager.getConnection(jdbcURL, username, password);
	}
	
	public DatabaseConnection usingPort(String portNumber) {
		this.portNumber = portNumber;
		return this;
	}
	
	public DatabaseConnection onDatabase(String databaseName) {
		this.databaseName = databaseName;
		return this;
	}
	
	public DatabaseConnection withUsername(String username) {
		this.username = username;
		return this;
	}
	
	public DatabaseConnection withPassword(String password) {
		this.password = password;
		return this;
	}
	
	/**
	 * create connection using defined environment variable values
	 * */
	public static Connection createMySQLConnection() {
		try {
			String url = Env.HOST.value();
			String portNumber = Env.PORT.value();
			String databaseName = Env.DATABASE_NAME.value();
			String username = KMSDecryptor.decrypt(Env.DATABASE_USER.value());
			String password = KMSDecryptor.decrypt(Env.DATABASE_PASSWORD.value());
			
			DriverManager.registerDriver(new Driver());
			String jdbcURL = "jdbc:mysql://" + url + ":" + portNumber + "/" + databaseName;
			return DriverManager.getConnection(jdbcURL, username, password);
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while creating database connection", e);
		}
	}
	

	private static List<String> getParameterNames() {
		StringBuilder prefix = new StringBuilder("/bliss/").append(Env.COMPANY_NAME.value()).append("/db/");
		List<String> parameterNames = new ArrayList<>();
		parameterNames.add(0, new StringBuilder().append(prefix).append("host").toString());
		parameterNames.add(1, new StringBuilder().append(prefix).append("port").toString());
		parameterNames.add(2, new StringBuilder().append(prefix).append(Env.APPLICATION.value()).append("/database").toString());
		parameterNames.add(3, new StringBuilder().append(prefix).append("user").toString());
		parameterNames.add(4, new StringBuilder().append(prefix).append("password").toString());
		System.out.println(parameterNames);
		return parameterNames;
	}

	/**
	 * create connection using defined environment variable values this uses AWS
	 * Systems Manager - Parameter Store to store values
	 */
	public static Connection getMySQLConnection() {
		try {
			List<String> parameterNames = getParameterNames();
			Map<String, String> parameters = SSMDBParameterRetriever.getDatabaseParameters(parameterNames);
			String host = parameters.get(parameterNames.get(0));
			String portNumber = parameters.get(parameterNames.get(1));
			String databaseName = parameters.get(parameterNames.get(2));
			String username = parameters.get(parameterNames.get(3));
			String password = parameters.get(parameterNames.get(4));

			DriverManager.registerDriver(new Driver());
			String jdbcURL = "jdbc:mysql://" + host + ":" + portNumber + "/" + databaseName;
			return DriverManager.getConnection(jdbcURL, username, password);
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while creating database connection", e);
		}
	}
}
