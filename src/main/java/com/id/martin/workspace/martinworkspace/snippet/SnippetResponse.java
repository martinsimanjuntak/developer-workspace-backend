package com.id.martin.workspace.martinworkspace.snippet;

import java.time.Instant;

public record SnippetResponse(
        Long id,
        String title,
        String content,
        SnippetCategory category,
        Instant createdAt,
        Instant updatedAt
) {
}
