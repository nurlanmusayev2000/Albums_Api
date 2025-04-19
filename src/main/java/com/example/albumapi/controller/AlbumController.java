package com.example.security_sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.security_sample.model.Account;
import com.example.security_sample.model.Album;
import com.example.security_sample.model.Photo;
import com.example.security_sample.payload.album.AlbumPayloadDTO;
import com.example.security_sample.payload.album.AlbumViewDto;
import com.example.security_sample.service.AccountService;
import com.example.security_sample.service.AlbumService;
import com.example.security_sample.service.PhotoService;
import com.example.security_sample.utils.AppUtils.AppUtil;
import com.example.security_sample.utils.constants.AlbumError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@Tag(name = "Album Controller", description = "controller for Album management")
@Slf4j
public class AlbumController {

		static final String PHOTOS_FOLDER_NAME = "photos";
    static final String THUMBNAIL_FOLDER_NAME = "thumbnails";
    static final int THUMBNAIL_WIDTH = 300;

	@Autowired
	private AlbumService albumService;

	@Autowired
	private PhotoService photoService;

	@Autowired
	private AccountService accountService;

	@PostMapping(value = "/album/add" ,consumes = "application/json" , produces = "application/json" )
	@ResponseStatus(HttpStatus.CREATED)
	@ApiResponse(responseCode = "200", description = "Users listed")
	@ApiResponse(responseCode = "404", description = "something went wrong")
	@Operation(summary = " add new album")
	@SecurityRequirement(name = "studyeasy-demo-api")
	public ResponseEntity<AlbumViewDto> addAlbum(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO,
			Authentication authentication) {
		try {

			String email = authentication.getName();
			Optional<Account> optionalAcc = accountService.findByEmail(email);
			Account account = optionalAcc.get();
			Album album = new Album();
			album.setName(albumPayloadDTO.getName());
			album.setDescription(albumPayloadDTO.getDescription());
			album.setAccount(account);

			album = albumService.save(album);
			AlbumViewDto albumViewDto = new AlbumViewDto(album.getId(), album.getName(), album.getDescription());
			return ResponseEntity.ok(albumViewDto);

		} catch (Exception e) {
			log.debug(AlbumError.ADD_ALBUM_ERROR.toString() + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping(value = "/user/albums" , produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponse(responseCode = "200", description = "Users listed")
	@ApiResponse(responseCode = "404", description = "something went wrong")
	@Operation(summary = " get all albums for idetificated user")
	@SecurityRequirement(name = "studyeasy-demo-api")
	public List<AlbumViewDto> getAllUserAlbums(Authentication authntication) {

		String email = authntication.getName();
		Optional<Account> optionalAcc = accountService.findByEmail(email);
		Account account = optionalAcc.get();

		List<AlbumViewDto> albums = new ArrayList<>();

		for (Album album : albumService.findByAccount(account.getId())) {
			albums.add(new AlbumViewDto(album.getId(), album.getName(), album.getDescription()));
		}

		return albums;
	}

	@PostMapping(value = "albums/{album_id}/upload-photos",consumes = "multipart/form-data")
	@Operation(summary = "upload photo into album")
	@SecurityRequirement(name ="studyeasy-demo-api")
	public ResponseEntity<List<HashMap<String, List<String>>>> photos(@RequestPart(required = true) MultipartFile[] files,
			@PathVariable long album_id, Authentication authentication) {
		String email = authentication.getName();
		Optional<Account> optionalAccount = accountService.findByEmail(email);
		Account account = optionalAccount.get();
		Optional<Album> optionaAlbum = albumService.findById(album_id);
		Album album;
		if (optionaAlbum.isPresent()) {
			album = optionaAlbum.get();
			if (account.getId() != album.getAccount().getId()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		List<String> fileNamesWithSuccess = new ArrayList<>();
		List<String> fileNamesWithError = new ArrayList<>();

		Arrays.asList(files).stream().forEach(file -> {
			String contentType = file.getContentType();
			if (contentType.equals("image/png")
					|| contentType.equals("image/jpg")
					|| contentType.equals("image/jpeg")) {
				fileNamesWithSuccess.add(file.getOriginalFilename());

				int length = 10;
				boolean useLetters = true;
				boolean useNumbers = true;

				try {
					String fileName = file.getOriginalFilename();
					String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
					String final_photo_name = generatedString + fileName;
					String absolute_fileLocation = AppUtil.get_photo_upload_path(final_photo_name, PHOTOS_FOLDER_NAME, album_id);
					Path path = Paths.get(absolute_fileLocation);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					Photo photo = new Photo();
					photo.setName(fileName);
					photo.setFileName(final_photo_name);
					photo.setOriginalFileName(fileName);
					photo.setAlbum(album);
					photoService.save(photo);

					BufferedImage thumbImg = AppUtil.getThumbnail(file, THUMBNAIL_WIDTH);
					File thumbnail_location = new File(
							AppUtil.get_photo_upload_path(final_photo_name, THUMBNAIL_FOLDER_NAME, album_id));
					ImageIO.write(thumbImg, file.getContentType().split("/")[1], thumbnail_location);

				} catch (Exception e) {
					log.debug(AlbumError.PHOTO_UPLOAD_ERROR.toString() + ": " + e.getMessage());
					fileNamesWithError.add(file.getOriginalFilename());
				}

			} else {
				fileNamesWithError.add(file.getOriginalFilename());
			}
		});

		HashMap<String, List<String>> result = new HashMap<>();
		result.put("SUCCESS", fileNamesWithSuccess);
		result.put("ERRORS", fileNamesWithError);

		List<HashMap<String, List<String>>> response = new ArrayList<>();
		response.add(result);

		return ResponseEntity.ok(response);

	}

	@GetMapping("albums/{album_id}/photos/{photo_id}/download-photos")
	@Operation(summary = "download photo from album")
	@SecurityRequirement(name ="studyeasy-demo-api")
	public ResponseEntity<?> downloadPhoto(@PathVariable("album_id") long album_id,
			@PathVariable("photo_id") long photo_id, Authentication authentication) {

			String email = authentication.getName();
			Optional<Account> optionalAccount = accountService.findByEmail(email);
			Account account = optionalAccount.get();
			Optional<Album> optionaAlbum = albumService.findById(album_id);
			Album album;
			if (optionaAlbum.isPresent()) {
				album = optionaAlbum.get();
				if (account.getId() != album.getAccount().getId()) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

			Optional<Photo> optionalPhoto = photoService.findById(photo_id);
			if (optionalPhoto.isPresent()) {
				Photo photo = optionalPhoto.get();
				Resource resource = null;
				try {
					resource = AppUtil.getFileAsResource(album_id, PHOTOS_FOLDER_NAME, photo.getFileName());
				} catch (Exception e) {
					return ResponseEntity.internalServerError().build();
				}

				if (resource == null) {
					return new ResponseEntity<>("file not found", HttpStatus.NOT_FOUND);
				}

				String contentType = "application/octet-stream";
				String headerValue = "attachment; filename=\"" + photo.getOriginalFileName() + "\"";

				return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
						.body(resource);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

	}

}
