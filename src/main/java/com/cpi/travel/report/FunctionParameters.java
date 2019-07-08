package com.cpi.travel.report;

import java.util.HashMap;
import java.util.Map;

public class FunctionParameters {

	private String recipient;
	private Integer policyId;
	private Integer tranId;

	public FunctionParameters() {
		// intentionally empty
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Integer getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Integer policyId) {
		this.policyId = policyId;
	}

	public Integer getTranId() {
		return tranId;
	}

	public void setTranId(Integer tranId) {
		this.tranId = tranId;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("P_POLICY_ID", this.policyId);
		map.put("P_TRAN_ID", this.tranId);
		
		return map;
	}
}
