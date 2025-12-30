package com.developer.pkg.service;

import com.developer.pkg.entity.Tag;
import com.developer.pkg.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + name));
    }

    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag findOrCreateTag(String name) {
        String normalizedName = name.toLowerCase().trim();

        return tagRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> {
                    log.info("Creating new tag: {}", normalizedName);
                    Tag tag = Tag.builder()
                            .name(normalizedName)
                            .build();
                    return tagRepository.save(tag);
                });
    }
}