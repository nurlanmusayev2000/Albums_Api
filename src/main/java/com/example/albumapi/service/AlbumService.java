package com.example.security_sample.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.security_sample.model.Album;
import com.example.security_sample.repository.AlbumRepository;

@Service
public class AlbumService {

	@Autowired
	private AlbumRepository albumRepository;

	public List<Album> findByAccount(long id) {
		return albumRepository.findByAccount_id(id);
	}

	public Album save(Album album) {
		return albumRepository.save(album);
	}

	public Optional<Album> findById(long id){
		return albumRepository.findById(id);
	}
}
