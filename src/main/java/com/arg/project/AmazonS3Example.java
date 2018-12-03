package com.arg.project;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
public class AmazonS3Example {
	
	private static final String SUFFIX = "/";
	


	public static void amazonConnect(String timestamp)
	{

		String timeanddate = timestamp;
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJ673ISJSSODR5YSA", "Qwqrr+snPe3PO2n9pAanGxoxLzCh3ElyVFztBErR");
		AmazonS3 s3client = new AmazonS3Client(credentials);
		// create a bucket
		String bucketName = "riskproject-arg";
		//s3client.createBucket(bucketName);

		String folderName = "riskgamelogs";
		//createFolder(bucketName, folderName, s3client);
		//System.out.println("dfasjklaskl;");

		String fileName = folderName + SUFFIX + timeanddate+"_risklog.txt";
		s3client.putObject(new PutObjectRequest(bucketName, fileName, new File(timeanddate + "_risklog.txt")).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	
	public static void createFolder(String bucketName, String folderName, AmazonS3 client) 
	{
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}
	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	/*public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) 
	{
		List fileList = 
				client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}*/
}