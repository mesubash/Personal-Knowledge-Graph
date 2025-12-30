package com.developer.pkg.dto;

import jakarta.validation.constraints.Size;

public record UpdateNoteInput(
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title,

        String content
) {}

