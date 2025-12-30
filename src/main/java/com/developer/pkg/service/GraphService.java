package com.developer.pkg.service;


import com.developer.pkg.dto.GraphData;
import com.developer.pkg.entity.Note;
import com.developer.pkg.entity.Tag;
import com.developer.pkg.repository.NoteRepository;
import com.developer.pkg.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GraphService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    public GraphData getGraphData() {
        log.info("Building graph data for visualization");

        List<Note> notes = noteRepository.findAll();
        List<Tag> tags = tagRepository.findAll();

        List<GraphData.NodeData> nodes = new ArrayList<>();
        List<GraphData.LinkData> links = new ArrayList<>();

        // Add note nodes
        notes.forEach(note -> {
            nodes.add(GraphData.NodeData.builder()
                    .id(note.getId())
                    .label(note.getTitle())
                    .type("note")
                    .content(note.getContent())
                    .tags(note.getTags().stream()
                            .map(Tag::getName)
                            .collect(Collectors.toList()))
                    .referenceCount(note.getReferencesTo().size())
                    .build());

            // Add note-to-tag links
            note.getTags().forEach(tag -> {
                links.add(GraphData.LinkData.builder()
                        .source(note.getId())
                        .target(tag.getId())
                        .type("has-tag")
                        .build());
            });

            // Add note-to-note reference links
            note.getReferencesTo().forEach(ref -> {
                links.add(GraphData.LinkData.builder()
                        .source(note.getId())
                        .target(ref.getId())
                        .type("references")
                        .build());
            });
        });

        // Add tag nodes
        tags.forEach(tag -> {
            nodes.add(GraphData.NodeData.builder()
                    .id(tag.getId())
                    .label(tag.getName())
                    .type("tag")
                    .build());
        });

        // Calculate statistics
        long referenceCount = links.stream()
                .filter(link -> "references".equals(link.getType()))
                .count();

        GraphData.Statistics stats = GraphData.Statistics.builder()
                .noteCount(notes.size())
                .tagCount(tags.size())
                .referenceCount(referenceCount)
                .build();

        return GraphData.builder()
                .nodes(nodes)
                .links(links)
                .stats(stats)
                .build();
    }
}
