package com.id.martin.workspace.martinworkspace.task;

import com.id.martin.workspace.martinworkspace.worklog.WorkLogResponse;
import java.time.Instant;
import java.util.List;

public record TaskDetailResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt,
        List<WorkLogResponse> workLogs
) {
}
