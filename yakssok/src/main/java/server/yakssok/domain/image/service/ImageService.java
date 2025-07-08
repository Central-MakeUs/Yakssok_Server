package server.yakssok.domain.image.service;

import org.springframework.web.multipart.MultipartFile;

import server.yakssok.domain.image.dto.UploadImageResponse;

public interface ImageService {
	UploadImageResponse upload(MultipartFile file, String type);
	void delete(String imageUrl);
	UploadImageResponse update(MultipartFile file, String type, String oldImageUrl);
}
