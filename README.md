# S3 Upload Event Handler Using Java and AWS Lambda
Dscribed here is a template java program whose purpose is to read contents of a S3 Object when an event is detected by AWS Lambda Trigger.

The code does not perform the detection of the event, that functionality is made available in AWS. Through the code we can accept the event "payload" that is generated on occurance of a PUT event in a specific bucket in S3 that is tied to our Lambda function.

I have opted to give a brief run down of the process of implemention of a Java program to do the above here. Starting from IAM role creation to viewing logs in Cloudwatch for our program.

## Pre-requisites
1. A bucket created in S3
2. IDE to write the Java code 

**Note: If you are uploading Jar/zip, make sure you are using Java 11, if you are using a docker container then there are no restrictions  on the version to my knowledge.**

## Dependencies and Plugins
The following dependencies are a must for performing basic read operation on S3 object using lambda.
```xml
    		<dependency>
			<groupId>com.amazonaws</groupId>
			<artifactId>aws-java-sdk-s3</artifactId>
			<version>1.12.420</version>
		</dependency>
    		<dependency>
			<groupId>com.amazonaws</groupId>
			<artifactId>aws-lambda-java-core</artifactId>
			<version>1.2.2</version>
		</dependency>
    		<dependency>
			<groupId>com.amazonaws</groupId>
			<artifactId>aws-lambda-java-events</artifactId>
			<version>3.11.0</version>
		</dependency>
```
**It is recommended to use shade plugin so that the classes included in the above dependencies are available in our jar**
```xml
		<plugin>
				<artifactId>maven-shade-plugin</artifactId>
				<version>3.2.4</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
					</execution>
				</executions>
		</plugin>
```
Note: I have found in my practice that <pluginmanagement> tag was causing shade plugins to not load. So, I removed the <pluginmanagement> tag entirely and put my plugins under the <plugins> tag.


## Create a Lambda Function
Head over to AWS console and look up lambda, create a function with a specific name and Runtime as Java 11(Corretto) or whatever Java version your code is compiled using provided it is available.

![Lambda Create function](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(2).png "Lambda Create function")

![Lambda Create function with params](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(3).png "Lambda Create Function Params")

## Add Trigger to Lambda Function
We need to link the events in our S3 bucket to the lambda function somehow. To this end we will be making use of events. On the overview page of your lambda function you will find the option to add an event listener/trigger.

![Lambda Add Trigger](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(16).png "Lambda Add Trigger")

![Lambda Trigger Settings](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(22).png "Lambda Trigger Settings")

## Update roles for Lambda 
Now that we have an event to look out for, we must make sure that our Lambda functionc an actually read the object contents uploaded to S3.
To allow this, we must define role for our lambda fucntion in IAM. 
Note: You can also update the auto generated role for your lambda function but in case you have multiple lambda funcitons then a preset role may be the better choice!

![IAM Role](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(8).png "IAM Role")

We have defined a preset S3-upload-listener-role as follows:
![IAM Role Creation](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(9).png "IAM Role Creation")

![IAM Role Overview](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(13).png "IAM Role Overview")

Now, head over to your Lambda function and under Configuration and edit roles to update the new role

![IAM Role Updation Lambda](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(15).png "IAM Role Updation Lambda")

## Upload the JAR generated for the code and update runtime params
Finally, we need to upload the jar for our program and test it.
![Lambda Function Code](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(17).png "Lambda Code Upload")

Next, we need to inform lambda function about the method we want it to invoke when it is triggered. To do this, look for "Runtime settings" under "Code" sub-tab in your lambda function's overview and click Edit.

![Lambda Runtime Params](https://github.com/RikuAlter/s3-lambda-listener/blob/main/img/Screenshot%20(19).png "Lambda Runtime Params")

In here under the "Handler" make sure your class(with full package path) is mentioned correctly along with the method name in the format shown above.


## View logs in CloudWatch
Now that we are done with setting up our lambda function we can trigger it by uploading a file to our bucket. And view the logs in Cloudwatch.
Cloudwatch logs can be viewed under "Monitor" section of your lambda funciton in "View Cloudwatch logs".

Thank you for reading!

