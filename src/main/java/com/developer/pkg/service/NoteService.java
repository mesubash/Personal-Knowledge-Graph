package com.developer.pkg.service;

import com.developer.pkg.entity.Note;
import com.developer.pkg.entity.Tag;
import com.developer.pkg.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public Note findById(UUID id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Note> findAll() {
        return noteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Note> findByTagName(String tagName) {
        return noteRepository.findByTags_Name(tagName.toLowerCase());
    }

    public Note createNote(String title, String content, Set<String> tagNames) {
        log.info("Creating note with title: {}", title);

        Note note = Note.builder()
                .title(title)
                .content(content)
                .build();

        if (tagNames != null && !tagNames.isEmpty()) {
            Set<Tag> tags = tagNames.stream()
                    .map(tagService::findOrCreateTag)
                    .collect(Collectors.toSet());

            tags.forEach(note::addTag);
        }

        return noteRepository.save(note);
    }

    public Note updateNote(UUID id, String title, String content) {
        log.info("Updating note with id: {}", id);

        Note note = findById(id);

        if (title != null) {
            note.setTitle(title);
        }
        if (content != null) {
            note.setContent(content);
        }

        return noteRepository.save(note);
    }

    public boolean deleteNote(UUID id) {
        log.info("Deleting note with id: {}", id);

        Note note = findById(id);

        note.getTags().clear();
        note.getReferencesTo().forEach(ref -> ref.getReferencedBy().remove(note));
        note.getReferencedBy().forEach(ref -> ref.getReferencesTo().remove(note));

        noteRepository.delete(note);
        return true;
    }

    public Note addReference(UUID fromNoteId, UUID toNoteId) {
        log.info("Adding reference from {} to {}", fromNoteId, toNoteId);

        if (fromNoteId.equals(toNoteId)) {
            throw new RuntimeException("Cannot reference self");
        }

        Note fromNote = findById(fromNoteId);
        Note toNote = findById(toNoteId);

        fromNote.addReference(toNote);

        return noteRepository.save(fromNote);
    }

    public Note removeReference(UUID fromNoteId, UUID toNoteId) {
        log.info("Removing reference from {} to {}", fromNoteId, toNoteId);

        Note fromNote = findById(fromNoteId);
        Note toNote = findById(toNoteId);

        fromNote.removeReference(toNote);

        return noteRepository.save(fromNote);
    }

    public Note addTag(UUID noteId, String tagName) {
        log.info("Adding tag '{}' to note {}", tagName, noteId);

        Note note = findById(noteId);
        Tag tag = tagService.findOrCreateTag(tagName);

        note.addTag(tag);

        return noteRepository.save(note);
    }

    public Note removeTag(UUID noteId, String tagName) {
        log.info("Removing tag '{}' from note {}", tagName, noteId);

        Note note = findById(noteId);
        Tag tag = tagService.findByName(tagName);

        note.removeTag(tag);

        return noteRepository.save(note);
    }
}
