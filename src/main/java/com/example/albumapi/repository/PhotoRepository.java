package com.example.security_sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.security_sample.model.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {


}
