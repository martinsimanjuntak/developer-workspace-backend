package com.id.martin.workspace.martinworkspace.worklog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record WorkLogRequest(
        @NotNull LocalDate logDate,
        @NotBlank @Size(max = 2000) String note
) {
}
