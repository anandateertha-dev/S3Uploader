package com.builderimage;

import java.io.File;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3Upload {

    private final S3Client client;

    public S3Upload() {
        this.client = S3Client.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(() -> AwsBasicCredentials.create(System.getenv("AWS_ACCESS_KEY_ID"),
                        System.getenv("AWS_SECRET_ACCESS_KEY")))
                .build();
    }

    public void putObjectService(String key, File file) {
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(S3Data.bucketName)
                    .key(key)
                    .build();

            RequestBody requestBody = RequestBody.fromFile(file);
            PutObjectResponse response = client.putObject(putOb, requestBody);

            System.out.println("File uploaded successfully: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadDirectory(File directory, String bucketKeyPrefix) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String key = bucketKeyPrefix + file.getName();
                    putObjectService(key, file);
                } else if (file.isDirectory()) {
                    String newPrefix = bucketKeyPrefix + file.getName() + "/";
                    uploadDirectory(file, newPrefix);
                }
            }
        }
    }
}
