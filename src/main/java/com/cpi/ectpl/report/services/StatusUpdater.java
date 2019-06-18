package com.cpi.ectpl.report.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.cpi.ectpl.report.db.DatabaseConnection;
import com.cpi.ectpl.report.exception.ReportGenerationException;

public final class StatusUpdater {

	private Integer policyId;
	private Integer tranId;

	public StatusUpdater(Integer policyId, Integer tranId) {
		this.policyId = policyId;
		this.tranId = tranId;
	}

	public void updateGenerateReportStatus(String message) {
		String query = "UPDATE EPIT_CTPL_TRANS SET GEN_REPORT_STAT = ?, LAST_UPDATE = sysdate() WHERE policy_id = ? AND tran_id = ?";

		try (Connection connection = DatabaseConnection.createMySQLConnection();) {
			try (PreparedStatement stmt = connection.prepareStatement(query);) {
				connection.setAutoCommit(false);
				stmt.setString(1, message);
				stmt.setInt(2, policyId);
				stmt.setInt(3, tranId);
				stmt.executeUpdate();
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
			}
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while updating report generation status", e);
		}
	}

	public void updateEmailSendingStatus(String message) {
		String query = "UPDATE EPIT_CTPL_TRANS SET EMAIL_REPORT_STAT = ?, LAST_UPDATE = sysdate() WHERE policy_id = ? AND tran_id = ?";

		try (Connection connection = DatabaseConnection.createMySQLConnection();) {
			try (PreparedStatement stmt = connection.prepareStatement(query);) {
				connection.setAutoCommit(false);
				stmt.setString(1, message);
				stmt.setInt(2, policyId);
				stmt.setInt(3, tranId);
				stmt.executeUpdate();
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
			}
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while updating report generation status", e);
		}
	}
}
