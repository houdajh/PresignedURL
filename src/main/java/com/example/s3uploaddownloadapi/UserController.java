package com.example.s3uploaddownloadapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("users")
public class UserController {

	@Autowired
	private S3Util s3Util;
	
	@PostMapping("upload")
	public void uploadFile(@RequestParam("key") String key) {
		System.out.println("Upload-" + key);
		try {
			//File fileObj = convertMultiPartToFile(file);
			//String key = "test";
			s3Util.uploadPhoto(key);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@GetMapping("download")
	public ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName) {
		System.out.println("Download-" + fileName);
		byte[] content = null;
		try {
			String key = fileName;
			content= s3Util.downloadPhoto(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" +fileName + "\"").body(content);
		
	}
	
	@PostMapping("delete")
	public void deleteFile(@RequestParam("fileName") String fileName) {
		System.out.println("Delete-" + fileName);
		try {
			String key = fileName;
			s3Util.deleteFile(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
	    File convFile = new File(file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}
}
