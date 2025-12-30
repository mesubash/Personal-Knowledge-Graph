package com.developer.pkg.repository;

import com.developer.pkg.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findByTags_Name(String tagName);

    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.tags WHERE n.id = :id")
    Optional<Note> findByIdWithTags(@Param("id") UUID id);

    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.referencesTo WHERE n.id = :id")
    Optional<Note> findByIdWithReferences(@Param("id") UUID id);

    @Query("SELECT DISTINCT n FROM Note n LEFT JOIN FETCH n.tags")
    List<Note> findAllWithTags();
}

