package com.s3listener.LambdaS3Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Hello world!
 *
 */
public class AWSS3LambdaListener implements RequestHandler<S3Event, Boolean> {

	private static final AmazonS3 AMAZON_S3_CLIENT = AmazonS3Client.builder()
			.withCredentials(new DefaultAWSCredentialsProviderChain()).build();

	@Override
	public Boolean handleRequest(S3Event input, Context context) {
		final List<S3EventNotificationRecord> recordList = input.getRecords();
		final LambdaLogger lambdaLogger = context.getLogger();
		
		lambdaLogger.log(input.toString());

		if (recordList.isEmpty()) {
			lambdaLogger.log("UNEXPECTED: No records found for object PUT event in S3!" + input.toString());
			return false;
		} else {
			for (final S3EventNotificationRecord record : recordList) {
				final String bucketName = record.getS3().getBucket().getName();
				final String objectKey = record.getS3().getObject().getKey();

				final S3Object s3Object = AMAZON_S3_CLIENT.getObject(bucketName, objectKey);

				try {
					final S3ObjectInputStream objectInputSteam = s3Object.getObjectContent();
					final BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(objectInputSteam, StandardCharsets.UTF_8));

					lambdaLogger.log("SUCCESS: Content of object: " + objectKey + "obtained as follows: "
							+ bufferedReader.lines().collect(Collectors.toList()));

					bufferedReader.close();
				} catch (final IOException e) {
					lambdaLogger
							.log("ERROR: IO Exception occured while trying to read object contents of: " + objectKey);
					return false;
				}
			}
		}
		return null;
	}

}
