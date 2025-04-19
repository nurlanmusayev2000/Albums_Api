package com.example.security_sample.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.security_sample.model.Album;
import java.util.List;


@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
	List<Album> findByAccount_id(long id);
}
