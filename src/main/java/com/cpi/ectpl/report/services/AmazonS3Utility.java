package com.cpi.ectpl.report.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class AmazonS3Utility {

	private AmazonS3 s3client;

	private static final String BUCKET_NAME = System.getenv("BUCKET");
	
	public AmazonS3Utility() {
		this.s3client = AmazonS3ClientBuilder.standard().build();
	}
	
	public InputStream getObjectFromS3(String key) {
		GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, key);
		S3Object object = s3client.getObject(request);
		return object.getObjectContent();
	}
	
	public boolean putObjectToS3Bucket(byte[] objectInByteArray, String key) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(objectInByteArray.length);
		metadata.setContentType("application/pdf");

		InputStream generatedReportStream = new ByteArrayInputStream(objectInByteArray);
		PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, generatedReportStream, metadata);
		s3client.putObject(putObjectRequest);

		return s3client.doesObjectExist(BUCKET_NAME, key);
	}
	
	public boolean deleteObjectsFromS3() {
		return true;
	}
}
