package com.example.s3uploaddownloadapi;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class S3Util {

	private static final String bucketName = "test2";

	private MinioClient minioClient;

	@Value("${minio.endpoint}")
	private String minioEndpoint;

	@Value("${minio.accessKey}")
	private String minioAccessKey;

	@Value("${minio.secretKey}")
	private String minioSecretKey;

	public S3Util() {
	}

	@PostConstruct
	private void initializeMinio() {
		try {
			/* Amazon S3: */
			// MinioClient minioClient =
			//     MinioClient.builder()
			//         .endpoint("https://s3.amazonaws.com")
			//         .credentials("YOUR-ACCESSKEY", "YOUR-SECRETACCESSKEY")
			//         .build();
			minioClient = MinioClient.builder()
					.endpoint(minioEndpoint)
					.credentials(minioAccessKey, minioSecretKey)
					.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadPhoto(String key) throws FileNotFoundException {
		try {
			Map<String, String> reqParams = new HashMap<String, String>();
			//reqParams.put("response-content-type", "application/json");
			String contentType = "image/png";
			reqParams.put("content-type", contentType);

			String url =
					minioClient.getPresignedObjectUrl(
							GetPresignedObjectUrlArgs.builder()
									.method(Method.PUT)
									.bucket(bucketName)
									.object(key)
									.expiry(60 * 60 * 24)
									.extraQueryParams(reqParams)
									.build());
			System.out.println(url);
		} catch (MinioException e) {
			System.out.println("Error occurred: " + e);
		} catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

        public byte[] downloadPhoto(String key) {
		try {
			InputStream inputStream = minioClient.getObject(
					GetObjectArgs.builder()
							.bucket(bucketName)
							.object(key)
							.build()
			);
			return IOUtils.toByteArray(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteFile(String key) {
		try {
			minioClient.removeObject(
					RemoveObjectArgs.builder()
							.bucket(bucketName)
							.object(key)
							.build()
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
