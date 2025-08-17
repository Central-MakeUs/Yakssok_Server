package server.yakssok.domain.image.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.image.dto.UploadImageResponse;
import server.yakssok.global.infra.s3.FileDirectory;
import server.yakssok.global.infra.s3.S3FileApi;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

	private final S3FileApi s3FileApi;

	@Override
	@Transactional
	public UploadImageResponse upload(MultipartFile file, String type) {
		FileDirectory directory = FileDirectory.from(type);
		String imageUrl = s3FileApi.upload(file, directory);
		return new UploadImageResponse(imageUrl);
	}

	@Override
	@Transactional
	public void delete(String imageUrl) {
		if (!isKakaoUrl(imageUrl)) {
			s3FileApi.delete(imageUrl);
		}
	}

	private boolean isKakaoUrl(String url) {
		String u = url.toLowerCase();
		return u.contains("kakaocdn.net") || u.contains("kakaocdn.com");
	}

	@Override
	@Transactional
	public UploadImageResponse update(MultipartFile file, String type, String oldImageUrl) {
		s3FileApi.delete(oldImageUrl);
		FileDirectory directory = FileDirectory.from(type);
		String newImageUrl = s3FileApi.upload(file, directory);
		return new UploadImageResponse(newImageUrl);
	}
}