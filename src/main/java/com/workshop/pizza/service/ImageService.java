package com.workshop.pizza.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.pizza.exception.NotFoundException;

public class ImageService {

	public static String uploadImage(MultipartFile file, String productName) throws IOException {
		String[] photoTail = file.getOriginalFilename().split("\\.");
		String[] imageTails = { "JPG", "PNG", "JPEG", "GIF" };
		for (String tail : imageTails) {
			if (photoTail[1].equalsIgnoreCase(tail)) {
				String pathDirectory = "src\\main\\resources\\static\\images";
				File newFile = new File(productName + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
				Files.copy(file.getInputStream(),
						Paths.get(pathDirectory + File.separator + newFile.toPath()),
						StandardCopyOption.REPLACE_EXISTING);
				
				return newFile.getName();
			}
		}
		return "The image doesn't have the same format as requested (PNG, GIF, JPG, JPEG)";
	}
	
	public static Object downloadImage(String fileName) throws IOException{
		String pathDirectory = "src\\main\\resources\\static\\images\\";
		try {
			MultipartFile multipartFile = new MockMultipartFile(fileName,
					new FileInputStream(new File(pathDirectory + fileName)));
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/png"))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment, file=\"" + fileName)
					.body(new ByteArrayResource(multipartFile.getBytes()));
		} catch (Exception e) {
			return new NotFoundException("Your image file does not exist)");
		}
	}
	
	public static void deleteImage(Path filePath) throws IOException {
		Files.deleteIfExists(filePath);
	}
}
