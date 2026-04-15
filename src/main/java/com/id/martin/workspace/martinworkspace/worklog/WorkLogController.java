package com.id.martin.workspace.martinworkspace.worklog;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks/{taskId}/work-logs")
public class WorkLogController {

    private final WorkLogService workLogService;

    public WorkLogController(WorkLogService workLogService) {
        this.workLogService = workLogService;
    }

    @PostMapping
    public ResponseEntity<WorkLogResponse> create(
            @PathVariable Long taskId,
            @Valid @RequestBody WorkLogRequest request
    ) {
        WorkLogResponse response = workLogService.create(taskId, request);
        return ResponseEntity.created(URI.create("/api/tasks/" + taskId + "/work-logs/" + response.id())).body(response);
    }

    @GetMapping
    public List<WorkLogResponse> findByTaskId(@PathVariable Long taskId) {
        return workLogService.findByTaskId(taskId);
    }

    @PutMapping("/{workLogId}")
    public WorkLogResponse update(
            @PathVariable Long taskId,
            @PathVariable Long workLogId,
            @Valid @RequestBody WorkLogRequest request
    ) {
        return workLogService.update(taskId, workLogId, request);
    }

    @DeleteMapping("/{workLogId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @PathVariable Long workLogId) {
        workLogService.delete(taskId, workLogId);
        return ResponseEntity.noContent().build();
    }
}
