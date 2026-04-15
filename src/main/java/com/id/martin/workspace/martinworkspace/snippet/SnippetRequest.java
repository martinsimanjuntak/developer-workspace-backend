package com.id.martin.workspace.martinworkspace.snippet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SnippetRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 4000) String content,
        @NotNull SnippetCategory category
) {
}
