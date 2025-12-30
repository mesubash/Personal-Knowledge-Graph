package com.developer.pkg.controller;

import com.developer.pkg.entity.Note;
import com.developer.pkg.service.TagService;
import com.developer.pkg.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @QueryMapping
    public Tag tag(@Argument String name) {
        return tagService.findByName(name);
    }

    @QueryMapping
    public List<Tag> tags() {
        return tagService.findAll();
    }

    @SchemaMapping(typeName = "Tag", field = "notes")
    public Set<Note> notes(Tag tag) {
        return tag.getNotes();
    }
}
