package server.yakssok.global.infra.s3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
@Getter
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsS3Properties {
	private final Credentials credentials = new Credentials();
	private final Region region = new Region();
	private final S3 s3 = new S3();

	@Getter
	public static class Credentials {
		private String accessKey;
		private String secretKey;

		public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
		public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
	}

	@Getter
	public static class Region {
		private String staticRegion;

		public void setStaticRegion(String staticRegion) { this.staticRegion = staticRegion; }
	}

	@Getter
	public static class S3 {
		private String bucket;

		public void setBucket(String bucket) { this.bucket = bucket; }
	}
}
