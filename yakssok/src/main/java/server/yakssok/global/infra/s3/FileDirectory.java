package server.yakssok.global.infra.s3;

import java.util.Arrays;

import lombok.Getter;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.infra.s3.exception.S3FileException;

@Getter
public enum FileDirectory {
	PROFILE("profile"),;

	private final String baseDirectory;

	FileDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public static FileDirectory from(String dir) {
		return Arrays.stream(FileDirectory.values())
			.filter(fd -> fd.baseDirectory.equalsIgnoreCase(dir))
			.findFirst()
			.orElseThrow(() -> new S3FileException(ErrorCode.UNSUPPORTED_FILE_TYPE));
	}

}
