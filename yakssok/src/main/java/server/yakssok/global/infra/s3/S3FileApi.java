package server.yakssok.global.infra.s3;


import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.infra.s3.config.AwsS3Properties;
import server.yakssok.global.infra.s3.exception.S3FileException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3FileApi {
	private static final String S3_URL_FORMAT = "https://%s.s3.%s.amazonaws.com/%s";
	private final FilePathGenerator pathGenerator;
	private final AwsS3Properties aws;
	private final S3Client s3Client;

	public String upload(MultipartFile file, FileDirectory dirName){
		try{
			String key = pathGenerator.generate(dirName, file.getOriginalFilename());

			PutObjectRequest putRequest = PutObjectRequest.builder()
				.bucket(aws.getS3().getBucket())
				.key(key)
				.contentType(file.getContentType())
				.build();

			s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
			return String.format(S3_URL_FORMAT,
				aws.getS3().getBucket(),
				aws.getRegion().getStaticRegion(),
				key);
		}
		catch (IOException | SdkException e) {
			throw new S3FileException(ErrorCode.FAILED_FILE_UPLOAD);
		}

	}

	public void delete(String fileUrl) {
		if (fileUrl == null) return;
		if (isKakaoUrl(fileUrl)) return;
		String key = extractKeyFromUrl(fileUrl);
		deleteByKey(key);
	}

	public void deleteByKey(String key) {
		try {
			s3Client.deleteObject(DeleteObjectRequest.builder()
				.bucket(aws.getS3().getBucket())
				.key(key)
				.build());
		} catch (SdkException e) {
			throw new S3FileException(ErrorCode.FAILED_FILE_DELETE);
		}
	}


	private String extractKeyFromUrl(String url) {
		String bucket = aws.getS3().getBucket();
		String region = aws.getRegion().getStaticRegion();

		String base = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
		if (url.startsWith(base)) {
			return url.substring(base.length());
		}
		throw new S3FileException(ErrorCode.FAILED_FILE_DELETE);
	}

	private boolean isKakaoUrl(String url) {
		String u = url.toLowerCase();
		return u.contains("kakaocdn.net") || u.contains("kakaocdn.com");
	}
}
