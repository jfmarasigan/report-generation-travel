package com.cpi.travel.report.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.cpi.travel.report.utilities.Env;

public class AmazonS3Utility {

	private AmazonS3 s3client;
	
	public AmazonS3Utility() {
		this.s3client = AmazonS3ClientBuilder.standard().build();
	}
	
	public InputStream getObjectFromS3(String key) {
		GetObjectRequest request = new GetObjectRequest(Env.BUCKET_NAME.value(), key);
		S3Object object = s3client.getObject(request);
		return object.getObjectContent();
	}
	
	public boolean putObjectToS3Bucket(byte[] objectInByteArray, String key) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(objectInByteArray.length);
		metadata.setContentType("application/pdf");

		InputStream generatedReportStream = new ByteArrayInputStream(objectInByteArray);
		PutObjectRequest putObjectRequest = new PutObjectRequest(Env.BUCKET_NAME.value(), key, generatedReportStream, metadata);
		s3client.putObject(putObjectRequest);

		return s3client.doesObjectExist(Env.BUCKET_NAME.value(), key);
	}
	
	public boolean deleteObjectsFromS3(String[] keys) {
		for (String key : keys) {
			deleteObjectFromS3(key);
		}
		return true;
	}
	
	public void deleteObjectFromS3(String key) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(Env.BUCKET_NAME.value(), key);
		s3client.deleteObject(deleteObjectRequest);
	}
}
