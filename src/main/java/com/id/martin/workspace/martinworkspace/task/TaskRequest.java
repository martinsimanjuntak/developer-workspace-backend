package com.id.martin.workspace.martinworkspace.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 2000) String description
) {
}
