package com.id.martin.workspace.martinworkspace.worklog;

import java.time.Instant;
import java.time.LocalDate;

public record WorkLogResponse(
        Long id,
        Long taskId,
        LocalDate logDate,
        String note,
        Instant createdAt,
        Instant updatedAt
) {
    public static WorkLogResponse from(WorkLog workLog) {
        return new WorkLogResponse(
                workLog.getId(),
                workLog.getTask().getId(),
                workLog.getLogDate(),
                workLog.getNote(),
                workLog.getCreatedAt(),
                workLog.getUpdatedAt()
        );
    }
}
