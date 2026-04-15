package com.id.martin.workspace.martinworkspace.task;

import com.id.martin.workspace.martinworkspace.common.ResourceNotFoundException;
import com.id.martin.workspace.martinworkspace.worklog.WorkLogResponse;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskSummaryResponse create(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(request.description().trim());
        task.setStatus(TaskStatus.TODO);
        return toSummary(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskSummaryResponse> findAll() {
        return taskRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getUpdatedAt).reversed())
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskDetailResponse findById(Long taskId) {
        Task task = getTask(taskId);
        List<WorkLogResponse> workLogs = task.getWorkLogs()
                .stream()
                .sorted(Comparator.comparing(workLog -> workLog.getCreatedAt(), Comparator.reverseOrder()))
                .map(WorkLogResponse::from)
                .toList();
        return new TaskDetailResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                workLogs
        );
    }

    public TaskSummaryResponse update(Long taskId, TaskRequest request) {
        Task task = getTask(taskId);
        task.setTitle(request.title().trim());
        task.setDescription(request.description().trim());
        task.touch();
        return toSummary(task);
    }

    public TaskSummaryResponse updateStatus(Long taskId, TaskStatusUpdateRequest request) {
        Task task = getTask(taskId);
        task.setStatus(request.status());
        task.touch();
        return toSummary(task);
    }

    public void delete(Long taskId) {
        Task task = getTask(taskId);
        taskRepository.delete(task);
    }

    public Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " was not found"));
    }

    private TaskSummaryResponse toSummary(Task task) {
        return new TaskSummaryResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
