package com.example.security_sample.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.security_sample.model.Photo;
import com.example.security_sample.repository.PhotoRepository;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }

    public Optional<Photo> findById(long id) {
        return photoRepository.findById(id);
    }

}
