package com.cpi.travel.report.utilities;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;

public final class KMSDecryptor {

	public static String decrypt(String envVariable) {
		byte[] encryptedKey = Base64.decode(envVariable);
		AWSKMS client = AWSKMSClientBuilder.defaultClient();
		DecryptRequest request = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(encryptedKey));
		ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
		return new String(plainTextKey.array(), StandardCharsets.UTF_8);
	}
}
