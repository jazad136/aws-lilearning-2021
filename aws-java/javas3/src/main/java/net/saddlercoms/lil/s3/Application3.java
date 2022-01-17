package net.saddlercoms.lil.s3;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class Application3 {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application3.class);
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
	
	public Application3(S3Client s3) { 
		this.s3 = s3;
	}
	
	public List<String> listFiles(String bucket) { 
		List<String> keys = new ArrayList<>();
		try {
			ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucket).build();
			ListObjectsResponse response = s3.listObjects(request);
			response.contents().forEach(content -> keys.add(content.key()));
		} catch(Exception e) { 
			LOGGER.error("error during list files", e);
		}
		return keys;
	}
	public void copyFile(String sourceBucket, String destinationBucket, String sourceKey, String destinationKey) { 
		try {
			String encodedUrl = URLEncoder.encode(sourceBucket + "/" + sourceKey, StandardCharsets.UTF_8.toString());
			CopyObjectRequest request = CopyObjectRequest.builder()
					.sourceBucket(sourceBucket)
					.destinationBucket(destinationBucket)
					.copySource(encodedUrl)
//					.sourceKey(sourceKey)
//					.destinationKey(destinationKey)
					.build();
			s3.copyObject(request);
		} catch(Exception e) { 
			LOGGER.error("error while executing method copyFile()", e);
		}
	}
	public void createBucket(String name) { 
		try { 
			CreateBucketRequest request = CreateBucketRequest
					.builder()
					.bucket(name)
					.build();
			s3.createBucket(request);
		} catch(Exception e) { 
			LOGGER.error("error while executing method createBucket()", e);
		}
	}
	
	public void uploadFile(String bucket, String localFile, String localDirectory, String key) { 
		try { 
			PutObjectRequest request = PutObjectRequest
					.builder()
					.bucket(bucket)
					.key(key)
					.build();
			s3.putObject(request, Paths.get(localDirectory, localFile));
		} catch(Exception e) {
			LOGGER.error("error while executing method uploadFile()", e);
		}
	}
	public void downloadFile(String bucket, String localFile, String localDirectory, String key) { 
		try { 
			GetObjectRequest request = GetObjectRequest
					.builder().bucket(bucket).key(key).build();
			s3.getObject(request, Paths.get(localDirectory, localFile));
		} catch(Exception e) { 
			LOGGER.error("error while executing method downloadFile()", e);
		}
	}
	public void deleteFile(String bucket, String key) { 
		try {
			DeleteObjectRequest request = DeleteObjectRequest
					.builder().bucket(bucket).key(key).build();
			s3.deleteObject(request);
		} catch(Exception e) { 
			LOGGER.error("error while executing method deleteFile()", e);
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
		
		Application3 app = new Application3(s3);

//		app.createBucket(TRANSIENT_BUCKET_NAME);
//		app.uploadFile(TRANSIENT_BUCKET_NAME, F1, DIR, F1);
//		app.downloadFile(TRANSIENT_BUCKET_NAME, F1, DOWN_DIR, F1);
//		app.deleteFile(TRANSIENT_BUCKET_NAME, F1);
		
		
	}
	
	
}
