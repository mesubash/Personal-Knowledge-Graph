package com.developer.pkg.controller;

import com.developer.pkg.dto.CreateNoteInput;
import com.developer.pkg.dto.UpdateNoteInput;
import com.developer.pkg.entity.Note;
import com.developer.pkg.entity.Tag;
import com.developer.pkg.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @QueryMapping
    public Note note(@Argument UUID id) {
        return noteService.findById(id);
    }

    @QueryMapping
    public List<Note> notes() {
        return noteService.findAll();
    }

    @QueryMapping
    public List<Note> notesByTag(@Argument String tagName) {
        return noteService.findByTagName(tagName);
    }

    @MutationMapping
    public Note createNote(@Argument CreateNoteInput input) {
        return noteService.createNote(
                input.title(),
                input.content(),
                input.tagNames()
        );
    }

    @MutationMapping
    public Note updateNote(@Argument UUID id, @Argument UpdateNoteInput input) {
        return noteService.updateNote(
                id,
                input.title(),
                input.content()
        );
    }

    @MutationMapping
    public Boolean deleteNote(@Argument UUID id) {
        return noteService.deleteNote(id);
    }

    @MutationMapping
    public Note addReference(@Argument UUID fromNoteId, @Argument UUID toNoteId) {
        return noteService.addReference(fromNoteId, toNoteId);
    }

    @MutationMapping
    public Note removeReference(@Argument UUID fromNoteId, @Argument UUID toNoteId) {
        return noteService.removeReference(fromNoteId, toNoteId);
    }

    @MutationMapping
    public Note addTag(@Argument UUID noteId, @Argument String tagName) {
        return noteService.addTag(noteId, tagName);
    }

    @MutationMapping
    public Note removeTag(@Argument UUID noteId, @Argument String tagName) {
        return noteService.removeTag(noteId, tagName);
    }

    @SchemaMapping(typeName = "Note", field = "tags")
    public Set<Tag> tags(Note note) {
        return note.getTags();
    }

    @SchemaMapping(typeName = "Note", field = "referencesTo")
    public Set<Note> referencesTo(Note note) {
        return note.getReferencesTo();
    }

    @SchemaMapping(typeName = "Note", field = "referencedBy")
    public Set<Note> referencedBy(Note note) {
        return note.getReferencedBy();
    }
}
