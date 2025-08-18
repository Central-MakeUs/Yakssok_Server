package server.yakssok.global.infra.s3;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.infra.s3.exception.S3FileException;
@Component
public class FilePathGenerator {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp");

	public String generate(FileDirectory directory, String originalFilename) {
		String extension = extractExtension(originalFilename);
		validateExtension(extension);
		return directory.getBaseDirectory() + "/" + UUID.randomUUID() + extension;
	}

	private String extractExtension(String filename) {
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex == -1) throw new S3FileException(ErrorCode.INVALID_FILE_EXTENSION);
		return filename.substring(dotIndex);
	}

	private void validateExtension(String extension) {
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new S3FileException(ErrorCode.INVALID_FILE_EXTENSION);
		}
	}
}
