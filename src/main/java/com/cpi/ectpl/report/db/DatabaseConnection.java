package com.cpi.ectpl.report.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.cpi.ectpl.report.exception.ReportGenerationException;
import com.cpi.ectpl.report.utilities.KMSDecryptor;
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
			String url = System.getenv("HOST");
			String portNumber = System.getenv("PORT");
			String databaseName = System.getenv("DATABASE_NAME");
			String username = KMSDecryptor.decrypt(System.getenv("DATABASE_USER"));
			String password = KMSDecryptor.decrypt(System.getenv("DATABASE_PASSWORD"));
			
			DriverManager.registerDriver(new Driver());
			String jdbcURL = "jdbc:mysql://" + url + ":" + portNumber + "/" + databaseName;
			return DriverManager.getConnection(jdbcURL, username, password);
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while create database connection", e);
		}
	}
}
