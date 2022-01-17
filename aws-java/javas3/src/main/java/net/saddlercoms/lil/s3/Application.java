package net.saddlercoms.lil.s3;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

public class Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	private static final String AWS_ACCESS_KEY = "AWS_ACCESS_KEY_ID";
	private static final String AWS_SECRET_KEY = "AWS_SECRET_ACCESS_KEY";
	private static final String PRI_BUCKET_NAME = "jaslil";
	private static final String TRANSIENT_BUCKET_NAME = "jaslilv2";

	private static final String F1 = "lil1.txt";
	private static final String F2 = "lil2.txt";
	private static final String F3 = "lil3.txt";
	private static final String BASE = "/Users/jsaddle/code/pers2/plain/AWS_exercise2/aws-s3-dev";
	private static final File fBASE = new File("C:\\Users\\jsaddle\\code\\pers2\\plain\\AWS_exercise2\\aws-s3-dev");
	private static final String DIR = BASE + "/s3";
	private static final String DOWN_DIR = BASE + "/s3alt";
	
	private final S3Client s3;
	
	public Application(S3Client s3) { 
		this.s3 = s3;
	}
	
	public void createBucket(String name) { 
		try { 
			CreateBucketRequest request = CreateBucketRequest
					.builder()
					.bucket(name)
					.build();
			s3.createBucket(request);
		} catch(Exception e) { 
			LOGGER.error("error during create bucket", e);
		}
	}
	
	public static void main(String[] args) {
		// this line must be here to configure Log4J to work. 
		BasicConfigurator.configure();
		
		String accessKey = System.getenv(AWS_ACCESS_KEY);
		String secretKey = System.getenv(AWS_SECRET_KEY);
		AwsSessionCredentials creds = AwsSessionCredentials.create(accessKey, secretKey, "");
		
		S3Client s3 = S3Client.builder()
				.credentialsProvider(StaticCredentialsProvider.create(creds)).build();
		Application app = new Application(s3);
		app.createBucket(PRI_BUCKET_NAME);
	}
	
	
}
