package com.id.martin.workspace.martinworkspace.task;

import java.time.Instant;

public record TaskSummaryResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
