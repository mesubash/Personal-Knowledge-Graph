package com.developer.pkg.repository;

import com.developer.pkg.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @EntityGraph(attributePaths = {"tags", "referencesTo", "referencedBy"})
    List<Note> findByTags_NameIgnoreCase(String tagName);

    @EntityGraph(attributePaths = {"tags", "referencesTo", "referencedBy"})
    @Query("SELECT n FROM Note n WHERE n.id = :id")
    Optional<Note> findByIdWithDetails(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"tags", "referencesTo", "referencedBy"})
    @Query("SELECT DISTINCT n FROM Note n")
    List<Note> findAllWithDetails();
}
