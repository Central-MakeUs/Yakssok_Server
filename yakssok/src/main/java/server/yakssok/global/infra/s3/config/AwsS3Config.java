package server.yakssok.global.infra.s3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {
	@Bean
	public S3Client s3Client(AwsS3Properties aws) {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(
			aws.getCredentials().getAccessKey(),
			aws.getCredentials().getSecretKey()
		);
		return S3Client.builder()
			.region(Region.of(aws.getRegion().getStaticRegion()))
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.build();
	}
}
